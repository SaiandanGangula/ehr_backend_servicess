CREATE TABLE facility (
  hospital_id   BIGSERIAL    PRIMARY KEY,
  facility_name VARCHAR(255),
  facility_type VARCHAR(255),
  block         VARCHAR(255),
  phc_chc_name  VARCHAR(255),
  location      VARCHAR(255),
  officer_in_charge VARCHAR(255),
  designation   VARCHAR(255),
  contact_number   VARCHAR(255),
  official_email   VARCHAR(255),
  network_id       VARCHAR(255),
  bed_strength     INTEGER,
  patient_types    VARCHAR(255),
  notes            TEXT,
  equipments       TEXT,
  created_at       TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at       TIMESTAMP WITH TIME ZONE DEFAULT now()
);
