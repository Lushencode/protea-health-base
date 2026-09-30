package com.proteahealth.api

data class DoctorSignupResponse(
    val success: Boolean,
    val message: String,
    val doctor_id: String? = null,
    val verification: String? = null
)