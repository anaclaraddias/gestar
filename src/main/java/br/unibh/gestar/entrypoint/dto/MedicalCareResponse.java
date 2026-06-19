package br.unibh.gestar.entrypoint.dto;

public record MedicalCareResponse(
        String id,
        PatientResponse patient,
        String complaint,
        String category,
        String urgencyLevel,
        String urgencyDescription,
        String status,
        String arrivalDateTime,
        VitalsResponse vitalSigns,
        String referralReason,
        String destinationUnit
) {}
