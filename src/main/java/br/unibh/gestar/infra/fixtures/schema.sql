CREATE TABLE IF NOT EXISTS patient (
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    birth_date  DATE
);

CREATE TABLE IF NOT EXISTS medical_care (
    id                  VARCHAR(36) PRIMARY KEY,
    patient_id          VARCHAR(36) NOT NULL REFERENCES patient (id),
    main_complaint      TEXT,
    priority_category   VARCHAR(32) NOT NULL,
    arrival_date_time   TIMESTAMP   NOT NULL,
    urgency_level       VARCHAR(16),
    status              VARCHAR(32) NOT NULL,
    referral_reason     TEXT,
    destination_unit    TEXT,
    systolic            INTEGER,
    diastolic           INTEGER,
    heart_rate          INTEGER,
    respiratory_rate    INTEGER,
    temperature         DOUBLE PRECISION,
    oxygen_saturation   INTEGER,
    pain_scale          INTEGER
);

CREATE INDEX IF NOT EXISTS idx_medical_care_status ON medical_care (status);
CREATE INDEX IF NOT EXISTS idx_medical_care_arrival ON medical_care (arrival_date_time);
