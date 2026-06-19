package br.unibh.gestar.contract;

import java.util.List;
import java.util.Optional;

import br.unibh.gestar.domain.MedicalCare;

public interface MedicalCareRepositoryContract {
    void save(MedicalCare medicalCare);
    Optional<MedicalCare> findById(String id);
    List<MedicalCare> listAll();
}
