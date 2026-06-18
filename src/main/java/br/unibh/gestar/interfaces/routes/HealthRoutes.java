package br.unibh.gestar.interfaces.routes;

import io.javalin.Javalin;
import br.unibh.gestar.infra.PostgresConnection;

import java.sql.Connection;
import java.util.Map;

public class HealthRoutes {
    public static void register(Javalin app) {
        app.get("/health", ctx -> {
            boolean isConnected = isDatabaseConnected();
            int statusCode = isConnected ? 200 : 503;

            Map<String, Object> response = Map.of(
                "database_status", isConnected ? "UP" : "DOWN"
            );
            ctx.status(statusCode).json(response);
        });
    }

    private static boolean isDatabaseConnected() {
        try (Connection conn = PostgresConnection.connect()) {
            return conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
