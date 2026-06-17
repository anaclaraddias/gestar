package br.unibh.gestar.interfaces.mapper;

import br.unibh.gestar.domain.MedicalCare;
import br.unibh.gestar.domain.Patient;
import br.unibh.gestar.domain.PriorityCategory;
import br.unibh.gestar.domain.VitalSigns;
import br.unibh.gestar.interfaces.dto.CareRequest;
import br.unibh.gestar.interfaces.dto.CareResponse;
import br.unibh.gestar.interfaces.dto.PatientResponse;
import br.unibh.gestar.interfaces.dto.QueueResponse;
import br.unibh.gestar.interfaces.dto.VitalsResponse;
import br.unibh.gestar.service.QueueStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class CareMapper {

    private CareMapper() {
    }

    public static Patient toPatient(CareRequest req) {
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

    public static VitalSigns toVitalSigns(CareRequest r) {
        return new VitalSigns(
                r.systolic() == null ? 120 : r.systolic(),
                r.diastolic() == null ? 80 : r.diastolic(),
                r.heartRate() == null ? 80 : r.heartRate(),
                r.respiratoryRate() == null ? 16 : r.respiratoryRate(),
                r.temperature() == null ? 36.5 : r.temperature(),
                r.spo2() == null ? 98 : r.spo2(),
                r.pain() == null ? 0 : r.pain());
    }

    public static boolean hasVitalSigns(CareRequest r) {
        return r.systolic() != null || r.diastolic() != null || r.heartRate() != null
                || r.respiratoryRate() != null || r.temperature() != null
                || r.spo2() != null || r.pain() != null;
    }

    public static PriorityCategory toCategory(CareRequest req) {
        String value = req.category();
        if (value == null || value.isBlank()) {
            return PriorityCategory.NORMAL;
        }
        try {
            return PriorityCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category '" + value
                    + "'. Use HIGHEST_PRIORITY, PREFERRED or NORMAL.");
        }
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field '" + field + "'");
        }
        return value;
    }

    public static CareResponse toResponse(MedicalCare c) {
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
                c.getDestinationUnit());
    }

    public static PatientResponse toPatientResponse(Patient p) {
        if (p == null) {
            return null;
        }
        return new PatientResponse(p.getId(), p.getName(), p.getAge(),
                p.getBirthDate() == null ? null : p.getBirthDate().toString());
    }

    public static VitalsResponse toVitalsResponse(VitalSigns v) {
        if (v == null) {
            return null;
        }
        return new VitalsResponse(v.getSystolicPressure(), v.getDiastolicPressure(), v.getHeartRate(),
                v.getRespiratoryRate(), v.getTemperature(), v.getOxygenSaturation(), v.getPainScale());
    }

    public static QueueResponse toQueueResponse(QueueStatus q) {
        MedicalCare n = q.next();
        QueueResponse.NextInQueue next = n == null ? null : new QueueResponse.NextInQueue(
                n.getId(),
                n.getUrgencyLevel() == null ? null : n.getUrgencyLevel().name(),
                n.getPatient() == null ? null : n.getPatient().getName());
        return new QueueResponse(q.red(), q.orange(), q.yellow(), q.green(), q.total(), next);
    }
}
