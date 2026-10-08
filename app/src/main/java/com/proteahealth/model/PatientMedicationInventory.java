package com.proteahealth.model;


public class PatientMedicationInventory {

    private int id;
    private int order_id;
    private String medication_name;
    private int quantity_received;
    private String received_at;
    private String pharmacy_name;

    public int getId() {
        return id;
    }

    public int getOrderId() {
        return order_id;
    }

    public String getMedicationName() {
        return medication_name;
    }

    public int getQuantityReceived() {
        return quantity_received;
    }

    public String getReceivedAt() {
        return received_at;
    }

    public String getPharmacyName() {
        return pharmacy_name;
    }
}

