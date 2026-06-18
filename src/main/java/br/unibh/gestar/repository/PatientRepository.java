package br.unibh.gestar.repository;

import br.unibh.gestar.domain.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    void save(Patient patient);
    void update(Patient patient);
    Optional<Patient> findByNameAndBirthDate(String name, LocalDate birthDate);
    List<Patient> listAll();
}
