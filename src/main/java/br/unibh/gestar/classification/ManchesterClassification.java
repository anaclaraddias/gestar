package br.unibh.gestar.classification;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;

/**
 * Classification based on Manchester Protocol (simplified version).
 * Combines two factors and uses the most urgent between them:
 *  1) discriminators by vital signs;
 *  2) floor of urgency by complaint (RN08): certain complaints impose
 *     minimum urgency regardless of vital signs. Ex.: chest pain is at least urgent.
 */
public class ManchesterClassification implements ClassificationStrategy {

    @Override
    public UrgencyLevel classify(MedicalCare medicalCare) {
        VitalSigns sv = medicalCare.getVitalSigns();
        if (sv == null) {
            throw new IllegalStateException("Vital signs not informed for classification.");
        }
        UrgencyLevel byVitalSigns = classifyByVitalSigns(sv);
        UrgencyLevel complaintFloor = floorByComplaint(medicalCare.getMainComplaint());
        return mostUrgent(byVitalSigns, complaintFloor);
    }

    private UrgencyLevel classifyByVitalSigns(VitalSigns sv) {
        if (sv.getOxygenSaturation() < 85
                || sv.getHeartRate() > 150 || sv.getHeartRate() < 40
                || sv.getRespiratoryRate() > 35 || sv.getRespiratoryRate() < 8
                || sv.getSystolicPressure() > 220 || sv.getSystolicPressure() < 70
                || sv.getTemperature() > 41.0) {
            return UrgencyLevel.RED;
        }
        if (sv.getOxygenSaturation() < 91
                || sv.getHeartRate() > 120
                || sv.getRespiratoryRate() > 24
                || sv.getSystolicPressure() > 200
                || sv.getTemperature() >= 39.5
                || sv.getPainScale() >= 8) {
            return UrgencyLevel.ORANGE;
        }
        if (sv.getOxygenSaturation() < 95
                || sv.getHeartRate() > 100
                || sv.getTemperature() >= 38.0
                || sv.getPainScale() >= 4) {
            return UrgencyLevel.YELLOW;
        }
        return UrgencyLevel.GREEN;
    }

    /**
     * RN08: floor of urgency by complaint. Chest pain is always, at least, urgent.
     */
    private UrgencyLevel floorByComplaint(String complaint) {
        if (complaint == null) {
            return UrgencyLevel.GREEN;
        }
        String q = complaint.toLowerCase();
        if (q.contains("chest") || q.contains("thorac") || q.contains("breast")) {
            return UrgencyLevel.YELLOW;
        }
        return UrgencyLevel.GREEN;
    }

    /**
     * Returns the most urgent level between two (lower priority = more urgent).
     */
    private UrgencyLevel mostUrgent(UrgencyLevel a, UrgencyLevel b) {
        return a.getPriority() <= b.getPriority() ? a : b;
    }
}
