package br.unibh.gestar.entrypoint.routes;

import br.unibh.gestar.entrypoint.controller.PatientController;
import br.unibh.gestar.service.PatientService;

import io.javalin.Javalin;

public final class PatientRoutes {
    public static void register(Javalin app, PatientService service) {
        PatientController controller = new PatientController(service);
        app.post("/patient", controller::create);
        app.get("/patients", controller::list);
    }
}
