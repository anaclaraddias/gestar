package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;

/**
 * Observer that displays the clinical alert on the medical panel.
 * In this version, writes the notification to the console.
 */
public class MedicalPanel implements AlertObserver {

    @Override
    public void notify(MedicalCare medicalCare) {
        System.out.println("[CLINICAL ALERT] Critical case in queue: "
                + medicalCare.getPaciente().getName()
                + " | Complaint: " + medicalCare.getMainComplaint()
                + " | Level: " + medicalCare.getUrgencyLevel());
    }
}
