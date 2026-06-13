package br.unibh.gestar.service;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.classification.ClassificationStrategy;
import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.domain.MedicalCareStatus;
import br.unibh.gestar.queue.QueueManager;
import br.unibh.gestar.repository.MedicalCareRepository;

/**
 * Orchestrates the triage flow: creates the medical care, classifies the risk,
 * persists, queues and triggers alerts. Depends only on abstractions
 * (the classification strategy and the repository), which materializes the DIP.
 */
public class TriageService {

    private final ClassificationStrategy strategy;
    private final MedicalCareRepository repository;
    private final QueueManager queue;
    private final ClinicalNotifier notifier;

    public TriageService(ClassificationStrategy strategy,
                          MedicalCareRepository repository,
                          QueueManager queue,
                          ClinicalNotifier notifier) {
        this.strategy = strategy;
        this.repository = repository;
        this.queue = queue;
        this.notifier = notifier;
    }

    /**
     * Triage of an eligible patient: classifies, persists, queues and,
     * if it is a critical case (Red), triggers the clinical alert (RN07).
     */
    public MedicalCare performTriage(Patient patient, VitalSigns vitalSigns,
                                       String complaint, PriorityCategory category) {
        MedicalCare medicalCare = new MedicalCare(patient, complaint, category);
        medicalCare.advanceStatus(MedicalCareStatus.IN_TRIAGE);
        medicalCare.setVitalSigns(vitalSigns);

        UrgencyLevel urgencyLevel = strategy.classify(medicalCare);
        medicalCare.setClassification(urgencyLevel);

        repository.save(medicalCare);

        medicalCare.advanceStatus(MedicalCareStatus.IN_QUEUE);
        queue.add(medicalCare);

        if (medicalCare.isCritical()) {
            notifier.triggerAlert(medicalCare);
        }
        return medicalCare;
    }

    /**
     * Registers a patient not seen by the unit: stores the data, vital signs
     * and referral (RN06), without queuing.
     */
    public MedicalCare refer(Patient patient, VitalSigns vitalSigns, String complaint,
                                  PriorityCategory category, String referralReason, String destinationUnit) {
        MedicalCare medicalCare = new MedicalCare(patient, complaint, category);
        medicalCare.setVitalSigns(vitalSigns);
        medicalCare.markReferred(referralReason, destinationUnit);
        repository.save(medicalCare);
        return medicalCare;
    }

    /**
     * Re-evaluates a medical care with new vital signs. Applies the monotonic rule
     * (RN05): urgency only increases, never decreases. If it becomes critical,
     * triggers the alert. Queue reordering after level change is left
     * as future evolution; here the level is updated and alert is triggered.
     */
    public MedicalCare reclassify(MedicalCare medicalCare, VitalSigns newVitalSigns) {
        medicalCare.setVitalSigns(newVitalSigns);
        UrgencyLevel newUrgencyLevel = strategy.classify(medicalCare);
        medicalCare.reclassify(newUrgencyLevel);
        repository.save(medicalCare);
        if (medicalCare.isCritical()) {
            notifier.triggerAlert(medicalCare);
        }
        return medicalCare;
    }

    /**
     * Calls the next patient in queue for medical care, or null if the queue
     * is empty.
     */
    public MedicalCare callNext() {
        MedicalCare medicalCare = queue.next();
        if (medicalCare != null) {
            medicalCare.advanceStatus(MedicalCareStatus.IN_MEDICAL_CARE);
            repository.save(medicalCare);
        }
        return medicalCare;
    }

    /**
     * Finishes a medical care.
     */
    public void finalize(MedicalCare medicalCare) {
        medicalCare.advanceStatus(MedicalCareStatus.FINISHED);
        repository.save(medicalCare);
    }
}
