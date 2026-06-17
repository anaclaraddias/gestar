package br.unibh.gestar.service;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.classification.ClassificationStrategy;
import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.MedicalCareStatus;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.queue.QueueManager;
import br.unibh.gestar.repository.MedicalCareRepository;

import java.util.List;
import java.util.Optional;

public class TriageService {

    private final ClassificationStrategy strategy;
    private final MedicalCareRepository repository;
    private final QueueManager queue;
    private final ClinicalNotifier notifier;

    public TriageService(ClassificationStrategy strategy, MedicalCareRepository repository,
                         QueueManager queue, ClinicalNotifier notifier) {
        this.strategy = strategy;
        this.repository = repository;
        this.queue = queue;
        this.notifier = notifier;
    }

    public MedicalCare performTriage(Patient patient, VitalSigns vitalSigns,
                                     String complaint, PriorityCategory category) {
        requireComplaint(complaint);
        MedicalCare care = new MedicalCare(patient, complaint, category);
        care.advanceStatus(MedicalCareStatus.IN_TRIAGE);
        care.setVitalSigns(vitalSigns);

        care.setClassification(strategy.classify(care));
        repository.save(care);

        care.advanceStatus(MedicalCareStatus.IN_QUEUE);
        queue.add(care);

        if (care.isCritical()) {
            notifier.triggerAlert(care);
        }
        return care;
    }

    public MedicalCare refer(Patient patient, VitalSigns vitalSigns, String complaint,
                             PriorityCategory category, String referralReason, String destinationUnit) {
        requireComplaint(complaint);
        require(referralReason, "referralReason");
        require(destinationUnit, "destinationUnit");
        MedicalCare care = new MedicalCare(patient, complaint, category);
        care.setVitalSigns(vitalSigns);
        care.markReferred(referralReason, destinationUnit);
        repository.save(care);
        return care;
    }

    public MedicalCare reclassify(String id, VitalSigns newVitalSigns) {
        MedicalCare care = findById(id);
        care.setVitalSigns(newVitalSigns);
        care.reclassify(strategy.classify(care));
        repository.save(care);
        if (care.isCritical()) {
            notifier.triggerAlert(care);
        }
        return care;
    }

    public Optional<MedicalCare> callNext() {
        MedicalCare care = queue.next();
        if (care == null) {
            return Optional.empty();
        }
        care.advanceStatus(MedicalCareStatus.IN_MEDICAL_CARE);
        repository.save(care);
        return Optional.of(care);
    }

    public MedicalCare finish(String id) {
        MedicalCare care = findById(id);
        care.advanceStatus(MedicalCareStatus.FINISHED);
        repository.save(care);
        return care;
    }

    public MedicalCare findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new MedicalCareNotFoundException(id));
    }

    public List<MedicalCare> listAll() {
        return repository.listAll();
    }
    
    public QueueStatus queueStatus() {
        return new QueueStatus(
                queue.size(UrgencyLevel.RED),
                queue.size(UrgencyLevel.ORANGE),
                queue.size(UrgencyLevel.YELLOW),
                queue.size(UrgencyLevel.GREEN),
                queue.size(),
                queue.peek());
    }

    private static void requireComplaint(String complaint) {
        require(complaint, "complaint");
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field '" + field + "'");
        }
    }
}
