package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalCareStatusTest {
    @Test
    void shouldHaveMedicalCareStatuses() {
        assertNotNull(MedicalCareStatus.WAITING_FOR_TRIAGE);
        assertNotNull(MedicalCareStatus.IN_TRIAGE);
        assertNotNull(MedicalCareStatus.IN_QUEUE);
        assertNotNull(MedicalCareStatus.IN_MEDICAL_CARE);
        assertNotNull(MedicalCareStatus.REFERRED);
        assertNotNull(MedicalCareStatus.FINISHED);
    }

    @Test
    void shouldBeAbleToConvertToString() {
        assertEquals("WAITING_FOR_TRIAGE", MedicalCareStatus.WAITING_FOR_TRIAGE.name());
        assertEquals("IN_TRIAGE", MedicalCareStatus.IN_TRIAGE.name());
        assertEquals("IN_QUEUE", MedicalCareStatus.IN_QUEUE.name());
        assertEquals("IN_MEDICAL_CARE", MedicalCareStatus.IN_MEDICAL_CARE.name());
        assertEquals("REFERRED", MedicalCareStatus.REFERRED.name());
        assertEquals("FINISHED", MedicalCareStatus.FINISHED.name());
    }

    @Test
    void shouldBeAbleToConvertFromString() {
        assertEquals(MedicalCareStatus.WAITING_FOR_TRIAGE, MedicalCareStatus.valueOf("WAITING_FOR_TRIAGE"));
        assertEquals(MedicalCareStatus.IN_TRIAGE, MedicalCareStatus.valueOf("IN_TRIAGE"));
    }
}
