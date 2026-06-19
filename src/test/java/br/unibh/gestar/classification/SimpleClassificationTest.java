package br.unibh.gestar.classification;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleClassificationTest {
    private SimpleClassification classification;
    private Patient patient;

    @BeforeEach
    void setUp() {
        classification = new SimpleClassification();
        patient = new Patient("Test Patient", LocalDate.of(1990, 5, 15));
    }

    @Test
    void shouldThrowExceptionWhenVitalSignsAreNull() {
        MedicalCare care = new MedicalCare(patient, "Check-up", PriorityCategory.NORMAL);

        assertThrows(IllegalStateException.class, () -> classification.classify(care));
    }

    @Test
    void shouldClassifyAsOrangeForLowOxygenSaturation() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 89, 2);
        MedicalCare care = new MedicalCare(patient, "Breathing issue", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyAsOrangeForHighPainScale() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 8);
        MedicalCare care = new MedicalCare(patient, "Severe pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyAsYellowForModeratePainScale() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 5);
        MedicalCare care = new MedicalCare(patient, "Moderate pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsYellowForPainScaleOf6() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 6);
        MedicalCare care = new MedicalCare(patient, "Pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsYellowForPainScaleOf7() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 7);
        MedicalCare care = new MedicalCare(patient, "Pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsGreenForNormalVitalSigns() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 2);
        MedicalCare care = new MedicalCare(patient, "Routine check", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }

    @Test
    void shouldClassifyAsGreenForNoPain() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 0);
        MedicalCare care = new MedicalCare(patient, "Consultation", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }

    @Test
    void shouldClassifyAsGreenForLowPainScale() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 3);
        MedicalCare care = new MedicalCare(patient, "Minor issue", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }

    @Test
    void shouldClassifyAsOrangeWhenBothLowOxAndHighPain() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 89, 8);
        MedicalCare care = new MedicalCare(patient, "Critical", PriorityCategory.HIGHEST_PRIORITY);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyCorrectlyBoundaryOxygenSaturation() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 90, 2);
        MedicalCare care = new MedicalCare(patient, "Check", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }

    @Test
    void shouldClassifyCorrectlyBoundaryPainScale() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 4);
        MedicalCare care = new MedicalCare(patient, "Pain check", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }
}
