package br.unibh.gestar.interfaces.routes;

import br.unibh.gestar.interfaces.controller.MedicalCareController;
import br.unibh.gestar.service.MedicalCareService;

import io.javalin.Javalin;

public final class MedicalCareRoutes {
    public static void register(Javalin app, MedicalCareService service) {
        MedicalCareController controller = new MedicalCareController(service);
        app.post("/medical-care", controller::create);
        app.get("/medical-care", controller::list);
        app.get("/medical-care/{id}", controller::getOne);
        app.patch("/medical-care/{id}", controller::update);
        app.post("/referral", controller::refer);
        app.get("/queue", controller::queue);
        app.post("/queue/call", controller::callNext);
    }
}
