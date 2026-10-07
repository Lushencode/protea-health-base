package com.proteahealth.api

data class DoctorAvailabilityResponse(
    val success: Boolean,
    val message: String,
    val doctor_id: Int?,
    val date: String?,
    val slots: List<String> = emptyList()
)