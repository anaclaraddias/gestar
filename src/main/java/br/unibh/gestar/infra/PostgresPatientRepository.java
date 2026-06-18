package br.unibh.gestar.infra;

import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.repository.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgresPatientRepository implements PatientRepository {
    @Override
    public void save(Patient patient) {
        upsertPatient(patient);
    }

    private void upsertPatient(Patient patient) {
        String query = """
            INSERT INTO patient (id, name, birth_date)
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                birth_date = EXCLUDED.birth_date
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
}
