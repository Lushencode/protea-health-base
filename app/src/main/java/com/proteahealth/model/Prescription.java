package com.proteahealth.model;

import java.io.Serializable;

public class Prescription implements Serializable {

    private final String id;
    private final String medicationName;
    private final int quantity;
    private final String prescribingDoctor;

    public Prescription(String id, String medicationName, int quantity, String prescribingDoctor) {
        this.id = id;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.prescribingDoctor = prescribingDoctor;
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
}