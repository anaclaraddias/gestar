package br.unibh.gestar.contract;

import br.unibh.gestar.domain.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepositoryContract {
    void save(Patient patient);
    void update(Patient patient);
    Optional<Patient> findByNameAndBirthDate(String name, LocalDate birthDate);
    List<Patient> listAll();
}
