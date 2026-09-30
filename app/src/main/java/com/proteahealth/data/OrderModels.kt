package com.proteahealth.data

enum class OrderStatus(val label: String) {
    RECEIVED("Received for Packing"),
    PACKED("Packed & Ready"),
    COLLECTED("Collected")
}

data class MedicationItem(
    val name: String,
    val dosage: String,
    val price: Double
)

data class PatientOrder(
    val id: String,
    val patientName: String,
    val patientSurname: String,
    val items: List<MedicationItem>,
    val collectionTime: String,
    var status: OrderStatus
) {
    val totalPrice: Double
        get() = items.sumOf { it.price }
}