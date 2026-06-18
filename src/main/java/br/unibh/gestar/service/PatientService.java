package br.unibh.gestar.service;

import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.interfaces.dto.PatientRequest;
import br.unibh.gestar.interfaces.dto.PatientResponse;
import br.unibh.gestar.repository.PatientRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PatientService {
    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public PatientResponse create(PatientRequest req) {
        ResolvedPatient resolved = resolvePatient(req);
        if (resolved.existing()) {
            repository.update(resolved.patient());
        } else {
            repository.save(resolved.patient());
        }
        
        return toResponse(resolved.patient());
    }

    public List<PatientResponse> list() {
        return repository.listAll().stream().map(PatientService::toResponse).toList();
    }

    private ResolvedPatient resolvePatient(PatientRequest req) {
        Patient candidate = toPatient(req);

        return repository.findByNameAndBirthDate(
            candidate.getName(),
            candidate.getBirthDate()
        ).map(
            existing -> new ResolvedPatient(existing, true)
        ).orElseGet(() -> new ResolvedPatient(candidate, false));
    }

    private record ResolvedPatient(Patient patient, boolean existing) {}

    private static Patient toPatient(PatientRequest req) {
        String name = require(req.name(), "name");
        String birthDate = require(req.birthDate(), "birthDate");
        
        try {
            return new Patient(name, LocalDate.parse(birthDate.trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("birthDate must be ISO yyyy-MM-dd");
        }
    }

    private static PatientResponse toResponse(Patient p) {
        if (p == null) {
            return null;
        }

        return buildResponse(p);
    }

    private static PatientResponse buildResponse(Patient p) {
        return new PatientResponse(
            p.getId(),
            p.getName(),
            p.getAge(),
            p.getBirthDate() == null ? null : p.getBirthDate().toString()
        );
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field '" + field + "'");
        }

        return value;
    }
}
