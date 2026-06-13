package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Clinical alert observer (Observer pattern).
 * Implementations react when a critical case is signaled.
 */
public interface AlertObserver {

    void notify(MedicalCare medicalCare);
}
