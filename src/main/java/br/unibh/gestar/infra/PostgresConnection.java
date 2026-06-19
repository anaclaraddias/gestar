package br.unibh.gestar.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {
    private static final String URL  = "jdbc:postgresql://localhost:5432/gestar_db";
    private static final String USER = "admin";
    private static final String PASS = "1234";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
