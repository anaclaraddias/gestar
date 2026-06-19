package br.unibh.gestar.classification;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;

public class SimpleClassification implements ClassificationStrategy {
    @Override
    public UrgencyLevel classify(MedicalCare medicalCare) {
        VitalSigns sv = medicalCare.getVitalSigns();
        if (sv == null) {
            throw new IllegalStateException("Vital signs not informed for classification.");
        }
        
        if (sv.getOxygenSaturation() < 90 || sv.getPainScale() >= 8) {
            return UrgencyLevel.ORANGE;
        }
        
        if (sv.getPainScale() >= 5) {
            return UrgencyLevel.YELLOW;
        }
        
        return UrgencyLevel.GREEN;
    }
}
