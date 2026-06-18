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

public class ManchesterClassificationTest {
    private ManchesterClassification classification;
    private Patient patient;

    @BeforeEach
    void setUp() {
        classification = new ManchesterClassification();
        patient = new Patient("Test Patient", LocalDate.of(1990, 5, 15));
    }

    @Test
    void shouldThrowExceptionWhenVitalSignsAreNull() {
        MedicalCare care = new MedicalCare(patient, "Chest pain", PriorityCategory.HIGH);

        assertThrows(IllegalStateException.class, () -> classification.classify(care));
    }

    @Test
    void shouldClassifyAsRedForLowOxygenSaturation() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 84, 2);
        MedicalCare care = new MedicalCare(patient, "Difficulty breathing", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsRedForHighHeartRate() {
        VitalSigns vitals = new VitalSigns(120, 80, 160, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "Heart palpitations", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsRedForLowHeartRate() {
        VitalSigns vitals = new VitalSigns(120, 80, 35, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "Low heart rate", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsRedForHighRespiratoryRate() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 36, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "Rapid breathing", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsRedForHighBloodPressure() {
        VitalSigns vitals = new VitalSigns(225, 140, 70, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "High blood pressure", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsRedForHighFever() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 41.5, 97, 2);
        MedicalCare care = new MedicalCare(patient, "High fever", PriorityCategory.HIGH);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.RED, level);
    }

    @Test
    void shouldClassifyAsOrangeForModerateLowOxygenSaturation() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 90, 2);
        MedicalCare care = new MedicalCare(patient, "Breathing issue", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyAsOrangeForElevatedHeartRate() {
        VitalSigns vitals = new VitalSigns(120, 80, 125, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "Elevated heart rate", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyAsOrangeForHighPainScale() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 97, 8);
        MedicalCare care = new MedicalCare(patient, "Severe pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.ORANGE, level);
    }

    @Test
    void shouldClassifyAsYellowForModerateSymptoms() {
        VitalSigns vitals = new VitalSigns(120, 80, 95, 16, 37.5, 95, 4);
        MedicalCare care = new MedicalCare(patient, "Chest discomfort", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsYellowForChestPainComplaint() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "chest pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsYellowForThoracicComplaint() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 97, 2);
        MedicalCare care = new MedicalCare(patient, "thoracic pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }

    @Test
    void shouldClassifyAsGreenForNormalVitalSigns() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 0);
        MedicalCare care = new MedicalCare(patient, "Routine checkup", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.GREEN, level);
    }

    @Test
    void shouldChooseMostUrgentBetweenVitalsAndComplaint() {
        VitalSigns vitals = new VitalSigns(120, 80, 70, 16, 36.8, 98, 2);
        MedicalCare care = new MedicalCare(patient, "chest pain", PriorityCategory.NORMAL);
        care.setVitalSigns(vitals);

        UrgencyLevel level = classification.classify(care);
        assertEquals(UrgencyLevel.YELLOW, level);
    }
}
