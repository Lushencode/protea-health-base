package com.proteahealth.api

data class MedicationResponse(
    val success: Boolean,
    val message: String,
    val medications: List<Medication> = emptyList()
)

data class Medication(
    val prescription_id: Int,
    val patient_id: Int,
    val doctor_id: Int,
    val medication_id: Int,

    val medication_name: String,

    val quantity: Int,
    val dosage: String?,
    val purpose: String?,
    val scheduled_time: String?,
    var remaining_doses: Int,
    var last_taken_date: String?,

    val date_prescription: String,

    val price: String,
    val availability: String,
    val pharmacy_id: Int
)