package br.unibh.gestar.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class MedicalCare {
    private final String id;
    private final Patient patient;
    private final String mainComplaint;
    private final PriorityCategory priorityCategory;
    private final LocalDateTime arrivalDateTime;

    private VitalSigns vitalSigns;
    private UrgencyLevel urgencyLevel;
    private MedicalCareStatus status;
    private String referralReason;
    private String destinationUnit;

    public MedicalCare(
        Patient patient, 
        String mainComplaint,
        PriorityCategory priorityCategory
    ) {
        this.id = UUID.randomUUID().toString();
        this.patient = patient;
        this.mainComplaint = mainComplaint;
        this.priorityCategory = priorityCategory;
        this.arrivalDateTime = LocalDateTime.now();
        this.status = MedicalCareStatus.WAITING_FOR_TRIAGE;
    }

    private MedicalCare(String id, Patient patient, String mainComplaint,
                        PriorityCategory priorityCategory, LocalDateTime arrivalDateTime) {
        this.id = id;
        this.patient = patient;
        this.mainComplaint = mainComplaint;
        this.priorityCategory = priorityCategory;
        this.arrivalDateTime = arrivalDateTime;
    }

    /**
     * Rebuilds a medical care from stored data (e.g. a database row), preserving
     * its original id and arrival time. Used by repositories to rehydrate; the
     * public constructor is for brand-new care that generates its own id.
     */
    public static MedicalCare fromPersistence(String id, Patient patient, String mainComplaint,
                                              PriorityCategory priorityCategory, LocalDateTime arrivalDateTime,
                                              VitalSigns vitalSigns, UrgencyLevel urgencyLevel,
                                              MedicalCareStatus status, String referralReason,
                                              String destinationUnit) {
        MedicalCare care = new MedicalCare(id, patient, mainComplaint, priorityCategory, arrivalDateTime);
        care.vitalSigns = vitalSigns;
        care.urgencyLevel = urgencyLevel;
        care.status = status;
        care.referralReason = referralReason;
        care.destinationUnit = destinationUnit;
        return care;
    }

    public void setClassification(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public void reclassify(UrgencyLevel newUrgencyLevel) {
        if (this.urgencyLevel == null || newUrgencyLevel.getPriority() < this.urgencyLevel.getPriority()) {
            this.urgencyLevel = newUrgencyLevel;
        }
    }

    public void markReferred(String referralReason, String destinationUnit) {
        this.referralReason = referralReason;
        this.destinationUnit = destinationUnit;
        this.status = MedicalCareStatus.REFERRED;
    }

    public void advanceStatus(MedicalCareStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isCritical() {
        return this.urgencyLevel == UrgencyLevel.RED;
    }

    public void setVitalSigns(VitalSigns vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public String getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    /** @deprecated use {@link #getPatient()}; kept for backward compatibility. */
    @Deprecated
    public Patient getPaciente() {
        return patient;
    }

    public String getMainComplaint() {
        return mainComplaint;
    }

    public PriorityCategory getPriorityCategory() {
        return priorityCategory;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public VitalSigns getVitalSigns() {
        return vitalSigns;
    }

    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public MedicalCareStatus getStatus() {
        return status;
    }

    public String getReferralReason() {
        return referralReason;
    }

    public String getDestinationUnit() {
        return destinationUnit;
    }
}
