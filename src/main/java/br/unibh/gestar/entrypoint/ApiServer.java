package br.unibh.gestar.entrypoint;

import br.unibh.gestar.service.PatientService;
import br.unibh.gestar.entrypoint.routes.HealthRoutes;
import br.unibh.gestar.entrypoint.routes.MedicalCareRoutes;
import br.unibh.gestar.entrypoint.routes.PatientRoutes;
import br.unibh.gestar.service.MedicalCareService;

import io.javalin.Javalin;

import java.util.Map;

public class ApiServer {
    private final MedicalCareService service;
    private final PatientService patientService;
    private Javalin app;

    public ApiServer(MedicalCareService service, PatientService patientService) {
        this.service = service;
        this.patientService = patientService;
    }

    public Javalin start(int port) {
        app = Javalin.create();

        HealthRoutes.register(app);
        PatientRoutes.register(app, patientService);
        MedicalCareRoutes.register(app, service);

        app.exception(MedicalCareService.MedicalCareNotFoundException.class, (e, ctx) -> ctx.status(404).json(Map.of("error", e.getMessage())));
        app.exception(IllegalArgumentException.class, (e, ctx) -> ctx.status(400).json(Map.of("error", e.getMessage())));

        app.start(port);
        return app;
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
