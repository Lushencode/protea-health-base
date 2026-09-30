package com.proteahealth.api

data class SignupResponse(
    val success: Boolean,
    val message: String,
    val patient_id: String? = null
)