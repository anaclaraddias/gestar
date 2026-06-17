package br.unibh.gestar.service;

public class MedicalCareNotFoundException extends RuntimeException {

    public MedicalCareNotFoundException(String id) {
        super("No medical care with id " + id);
    }
}
