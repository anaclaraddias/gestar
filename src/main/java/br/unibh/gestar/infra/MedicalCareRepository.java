package br.unibh.gestar.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import br.unibh.gestar.domain.MedicalCare;

public class MedicalCareRepository {
    public void create(MedicalCare medicalCare) throws SQLException {
        String query = """
            INSERT INTO medical_care (
                id,
                patient_id,
                main_complaint,
                priority_category,
                arrival_date_time,
                urgency_level,
                status,
                referral_reason,
                destination_unit
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, medicalCare.getId());
            ps.setString(2, medicalCare.getPaciente().getId());
            ps.setString(3, medicalCare.getMainComplaint());
            ps.setString(4, medicalCare.getPriorityCategory().name());
            ps.setTimestamp(5, Timestamp.valueOf(medicalCare.getArrivalDateTime()));
            ps.setString(6, medicalCare.getUrgencyLevel() != null ? medicalCare.getUrgencyLevel().name() : null);
            ps.setString(7, medicalCare.getStatus().name());
            ps.setString(8, medicalCare.getReferralReason());
            ps.setString(9, medicalCare.getDestinationUnit());
            ps.executeUpdate();
        }
    }
}
