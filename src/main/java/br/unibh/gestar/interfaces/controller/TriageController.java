package br.unibh.gestar.interfaces.controller;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.interfaces.dto.CareRequest;
import br.unibh.gestar.interfaces.mapper.CareMapper;
import br.unibh.gestar.service.TriageService;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class TriageController {

    private final TriageService service;

    public TriageController(TriageService service) {
        this.service = service;
    }

    public void register(Javalin app) {
        app.post("/triages", this::create);
        app.get("/triages", this::list);
        app.get("/triages/{id}", this::getOne);
        app.patch("/triages/{id}", this::update);
        app.post("/referrals", this::refer);
        app.get("/queue", this::queue);
        app.post("/queue/calls", this::callNext);
    }

    private void create(Context ctx) {
        CareRequest req = body(ctx);
        MedicalCare care = service.performTriage(
                CareMapper.toPatient(req), CareMapper.toVitalSigns(req), req.complaint(), CareMapper.toCategory(req));
        ctx.status(201).json(CareMapper.toResponse(care));
    }

    private void refer(Context ctx) {
        CareRequest req = body(ctx);
        MedicalCare care = service.refer(
                CareMapper.toPatient(req), CareMapper.toVitalSigns(req), req.complaint(), CareMapper.toCategory(req),
                req.referralReason(), req.destinationUnit());
        ctx.status(201).json(CareMapper.toResponse(care));
    }

    private void list(Context ctx) {
        ctx.json(service.listAll().stream().map(CareMapper::toResponse).toList());
    }

    private void getOne(Context ctx) {
        ctx.json(CareMapper.toResponse(service.findById(ctx.pathParam("id"))));
    }

    private void update(Context ctx) {
        String id = ctx.pathParam("id");
        CareRequest req = body(ctx);
        MedicalCare care;
        if (req.status() != null && !req.status().isBlank()) {
            if (!"FINISHED".equalsIgnoreCase(req.status().trim())) {
                throw new IllegalArgumentException("Unsupported status transition '" + req.status()
                        + "'. Only FINISHED is allowed via PATCH.");
            }
            care = service.finish(id);
        } else if (CareMapper.hasVitalSigns(req)) {
            care = service.reclassify(id, CareMapper.toVitalSigns(req));
        } else {
            throw new IllegalArgumentException(
                    "Nothing to update: send new vital signs (reclassify) or \"status\":\"FINISHED\".");
        }
        ctx.json(CareMapper.toResponse(care));
    }

    private void queue(Context ctx) {
        ctx.json(CareMapper.toQueueResponse(service.queueStatus()));
    }

    private void callNext(Context ctx) {
        service.callNext().ifPresentOrElse(
                care -> ctx.json(CareMapper.toResponse(care)),
                () -> ctx.json(Map.of("message", "Queue is empty")));
    }

    private static CareRequest body(Context ctx) {
        try {
            return ctx.bodyAsClass(CareRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON request body");
        }
    }
}
