package br.unibh.gestar.interfaces;

import br.unibh.gestar.interfaces.controller.TriageController;
import br.unibh.gestar.interfaces.routes.HealthRoutes;
import br.unibh.gestar.interfaces.routes.PatientRoutes;
import br.unibh.gestar.interfaces.routes.TriageRoutes;
import br.unibh.gestar.service.MedicalCareNotFoundException;
import br.unibh.gestar.service.PatientService;
import br.unibh.gestar.service.TriageService;

import io.javalin.Javalin;

import java.util.Map;

public class ApiServer {
    private final TriageService service;
    private final PatientService patientService;
    private Javalin app;

    public ApiServer(TriageService service, PatientService patientService) {
        this.service = service;
        this.patientService = patientService;
    }

    public Javalin start(int port) {
        app = Javalin.create();

        HealthRoutes.register(app);
        PatientRoutes.register(app, patientService);
        TriageRoutes.register(app, service);

        app.exception(MedicalCareNotFoundException.class, (e, ctx) -> ctx.status(404).json(Map.of("error", e.getMessage())));
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
