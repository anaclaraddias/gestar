package br.unibh.gestar.classification;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

public interface ClassificationStrategy {
    UrgencyLevel classify(MedicalCare medicalCare);
}
