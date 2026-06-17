package br.unibh.gestar.interfaces;

import br.unibh.gestar.interfaces.controller.TriageController;
import br.unibh.gestar.service.MedicalCareNotFoundException;
import br.unibh.gestar.service.TriageService;

import io.javalin.Javalin;

import java.util.Map;

public class ApiServer {

    private final TriageService service;
    private Javalin app;

    public ApiServer(TriageService service) {
        this.service = service;
    }

    public Javalin start(int port) {
        app = Javalin.create();
        new TriageController(service).register(app);

        app.exception(MedicalCareNotFoundException.class,
                (e, ctx) -> ctx.status(404).json(Map.of("error", e.getMessage())));
        app.exception(IllegalArgumentException.class,
                (e, ctx) -> ctx.status(400).json(Map.of("error", e.getMessage())));

        app.start(port);
        return app;
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
