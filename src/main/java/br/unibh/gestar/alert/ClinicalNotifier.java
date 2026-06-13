package br.unibh.gestar.alert;

import br.unibh.gestar.domain.MedicalCare;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject of the Observer pattern: maintains registered observers and triggers
 * the clinical alert to all when a critical case occurs (RN07).
 */
public class ClinicalNotifier {

    private final List<AlertObserver> observers = new ArrayList<>();

    public void register(AlertObserver observer) {
        observers.add(observer);
    }

    public void triggerAlert(MedicalCare medicalCare) {
        for (AlertObserver observer : observers) {
            observer.notify(medicalCare);
        }
    }
}
