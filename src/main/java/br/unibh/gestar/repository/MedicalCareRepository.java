package br.unibh.gestar.repository;

import java.util.List;
import java.util.Optional;

import br.unibh.gestar.domain.MedicalCare;

public interface MedicalCareRepository {
    void save(MedicalCare medicalCare);
    Optional<MedicalCare> findById(String id);
    List<MedicalCare> listAll();
}
