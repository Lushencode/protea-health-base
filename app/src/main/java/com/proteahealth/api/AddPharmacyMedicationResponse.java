
package com.proteahealth.api;

public class AddPharmacyMedicationResponse {

    private boolean success;
    private String message;
    private int medication_id;

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getMedicationId() {
        return medication_id;
    }
}

