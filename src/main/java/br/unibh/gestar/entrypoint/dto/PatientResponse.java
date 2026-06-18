package br.unibh.gestar.entrypoint.dto;

public record PatientResponse(
    String id, 
    String name, 
    int age,
    String birthDate
) {}
