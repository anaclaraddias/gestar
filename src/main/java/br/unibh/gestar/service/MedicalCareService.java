package br.unibh.gestar.service;

import br.unibh.gestar.alert.ClinicalNotifier;
import br.unibh.gestar.classification.ClassificationStrategy;
import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.MedicalCareStatus;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.UrgencyLevel;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.interfaces.dto.CareRequest;
import br.unibh.gestar.interfaces.dto.CareResponse;
import br.unibh.gestar.interfaces.dto.PatientResponse;
import br.unibh.gestar.interfaces.dto.QueueResponse;
import br.unibh.gestar.interfaces.dto.VitalsResponse;
import br.unibh.gestar.queue.QueueManager;
import br.unibh.gestar.repository.PatientRepository;
import br.unibh.gestar.repository.MedicalCareRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class MedicalCareService {
    private final ClassificationStrategy strategy;
    private final MedicalCareRepository repository;
    private final PatientRepository patientRepository;
    private final QueueManager queue;
    private final ClinicalNotifier notifier;

    public MedicalCareService(
        ClassificationStrategy strategy, 
        MedicalCareRepository repository,
        PatientRepository patientRepository,
        QueueManager queue, 
        ClinicalNotifier notifier
    ) {
        this.strategy = strategy;
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.queue = queue;
        this.notifier = notifier;
    }

    public CareResponse create(CareRequest req) {
        Patient patient = resolvePatient(req);
        VitalSigns vitalSigns = toVitalSigns(req);
        String complaint = require(req.complaint(), "complaint");
        PriorityCategory category = toCategory(req);

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

        return toResponse(care);
    }

    public CareResponse refer(CareRequest req) {
        Patient patient = resolvePatient(req);
        VitalSigns vitalSigns = toVitalSigns(req);
        String complaint = require(req.complaint(), "complaint");
        PriorityCategory category = toCategory(req);
        String referralReason = require(req.referralReason(), "referralReason");
        String destinationUnit = require(req.destinationUnit(), "destinationUnit");

        requireComplaint(complaint);
        
        MedicalCare care = new MedicalCare(patient, complaint, category);
        
        care.setVitalSigns(vitalSigns);
        care.markReferred(referralReason, destinationUnit);
        
        repository.save(care);
        
        return toResponse(care);
    }

    public CareResponse update(String id, CareRequest req) {
        if (req.status() != null && !req.status().isBlank()) {
            if (!"FINISHED".equalsIgnoreCase(req.status().trim())) {
                throw new IllegalArgumentException(
                    "Unsupported status transition '" + req.status() + "'. Only FINISHED is allowed via PATCH."
                );
            }
            return finish(id);
        }

        if (hasVitalSigns(req)) {
            return reclassify(id, toVitalSigns(req));
        }

        throw new IllegalArgumentException(
            "Nothing to update: send new vital signs (reclassify) or \"status\":\"FINISHED\"."
        );
    }

    public CareResponse reclassify(String id, VitalSigns newVitalSigns) {
        MedicalCare care = findById(id);
        care.setVitalSigns(newVitalSigns);
        care.reclassify(strategy.classify(care));
        
        repository.save(care);
        
        if (care.isCritical()) {
            notifier.triggerAlert(care);
        }

        return toResponse(care);
    }

    public Optional<CareResponse> callNext() {
        MedicalCare care = queue.next();
        
        if (care == null) {
            return Optional.empty();
        }
        
        care.advanceStatus(MedicalCareStatus.IN_MEDICAL_CARE);
        
        repository.save(care);
        
        return Optional.of(toResponse(care));
    }

    public CareResponse finish(String id) {
        MedicalCare care = findById(id);
        
        care.advanceStatus(MedicalCareStatus.FINISHED);
        
        repository.save(care);
        
        return toResponse(care);
    }

    public MedicalCare findById(String id) {
        return repository.findById(id).orElseThrow(() -> new MedicalCareNotFoundException(id));
    }

    public List<MedicalCare> listAll() {
        return repository.listAll();
    }

    public List<CareResponse> listResponses() {
        return listAll().stream().map(this::toResponse).toList();
    }

    public CareResponse findResponse(String id) {
        return toResponse(findById(id));
    }

    public QueueStatus queueStatus() {
        return new QueueStatus(
            queue.size(UrgencyLevel.RED),
            queue.size(UrgencyLevel.ORANGE),
            queue.size(UrgencyLevel.YELLOW),
            queue.size(UrgencyLevel.GREEN),
            queue.size(),
            queue.peek()
        );
    }

    private static void requireComplaint(String complaint) {
        require(complaint, "complaint");
    }

    public CareResponse toResponse(MedicalCare c) {
        return new CareResponse(
            c.getId(),
            toPatientResponse(c.getPatient()),
            c.getMainComplaint(),
            c.getPriorityCategory() == null ? null : c.getPriorityCategory().name(),
            c.getUrgencyLevel() == null ? null : c.getUrgencyLevel().name(),
            c.getUrgencyLevel() == null ? null : c.getUrgencyLevel().getDescription(),
            c.getStatus() == null ? null : c.getStatus().name(),
            c.getArrivalDateTime() == null ? null : c.getArrivalDateTime().toString(),
            toVitalsResponse(c.getVitalSigns()),
            c.getReferralReason(),
            c.getDestinationUnit()
        );
    }

    public QueueResponse toQueueResponse(QueueStatus q) {
        MedicalCare n = q.next();
        QueueResponse.NextInQueue next = n == null ? null : new QueueResponse.NextInQueue(
            n.getId(),
            n.getUrgencyLevel() == null ? null : n.getUrgencyLevel().name(),
            n.getPatient() == null ? null : n.getPatient().getName()
        );

        return new QueueResponse(q.red(), q.orange(), q.yellow(), q.green(), q.total(), next);
    }

    private Patient resolvePatient(CareRequest req) {
        Patient candidate = toPatient(req);
        return patientRepository.findByNameAndBirthDate(
            candidate.getName(),
            candidate.getBirthDate()
        ).map(existing -> {
            patientRepository.update(existing);
            return existing;
        }).orElseGet(() -> {
            patientRepository.save(candidate);
            return candidate;
        });
    }

    private static Patient toPatient(CareRequest req) {
        String name = require(req.name(), "name");
       
        if (req.birthDate() != null && !req.birthDate().isBlank()) {
            try {
                return new Patient(name, LocalDate.parse(req.birthDate().trim()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("birthDate must be ISO yyyy-MM-dd");
            }
        }

        if (req.age() != null) {
            if (req.age() < 0 || req.age() > 130) {
                throw new IllegalArgumentException("age must be between 0 and 130");
            }

            return new Patient(name, LocalDate.now().minusYears(req.age()));
        }

        throw new IllegalArgumentException("Provide 'birthDate' (yyyy-MM-dd) or 'age'");
    }

    private static VitalSigns toVitalSigns(CareRequest r) {
        return new VitalSigns(
            r.systolic() == null ? 120 : r.systolic(),
            r.diastolic() == null ? 80 : r.diastolic(),
            r.heartRate() == null ? 80 : r.heartRate(),
            r.respiratoryRate() == null ? 16 : r.respiratoryRate(),
            r.temperature() == null ? 36.5 : r.temperature(),
            r.spo2() == null ? 98 : r.spo2(),
            r.pain() == null ? 0 : r.pain()
        );
    }

    private static boolean hasVitalSigns(CareRequest r) {
        return r.systolic() != null || r.diastolic() != null || r.heartRate() != null
            || r.respiratoryRate() != null || r.temperature() != null
            || r.spo2() != null || r.pain() != null;
    }

    private static PriorityCategory toCategory(CareRequest req) {
        String value = req.category();
        
        if (value == null || value.isBlank()) {
            return PriorityCategory.NORMAL;
        }
        
        try {
            return PriorityCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid category '" + value + "'. Use HIGHEST_PRIORITY, PREFERRED or NORMAL."
            );
        }
    }

    private static PatientResponse toPatientResponse(Patient p) {
        if (p == null) {
            return null;
        }

        return new PatientResponse(
            p.getId(), 
            p.getName(), 
            p.getAge(),
            p.getBirthDate() == null ? null : p.getBirthDate().toString()
        );
    }

    private static VitalsResponse toVitalsResponse(VitalSigns v) {
        if (v == null) {
            return null;
        }

        return new VitalsResponse(
            v.getSystolicPressure(), 
            v.getDiastolicPressure(), 
            v.getHeartRate(),
            v.getRespiratoryRate(), 
            v.getTemperature(), 
            v.getOxygenSaturation(), 
            v.getPainScale()
        );
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field '" + field + "'");
        }
        
        return value;
    }
}
