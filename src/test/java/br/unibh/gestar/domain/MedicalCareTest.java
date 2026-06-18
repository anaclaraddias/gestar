package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalCareTest {
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient("Test Patient", LocalDate.of(1990, 5, 15));
    }

    @Test
    void shouldCreateMedicalCareWithGeneratedId() {
        MedicalCare care = new MedicalCare(
            patient,
            "Chest pain",
            PriorityCategory.HIGH
        );

        assertNotNull(care.getId());
        assertFalse(care.getId().isBlank());
    }

    @Test
    void shouldInitializeWithWaitingForTriageStatus() {
        MedicalCare care = new MedicalCare(
            patient,
            "Headache",
            PriorityCategory.NORMAL
        );

        assertEquals(MedicalCareStatus.WAITING_FOR_TRIAGE, care.getStatus());
    }

    @Test
    void shouldSetClassification() {
        MedicalCare care = new MedicalCare(
            patient,
            "Fever",
            PriorityCategory.NORMAL
        );

        care.setClassification(UrgencyLevel.YELLOW);
        assertEquals(UrgencyLevel.YELLOW, care.getUrgencyLevel());
    }

    @Test
    void shouldReclassifyOnlyIfMoreUrgent() {
        MedicalCare care = new MedicalCare(
            patient,
            "Minor injury",
            PriorityCategory.NORMAL
        );

        care.setClassification(UrgencyLevel.GREEN);
        care.reclassify(UrgencyLevel.YELLOW);

        assertEquals(UrgencyLevel.YELLOW, care.getUrgencyLevel());
    }

    @Test
    void shouldNotReclassifyIfLessUrgent() {
        MedicalCare care = new MedicalCare(
            patient,
            "Severe chest pain",
            PriorityCategory.HIGH
        );

        care.setClassification(UrgencyLevel.RED);
        care.reclassify(UrgencyLevel.YELLOW);

        assertEquals(UrgencyLevel.RED, care.getUrgencyLevel());
    }

    @Test
    void shouldMarkAsReferred() {
        MedicalCare care = new MedicalCare(
            patient,
            "Complex case",
            PriorityCategory.HIGH
        );

        care.markReferred("Needs specialist", "Cardiology");

        assertEquals(MedicalCareStatus.REFERRED, care.getStatus());
    }

    @Test
    void shouldIdentifyCriticalCase() {
        MedicalCare care = new MedicalCare(
            patient,
            "Critical condition",
            PriorityCategory.HIGH
        );

        care.setClassification(UrgencyLevel.RED);
        assertTrue(care.isCritical());
    }

    @Test
    void shouldNotIdentifyNonCriticalAsCritical() {
        MedicalCare care = new MedicalCare(
            patient,
            "Minor issue",
            PriorityCategory.NORMAL
        );

        care.setClassification(UrgencyLevel.YELLOW);
        assertFalse(care.isCritical());
    }

    @Test
    void shouldSetVitalSigns() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 2);
        MedicalCare care = new MedicalCare(
            patient,
            "Check-up",
            PriorityCategory.NORMAL
        );

        care.setVitalSigns(vitals);
        assertEquals(vitals, care.getVitalSigns());
    }

    @Test
    void shouldAdvanceStatus() {
        MedicalCare care = new MedicalCare(
            patient,
            "Check-up",
            PriorityCategory.NORMAL
        );

        care.advanceStatus(MedicalCareStatus.IN_TRIAGE);
        assertEquals(MedicalCareStatus.IN_TRIAGE, care.getStatus());

        care.advanceStatus(MedicalCareStatus.IN_QUEUE);
        assertEquals(MedicalCareStatus.IN_QUEUE, care.getStatus());
    }

    @Test
    void shouldRecoverFromPersistence() {
        VitalSigns vitals = new VitalSigns(110, 75, 65, 15, 36.5, 97, 1);
        String id = "care-uuid-123";
        LocalDateTime arrivalTime = LocalDateTime.now().minusHours(2);

        MedicalCare care = MedicalCare.fromPersistence(
            id,
            patient,
            "Consultation",
            PriorityCategory.NORMAL,
            arrivalTime,
            vitals,
            UrgencyLevel.GREEN,
            MedicalCareStatus.FINISHED,
            null,
            null
        );

        assertEquals(id, care.getId());
        assertEquals(patient, care.getPatient());
        assertEquals(vitals, care.getVitalSigns());
        assertEquals(UrgencyLevel.GREEN, care.getUrgencyLevel());
        assertEquals(MedicalCareStatus.FINISHED, care.getStatus());
    }
}
