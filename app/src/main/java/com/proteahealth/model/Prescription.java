package com.proteahealth.model;

import java.io.Serializable;

public class Prescription implements Serializable {

    private final String id;
    private final String medicationName;
    private final int quantity;
    private final String prescribingDoctor;
    private final String doctorContact;
    private final String dosage;
    private final String appearance;
    private final String purpose;
    private final String scheduledTime;
    private final int totalDoses;
    private int remainingDoses;
    private boolean takenToday;

    public Prescription(String id, String medicationName, int quantity, String prescribingDoctor,
                        String doctorContact, String dosage, String appearance, String purpose,
                        String scheduledTime, int totalDoses, int remainingDoses, boolean takenToday) {
        this.id = id;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.prescribingDoctor = prescribingDoctor;
        this.doctorContact = doctorContact;
        this.dosage = dosage;
        this.appearance = appearance;
        this.purpose = purpose;
        this.scheduledTime = scheduledTime;
        this.totalDoses = totalDoses;
        this.remainingDoses = remainingDoses;
        this.takenToday = takenToday;
    }

    public String getId() {
        return id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrescribingDoctor() {
        return prescribingDoctor;
    }

    public String getDoctorContact() {
        return doctorContact;
    }

    public String getDosage() {
        return dosage;
    }

    public String getAppearance() {
        return appearance;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public int getTotalDoses() {
        return totalDoses;
    }

    public int getRemainingDoses() {
        return remainingDoses;
    }

    public void setRemainingDoses(int remainingDoses) {
        this.remainingDoses = remainingDoses;
    }

    public boolean isTakenToday() {
        return takenToday;
    }

    public void setTakenToday(boolean takenToday) {
        this.takenToday = takenToday;
    }
}