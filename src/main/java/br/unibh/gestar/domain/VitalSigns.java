package br.unibh.gestar.domain;

public class VitalSigns {
    private final int systolicPressure;
    private final int diastolicPressure;
    private final int heartRate;
    private final int respiratoryRate;
    private final double temperature;
    private final int oxygenSaturation;
    private final int painScale;

    public VitalSigns(
        int systolicPressure,
        int diastolicPressure,
        int heartRate,
        int respiratoryRate,
        double temperature,
        int oxygenSaturation,
        int painScale
    ) {
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
        this.oxygenSaturation = oxygenSaturation;
        this.painScale = painScale;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getOxygenSaturation() {
        return oxygenSaturation;
    }

    public int getPainScale() {
        return painScale;
    }
}
