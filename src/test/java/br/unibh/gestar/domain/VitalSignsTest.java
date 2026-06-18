package br.unibh.gestar.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VitalSignsTest {

    @Test
    void shouldCreateVitalSignsWithAllParameters() {
        int systolic = 120;
        int diastolic = 80;
        int heartRate = 70;
        int respiratoryRate = 16;
        double temperature = 36.8;
        int oxygenSaturation = 98;
        int painScale = 3;

        VitalSigns vitals = new VitalSigns(
            systolic, diastolic, heartRate, respiratoryRate,
            temperature, oxygenSaturation, painScale
        );

        assertEquals(systolic, vitals.getSystolicPressure());
        assertEquals(diastolic, vitals.getDiastolicPressure());
        assertEquals(heartRate, vitals.getHeartRate());
        assertEquals(respiratoryRate, vitals.getRespiratoryRate());
        assertEquals(temperature, vitals.getTemperature());
        assertEquals(oxygenSaturation, vitals.getOxygenSaturation());
        assertEquals(painScale, vitals.getPainScale());
    }

    @Test
    void shouldStoreNormalVitalSigns() {
        VitalSigns vitals = new VitalSigns(120, 80, 72, 18, 36.5, 98, 0);

        assertTrue(vitals.getSystolicPressure() > 0);
        assertTrue(vitals.getOxygenSaturation() > 0);
        assertTrue(vitals.getTemperature() > 0);
    }

    @Test
    void shouldStoreAbnormalVitalSigns() {
        VitalSigns vitals = new VitalSigns(220, 140, 160, 35, 40.5, 80, 10);

        assertEquals(220, vitals.getSystolicPressure());
        assertEquals(160, vitals.getHeartRate());
        assertEquals(40.5, vitals.getTemperature());
        assertEquals(80, vitals.getOxygenSaturation());
    }

    @Test
    void shouldHandleExtremePressureValues() {
        VitalSigns vitals = new VitalSigns(50, 30, 40, 8, 35.0, 85, 2);

        assertEquals(50, vitals.getSystolicPressure());
        assertEquals(30, vitals.getDiastolicPressure());
    }

    @Test
    void shouldHandleHighPainScale() {
        VitalSigns vitals = new VitalSigns(130, 85, 80, 20, 37.5, 96, 10);

        assertEquals(10, vitals.getPainScale());
    }
}
