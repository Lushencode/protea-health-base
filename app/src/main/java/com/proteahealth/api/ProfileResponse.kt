package com.proteahealth.api

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val user: ProfileUser? = null
)

data class ProfileUser(
    val id: Int? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,

    // Patient
    val age: Int? = null,
    val phone: String? = null,
    val gender: String? = null,
    val home_address: String? = null,
    val emergency_contact_name: String? = null,
    val emergency_contact_number: String? = null,
    val emergency_contact_relationship: String? = null,
    val allergies: String? = null,
    val medical_conditions: String? = null,
    val risk_level: String? = null,

    // Doctor
    val specialization: String? = null,
    val location: String? = null,
    val clinic_name: String? = null,
    val years_in_professional: Int? = null,
    val consultation_price: String? = null,
    val certification: String? = null,
    val verification: String? = null,

    // Pharmacist
    val pharmacy_id: Int? = null,
    val pharmacy_location: String? = null,
    val registration_code: String? = null,
    val work_experience_years: Int? = null,

    // Pharmacy
    val open_hour: String? = null,
    val close_hours: String? = null,
    val details: String? = null
)