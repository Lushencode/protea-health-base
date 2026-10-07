package com.proteahealth.api

data class DoctorResponse(
    val success: Boolean,
    val message: String,
    val doctors: List<AppointmentDoctor> = emptyList()
)

data class AppointmentDoctor(
    val id: Int,
    val name: String,
    val surname: String,
    val specialization: String?,
    val clinic_name: String?,
    val location: String?,
    val consultation_price: String?
) {
    fun getDisplayName(): String {
        return "Dr $name $surname"
    }
}