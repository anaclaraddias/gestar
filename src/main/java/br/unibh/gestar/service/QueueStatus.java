package br.unibh.gestar.service;

import br.unibh.gestar.domain.MedicalCare;

public record QueueStatus(int red, int orange, int yellow, int green, int total, MedicalCare next) {
}
