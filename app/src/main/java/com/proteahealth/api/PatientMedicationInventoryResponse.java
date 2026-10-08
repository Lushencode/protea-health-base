
package com.proteahealth.api;

import java.util.List;


import com.proteahealth.model.PatientMedicationInventory;

public class PatientMedicationInventoryResponse {

    private boolean success;
    private String message;
    private int total_medications;
    private List<PatientMedicationInventory> medications;

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalMedications() {
        return total_medications;
    }

    public List<PatientMedicationInventory> getMedications() {
        return medications;
    }
}

