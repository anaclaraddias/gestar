package br.unibh.gestar.interfaces.dto;

public record CareResponse(
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
        String destinationUnit) {
}
