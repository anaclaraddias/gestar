package br.unibh.gestar.infra;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.MedicalCareStatus;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.repository.MedicalCareRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PostgresMedicalCareRepository implements MedicalCareRepository {

    private final Map<String, MedicalCare> identityMap = new LinkedHashMap<>();

    private static final String UPSERT_PATIENT = """
            INSERT INTO patient (id, name, birth_date)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO NOTHING
            """;

    private static final String UPSERT_CARE = """
            INSERT INTO medical_care (
                id, patient_id, main_complaint, priority_category, arrival_date_time,
                urgency_level, status, referral_reason, destination_unit,
                systolic, diastolic, heart_rate, respiratory_rate, temperature,
                oxygen_saturation, pain_scale
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                patient_id = EXCLUDED.patient_id,
                main_complaint = EXCLUDED.main_complaint,
                priority_category = EXCLUDED.priority_category,
                arrival_date_time = EXCLUDED.arrival_date_time,
                urgency_level = EXCLUDED.urgency_level,
                status = EXCLUDED.status,
                referral_reason = EXCLUDED.referral_reason,
                destination_unit = EXCLUDED.destination_unit,
                systolic = EXCLUDED.systolic,
                diastolic = EXCLUDED.diastolic,
                heart_rate = EXCLUDED.heart_rate,
                respiratory_rate = EXCLUDED.respiratory_rate,
                temperature = EXCLUDED.temperature,
                oxygen_saturation = EXCLUDED.oxygen_saturation,
                pain_scale = EXCLUDED.pain_scale
            """;

    private static final String SELECT_BASE = """
            SELECT mc.id, mc.main_complaint, mc.priority_category, mc.arrival_date_time,
                   mc.urgency_level, mc.status, mc.referral_reason, mc.destination_unit,
                   mc.systolic, mc.diastolic, mc.heart_rate, mc.respiratory_rate,
                   mc.temperature, mc.oxygen_saturation, mc.pain_scale,
                   p.id AS patient_id, p.name AS patient_name, p.birth_date AS patient_birth_date
            FROM medical_care mc
            JOIN patient p ON p.id = mc.patient_id
            """;

    @Override
    public void save(MedicalCare medicalCare) {
        try (Connection conn = PostgresConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                upsertPatient(conn, medicalCare.getPatient());
                upsertCare(conn, medicalCare);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save medical care " + medicalCare.getId() + ": " + e.getMessage(), e);
        }
        identityMap.put(medicalCare.getId(), medicalCare);
    }

    @Override
    public Optional<MedicalCare> findById(String id) {
        MedicalCare cached = identityMap.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        try (Connection conn = PostgresConnection.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_BASE + " WHERE mc.id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                MedicalCare care = mapRow(rs);
                identityMap.put(care.getId(), care);
                return Optional.of(care);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load medical care " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<MedicalCare> listAll() {
        List<MedicalCare> result = new ArrayList<>();
        try (Connection conn = PostgresConnection.connect();
             PreparedStatement ps = conn.prepareStatement(SELECT_BASE + " ORDER BY mc.arrival_date_time");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                MedicalCare care = identityMap.get(id);
                if (care == null) {
                    care = mapRow(rs);
                    identityMap.put(id, care);
                }
                result.add(care);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list medical care: " + e.getMessage(), e);
        }
        return result;
    }

    private void upsertPatient(Connection conn, Patient patient) throws SQLException {
        if (patient == null) {
            throw new SQLException("Medical care has no patient to persist");
        }
        try (PreparedStatement ps = conn.prepareStatement(UPSERT_PATIENT)) {
            ps.setString(1, patient.getId());
            ps.setString(2, patient.getName());
            ps.setDate(3, patient.getBirthDate() == null ? null : Date.valueOf(patient.getBirthDate()));
            ps.executeUpdate();
        }
    }

    private void upsertCare(Connection conn, MedicalCare care) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPSERT_CARE)) {
            ps.setString(1, care.getId());
            ps.setString(2, care.getPatient().getId());
            ps.setString(3, care.getMainComplaint());
            ps.setString(4, care.getPriorityCategory() == null ? null : care.getPriorityCategory().name());
            ps.setTimestamp(5, Timestamp.valueOf(care.getArrivalDateTime()));
            ps.setString(6, care.getUrgencyLevel() == null ? null : care.getUrgencyLevel().name());
            ps.setString(7, care.getStatus() == null ? null : care.getStatus().name());
            ps.setString(8, care.getReferralReason());
            ps.setString(9, care.getDestinationUnit());

            VitalSigns v = care.getVitalSigns();
            if (v == null) {
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.INTEGER);
                ps.setNull(12, Types.INTEGER);
                ps.setNull(13, Types.INTEGER);
                ps.setNull(14, Types.DOUBLE);
                ps.setNull(15, Types.INTEGER);
                ps.setNull(16, Types.INTEGER);
            } else {
                ps.setInt(10, v.getSystolicPressure());
                ps.setInt(11, v.getDiastolicPressure());
                ps.setInt(12, v.getHeartRate());
                ps.setInt(13, v.getRespiratoryRate());
                ps.setDouble(14, v.getTemperature());
                ps.setInt(15, v.getOxygenSaturation());
                ps.setInt(16, v.getPainScale());
            }
            ps.executeUpdate();
        }
    }

    private MedicalCare mapRow(ResultSet rs) throws SQLException {
        Date birthDate = rs.getDate("patient_birth_date");
        Patient patient = Patient.fromPersistence(
                rs.getString("patient_id"),
                rs.getString("patient_name"),
                birthDate == null ? null : birthDate.toLocalDate());

        VitalSigns vitals = null;
        if (rs.getObject("systolic") != null) {
            vitals = new VitalSigns(
                    rs.getInt("systolic"),
                    rs.getInt("diastolic"),
                    rs.getInt("heart_rate"),
                    rs.getInt("respiratory_rate"),
                    rs.getDouble("temperature"),
                    rs.getInt("oxygen_saturation"),
                    rs.getInt("pain_scale"));
        }

        String urgency = rs.getString("urgency_level");
        return MedicalCare.fromPersistence(
                rs.getString("id"),
                patient,
                rs.getString("main_complaint"),
                PriorityCategory.valueOf(rs.getString("priority_category")),
                rs.getTimestamp("arrival_date_time").toLocalDateTime(),
                vitals,
                urgency == null ? null : UrgencyLevel.valueOf(urgency),
                MedicalCareStatus.valueOf(rs.getString("status")),
                rs.getString("referral_reason"),
                rs.getString("destination_unit"));
    }
}
