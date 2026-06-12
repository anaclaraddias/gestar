package br.unibh.gestar.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.unibh.gestar.domain.Patient;

public class PatientRepository {
    public void create(Patient patient) throws SQLException {
        String query = """
            INSERT INTO patient (
                id, name, birth_date
            ) 
            VALUES (?, ?, ?)
        """;

        try (Connection conn = PostgresConnection.connect();
            PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, patient.getId());
            ps.setString(2, patient.getName());
            ps.setDate(3, java.sql.Date.valueOf(patient.getBirthDate()));
            ps.executeUpdate();
        }
    }
}
