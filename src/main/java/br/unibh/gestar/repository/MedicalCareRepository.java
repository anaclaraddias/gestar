package br.unibh.gestar.repository;

import java.util.List;
import java.util.Optional;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Persistence contract for medical care (Repository pattern).
 * Abstracts storage, allowing to change implementation (memory, database)
 * without affecting business rule. It is the key point of DIP in the project: the triage
 * service will depend on this interface, never on a concrete implementation.
 */
public interface MedicalCareRepository {

    void save(MedicalCare medicalCare);

    Optional<MedicalCare> findById(String id);

    List<MedicalCare> listAll();
}
