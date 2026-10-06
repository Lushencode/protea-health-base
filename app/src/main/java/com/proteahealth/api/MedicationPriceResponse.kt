package com.proteahealth.api

data class MedicationPriceResponse(
    val success: Boolean,
    val message: String,
    val pharmacies: List<MedicationPriceItem> = emptyList()
)

data class MedicationPriceItem(
    val medication_id: Int,
    val pharmacy_id: Int,

    val pharmacy_name: String,
    val pharmacy_location: String?,

    val open_hour: String?,
    val close_hours: String?,

    val offers_delivery: Int,
    val delivery_fee: String?,

    val medication_name: String,
    val price: String,
    val availability: String
)