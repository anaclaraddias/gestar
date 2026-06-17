package br.unibh.gestar.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CareRequest(
        String name,
        Integer age,
        String birthDate,
        String complaint,
        String category,
        Integer systolic,
        Integer diastolic,
        Integer heartRate,
        Integer respiratoryRate,
        Double temperature,
        Integer spo2,
        Integer pain,
        String referralReason,
        String destinationUnit,
        String status) {
}
