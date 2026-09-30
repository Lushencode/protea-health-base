package com.proteahealth.api

data class PharmacySignupResponse(
    val success: Boolean,
    val message: String,
    val pharmacy_id: String? = null,
    val pharmacist_id: String? = null
)