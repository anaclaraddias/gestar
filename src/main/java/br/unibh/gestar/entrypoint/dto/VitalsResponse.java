package br.unibh.gestar.entrypoint.dto;

public record VitalsResponse(
   int systolic,
   int diastolic,
   int heartRate,
   int respiratoryRate,
   double temperature,
   int oxygenSaturation,
   int painScale
) {}
