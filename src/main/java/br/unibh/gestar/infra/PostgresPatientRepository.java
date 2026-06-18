package br.unibh.gestar.infra;

import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.repository.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresPatientRepository implements PatientRepository {
    @Override
    public void save(Patient patient) {
        String query = """
            INSERT INTO patient (id, name, birth_date)
            VALUES (?, ?, ?)
        """;

        try (
            Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, patient.getId());
            ps.setString(2, patient.getName());
            ps.setDate(3, patient.getBirthDate() == null ? null : Date.valueOf(patient.getBirthDate()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save patient " + patient.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Patient patient) {
        String query = """
            UPDATE patient
            SET name = ?, birth_date = ?
            WHERE id = ?
        """;

        try (
            Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, patient.getName());
            ps.setDate(2, patient.getBirthDate() == null ? null : Date.valueOf(patient.getBirthDate()));
            ps.setString(3, patient.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new IllegalArgumentException("Patient not found for update: " + patient.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient " + patient.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findByNameAndBirthDate(String name, LocalDate birthDate) {
        String query = """
            SELECT *
            FROM patient
            WHERE name = ? AND birth_date IS NOT DISTINCT FROM ?
            LIMIT 1
        """;

        try (
            Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, name);
            ps.setDate(2, birthDate == null ? null : Date.valueOf(birthDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find patient " + name + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> listAll() {
        String query = """
            SELECT id, name, birth_date
            FROM patient
            ORDER BY name, birth_date
        """;

        List<Patient> patients = new ArrayList<>();

        try (
            Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list patients: " + e.getMessage(), e);
        }

        return patients;
    }

    private static Patient mapRow(ResultSet rs) throws SQLException {
        Date birthDate = rs.getDate("birth_date");
        return Patient.fromPersistence(
            rs.getString("id"),
            rs.getString("name"),
            birthDate == null ? null : birthDate.toLocalDate()
        );
    }
}
