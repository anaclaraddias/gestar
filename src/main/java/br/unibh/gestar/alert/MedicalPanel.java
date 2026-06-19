package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;

public class MedicalPanel implements AlertObserver {
    @Override
    public void notify(MedicalCare medicalCare) {
        System.out.println(
            "[CLINICAL ALERT] Critical case in queue: "
            + medicalCare.getPatient().getName()
            + " | Complaint: " + medicalCare.getMainComplaint()
            + " | Level: " + medicalCare.getUrgencyLevel()
        );
    }
}
