package com.proteahealth.api

data class AppointmentCreateResponse(
    val success: Boolean,
    val message: String,
    val appointment_id: Int? = null
)