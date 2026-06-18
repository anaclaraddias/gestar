package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;

public interface AlertObserver {
    void notify(MedicalCare medicalCare);
}
