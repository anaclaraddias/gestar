package br.unibh.gestar.service;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.classification.SimpleClassification;
import br.unibh.gestar.contract.MedicalCareRepositoryContract;
import br.unibh.gestar.contract.PatientRepositoryContract;
import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.MedicalCareStatus;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.entrypoint.dto.MedicalCareRequest;
import br.unibh.gestar.entrypoint.dto.MedicalCareResponse;
import br.unibh.gestar.queue.QueueManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalCareServiceTest {
    private MedicalCareService service;
    private MockMedicalCareRepository careRepository;
    private MockPatientRepository patientRepository;
    private QueueManager queue;
    private MockNotifier notifier;

    @BeforeEach
    void setUp() {
        careRepository = new MockMedicalCareRepository();
        patientRepository = new MockPatientRepository();
        queue = new QueueManager();
        notifier = new MockNotifier();

        service = new MedicalCareService(
            new SimpleClassification(),
            careRepository,
            patientRepository,
            queue,
            notifier
        );
    }

    @Test
    void shouldCreateMedicalCareWithNewPatient() {
        MedicalCareRequest req = createRequest("New Patient", "1990-05-15", "Chest pain", null);

        MedicalCareResponse response = service.create(req);

        assertNotNull(response);
        assertEquals("New Patient", response.patientName());
        assertEquals("Chest pain", response.complaint());
        assertTrue(careRepository.hasSaved());
    }

    @Test
    void shouldCreateMedicalCareWithAge() {
        MedicalCareRequest req = createRequestWithAge("Age Based", 25, "Headache", null);

        MedicalCareResponse response = service.create(req);

        assertNotNull(response);
        assertEquals("Age Based", response.patientName());
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithoutAgeOrBirthDate() {
        MedicalCareRequest req = new MedicalCareRequest(
            "Patient", null, null, "Complaint",
            null, null, null, null, null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForInvalidAge() {
        MedicalCareRequest req = createRequestWithAge("Patient", 150, "Complaint", null);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForNegativeAge() {
        MedicalCareRequest req = createRequestWithAge("Patient", -5, "Complaint", null);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldSetDefaultVitalSigns() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Complaint", null);

        MedicalCareResponse response = service.create(req);

        assertNotNull(response.vitals());
    }

    @Test
    void shouldTriggerAlertForCriticalCase() {
        MedicalCareRequest req = new MedicalCareRequest(
            "Patient", "1990-05-15", null, "Critical",
            220, 140, 160, 35, 41.5, 80, 10, "NORMAL"
        );

        service.create(req);

        assertTrue(notifier.alertTriggered());
    }

    @Test
    void shouldNotTriggerAlertForNonCriticalCase() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Minor issue", null);

        service.create(req);

        assertFalse(notifier.alertTriggered());
    }

    @Test
    void shouldAddToQueue() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Complaint", null);

        service.create(req);

        assertEquals(1, queue.size());
    }

    @Test
    void shouldReferPatient() {
        MedicalCareRequest req = new MedicalCareRequest(
            "Patient", "1990-05-15", null, "Complex",
            null, null, null, null, null, null, null, "NORMAL"
        );
        req = new MedicalCareRequest(
            req.name(), req.birthDate(), req.age(), req.complaint(),
            req.systolic(), req.diastolic(), req.heartRate(), req.respiratoryRate(),
            req.temperature(), req.spo2(), req.pain(), req.category(),
            "Needs specialist", "Cardiology"
        );

        MedicalCareResponse response = service.refer(req);

        assertEquals(MedicalCareStatus.REFERRED.name(), response.status());
    }

    @Test
    void shouldThrowExceptionForMissingComplaint() {
        MedicalCareRequest req = new MedicalCareRequest(
            "Patient", "1990-05-15", null, null,
            null, null, null, null, null, null, null, "NORMAL"
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldThrowExceptionForBlankComplaint() {
        MedicalCareRequest req = new MedicalCareRequest(
            "Patient", "1990-05-15", null, "  ",
            null, null, null, null, null, null, null, "NORMAL"
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void shouldListAllMedicalCares() {
        MedicalCareRequest req1 = createRequest("Patient1", "1990-05-15", "Issue1", null);
        MedicalCareRequest req2 = createRequest("Patient2", "1985-03-20", "Issue2", null);

        service.create(req1);
        service.create(req2);

        List<MedicalCareResponse> responses = service.listResponses();

        assertEquals(2, responses.size());
    }

    @Test
    void shouldReturnQueueStatus() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Complaint", null);
        service.create(req);

        QueueStatus status = service.queueStatus();

        assertEquals(1, status.total());
    }

    @Test
    void shouldCallNextPatient() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Complaint", null);
        service.create(req);

        Optional<MedicalCareResponse> next = service.callNext();

        assertTrue(next.isPresent());
        assertEquals(MedicalCareStatus.IN_MEDICAL_CARE.name(), next.get().status());
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoNextPatient() {
        Optional<MedicalCareResponse> next = service.callNext();

        assertFalse(next.isPresent());
    }

    @Test
    void shouldFinishMedicalCare() {
        MedicalCareRequest req = createRequest("Patient", "1990-05-15", "Complaint", null);
        MedicalCareResponse created = service.create(req);

        MedicalCareResponse finished = service.finish(created.id());

        assertEquals(MedicalCareStatus.FINISHED.name(), finished.status());
    }

    @Test
    void shouldThrowExceptionWhenFinishingNonexistent() {
        assertThrows(MedicalCareService.MedicalCareNotFoundException.class,
            () -> service.finish("non-existent-id"));
    }

    private MedicalCareRequest createRequest(String name, String birthDate, String complaint, String category) {
        return new MedicalCareRequest(
            name, birthDate, null, complaint,
            null, null, null, null, null, null, null, category
        );
    }

    private MedicalCareRequest createRequestWithAge(String name, Integer age, String complaint, String category) {
        return new MedicalCareRequest(
            name, null, age, complaint,
            null, null, null, null, null, null, null, category
        );
    }

    // Mock repositories and notifier for testing
    private static class MockMedicalCareRepository implements MedicalCareRepositoryContract {
        private List<MedicalCare> cares = new java.util.ArrayList<>();
        private boolean saved = false;

        boolean hasSaved() {
            return saved;
        }

        @Override
        public void save(MedicalCare medicalCare) {
            saved = true;
            cares.removeIf(c -> c.getId().equals(medicalCare.getId()));
            cares.add(medicalCare);
        }

        @Override
        public Optional<MedicalCare> findById(String id) {
            return cares.stream().filter(c -> c.getId().equals(id)).findFirst();
        }

        @Override
        public List<MedicalCare> listAll() {
            return new java.util.ArrayList<>(cares);
        }
    }

    private static class MockPatientRepository implements PatientRepositoryContract {
        private List<Patient> patients = new java.util.ArrayList<>();

        @Override
        public void save(Patient patient) {
            patients.add(patient);
        }

        @Override
        public void update(Patient patient) {
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

    private static class MockNotifier extends ClinicalNotifier {
        private boolean alertTriggered = false;

        boolean alertTriggered() {
            return alertTriggered;
        }

        @Override
        public void triggerAlert(MedicalCare medicalCare) {
            alertTriggered = true;
            super.triggerAlert(medicalCare);
        }
    }
}
