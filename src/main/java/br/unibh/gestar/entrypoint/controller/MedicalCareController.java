package br.unibh.gestar.entrypoint.controller;

import br.unibh.gestar.entrypoint.dto.MedicalCareRequest;
import br.unibh.gestar.entrypoint.dto.MedicalCareResponse;
import br.unibh.gestar.service.MedicalCareService;

import io.javalin.http.Context;

import java.util.Map;

public class MedicalCareController {
    private final MedicalCareService service;

    public MedicalCareController(MedicalCareService service) {
        this.service = service;
    }

    public void create(Context ctx) {
        MedicalCareRequest req = body(ctx);
        MedicalCareResponse care = service.create(req);
        
        ctx.status(201).json(care);
    }

    public void refer(Context ctx) {
        MedicalCareRequest req = body(ctx);
        MedicalCareResponse care = service.refer(req);
        
        ctx.status(201).json(care);
    }

    public void list(Context ctx) {
        ctx.json(service.listResponses());
    }

    public void getOne(Context ctx) {
        ctx.json(service.findResponse(ctx.pathParam("id")));
    }

    public void update(Context ctx) {
        String id = ctx.pathParam("id");
        MedicalCareRequest req = body(ctx);
        
        ctx.json(service.update(id, req));
    }

    public void queue(Context ctx) {
        ctx.json(service.toQueueResponse(service.queueStatus()));
    }

    public void callNext(Context ctx) {
        service.callNext().ifPresentOrElse(
            care -> ctx.json(care),
            () -> ctx.json(Map.of("message", "Queue is empty"))
        );
    }

    private static MedicalCareRequest body(Context ctx) {
        try {
            return ctx.bodyAsClass(MedicalCareRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON request body");
        }
    }
}
