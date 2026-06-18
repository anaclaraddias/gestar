package br.unibh.gestar.interfaces.controller;

import br.unibh.gestar.interfaces.dto.PatientRequest;
import br.unibh.gestar.service.PatientService;

import io.javalin.http.Context;

public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    public void create(Context ctx) {
        PatientRequest req = patientBody(ctx);

        ctx.status(201).json(service.create(req));
    }

    private static PatientRequest patientBody(Context ctx) {
        try {
            return ctx.bodyAsClass(PatientRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed JSON request body");
        }
    }
}
