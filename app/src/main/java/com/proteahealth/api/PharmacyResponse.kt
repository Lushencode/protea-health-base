package com.proteahealth.api

data class PharmacyResponse(
    val success: Boolean,
    val message: String,
    val pharmacies: List<Pharmacy> = emptyList()
)

data class Pharmacy(
    val id: Int,
    val name: String,
    val location: String?,
    val open_hour: String?,
    val close_hours: String?,
    val details: String?,
    val offers_delivery: Int,
    val delivery_fee: String?
)