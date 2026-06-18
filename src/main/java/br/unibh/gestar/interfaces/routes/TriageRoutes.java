package br.unibh.gestar.interfaces.routes;

import br.unibh.gestar.interfaces.controller.TriageController;
import br.unibh.gestar.service.TriageService;

import io.javalin.Javalin;

public final class TriageRoutes {
    public static void register(Javalin app, TriageService service) {
        TriageController controller = new TriageController(service);
        app.post("/triage", controller::create);
        app.get("/triage", controller::list);
        app.get("/triage/{id}", controller::getOne);
        app.patch("/triage/{id}", controller::update);
        app.post("/referral", controller::refer);
        app.get("/queue", controller::queue);
        app.post("/queue/call", controller::callNext);
    }
}
