package br.unibh.gestar.classification;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.UrgencyLevel;

/**
 * Risk classification contract (Strategy pattern).
 * Allows changing the classification protocol without altering the queue or service.
 */
public interface ClassificationStrategy {

    UrgencyLevel classify(MedicalCare medicalCare);
}
