package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {
    @Test
    void shouldCreatePatientWithGeneratedId() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Patient patient = new Patient("João Silva", birthDate);

        assertNotNull(patient.getId());
        assertFalse(patient.getId().isBlank());
    }

    @Test
    void shouldCreatePatientWithCorrectData() {
        String name = "Maria Santos";
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        Patient patient = new Patient(name, birthDate);

        assertEquals(name, patient.getName());
        assertEquals(birthDate, patient.getBirthDate());
    }

    @Test
    void shouldCalculateAgeCorrectly() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        Patient patient = new Patient("Test Patient", birthDate);

        assertEquals(30, patient.getAge());
    }

    @Test
    void shouldCalculateAgeForYoungPerson() {
        LocalDate birthDate = LocalDate.now().minusYears(1).minusMonths(6);
        Patient patient = new Patient("Young Patient", birthDate);

        assertEquals(1, patient.getAge());
    }

    @Test
    void shouldRecreatePatientFromPersistence() {
        String id = "patient-uuid-123";
        String name = "John Doe";
        LocalDate birthDate = LocalDate.of(1980, 12, 10);

        Patient patient = Patient.fromPersistence(id, name, birthDate);

        assertEquals(id, patient.getId());
        assertEquals(name, patient.getName());
        assertEquals(birthDate, patient.getBirthDate());
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentPatients() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Patient patient1 = new Patient("Patient One", birthDate);
        Patient patient2 = new Patient("Patient Two", birthDate);

        assertNotEquals(patient1.getId(), patient2.getId());
    }
}
