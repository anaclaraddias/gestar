package br.unibh.gestar.interfaces.controller;

import br.unibh.gestar.interfaces.dto.CareRequest;
import br.unibh.gestar.interfaces.dto.CareResponse;
import br.unibh.gestar.service.TriageService;

import io.javalin.http.Context;

import java.util.Map;

public class TriageController {
    private final TriageService service;

    public TriageController(TriageService service) {
        this.service = service;
    }

    public void create(Context ctx) {
        CareRequest req = body(ctx);
        CareResponse care = service.performTriage(req);
        
        ctx.status(201).json(care);
    }

    public void refer(Context ctx) {
        CareRequest req = body(ctx);
        CareResponse care = service.refer(req);
        
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
        CareRequest req = body(ctx);
        
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

    private static CareRequest body(Context ctx) {
        try {
            return ctx.bodyAsClass(CareRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON request body");
        }
    }
}
