package br.unibh.gestar.service;

import br.unibh.gestar.contract.PatientRepositoryContract;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.entrypoint.dto.PatientRequest;
import br.unibh.gestar.entrypoint.dto.PatientResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PatientServiceTest {
    private PatientService service;
    private MockPatientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MockPatientRepository();
        service = new PatientService(repository);
    }

    @Test
    void shouldCreateNewPatient() {
        PatientRequest req = new PatientRequest("John Doe", "1990-05-15");

        PatientResponse response = service.create(req);

        assertNotNull(response);
        assertEquals("John Doe", response.name());
        assertEquals(1990 + "-05-15", response.birthDate());
        assertTrue(repository.hasSaved());
    }

    @Test
    void shouldUpdateExistingPatient() {
        Patient existing = new Patient("Jane Doe", LocalDate.of(1985, 3, 20));
        repository.addPatient(existing);

        PatientRequest req = new PatientRequest("Jane Doe", "1985-03-20");
        PatientResponse response = service.create(req);

        assertTrue(repository.hasUpdated());
    }

    @Test
    void shouldThrowExceptionForMissingName() {
        PatientRequest req = new PatientRequest(null, "1990-05-15");

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForBlankName() {
        PatientRequest req = new PatientRequest("  ", "1990-05-15");

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForMissingBirthDate() {
        PatientRequest req = new PatientRequest("John Doe", null);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        PatientRequest req = new PatientRequest("John Doe", "15/05/1990");

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldListAllPatients() {
        Patient patient1 = new Patient("Patient One", LocalDate.of(1990, 5, 15));
        Patient patient2 = new Patient("Patient Two", LocalDate.of(1985, 3, 20));

        repository.addPatient(patient1);
        repository.addPatient(patient2);

        List<PatientResponse> responses = service.list();

        assertEquals(2, responses.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoPatients() {
        List<PatientResponse> responses = service.list();

        assertEquals(0, responses.size());
    }

    @Test
    void shouldCalculateAgeInResponse() {
        PatientRequest req = new PatientRequest("Test", "1990-05-15");
        PatientResponse response = service.create(req);

        assertTrue(response.age() >= 30);
    }

    @Test
    void shouldTrimWhitespaceFromBirthDate() {
        PatientRequest req = new PatientRequest("John Doe", "  1990-05-15  ");

        PatientResponse response = service.create(req);

        assertNotNull(response);
    }

    // Mock repository for testing
    private static class MockPatientRepository implements PatientRepositoryContract {
        private List<Patient> patients = new java.util.ArrayList<>();
        private boolean saved = false;
        private boolean updated = false;

        void addPatient(Patient patient) {
            patients.add(patient);
        }

        boolean hasSaved() {
            return saved;
        }

        boolean hasUpdated() {
            return updated;
        }

        @Override
        public void save(Patient patient) {
            saved = true;
            patients.add(patient);
        }

        @Override
        public void update(Patient patient) {
            updated = true;
        }

        @Override
        public Optional<Patient> findByNameAndBirthDate(String name, LocalDate birthDate) {
            return patients.stream()
                .filter(p -> p.getName().equals(name) && p.getBirthDate().equals(birthDate))
                .findFirst();
        }

        @Override
        public List<Patient> listAll() {
            return new java.util.ArrayList<>(patients);
        }
    }
}
