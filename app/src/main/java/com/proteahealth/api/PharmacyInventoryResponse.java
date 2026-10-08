package com.proteahealth.api;

import com.proteahealth.data.PharmacyInventoryItem;

import java.util.List;

public class PharmacyInventoryResponse {

    private boolean success;
    private String message;
    private int pharmacy_id;
    private int total_medications;
    private List<PharmacyInventoryItem> medications;

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getPharmacyId() {
        return pharmacy_id;
    }

    public int getTotalMedications() {
        return total_medications;
    }

    public List<PharmacyInventoryItem> getMedications() {
        return medications;
    }
}
