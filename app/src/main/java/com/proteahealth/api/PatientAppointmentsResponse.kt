package com.proteahealth.api

data class PatientAppointmentsResponse(
    val success: Boolean,
    val message: String,
    val appointments: List<PatientAppointment> = emptyList()
)

data class PatientAppointment(
    val id: Int,
    val patient_id: Int,
    val doctor_id: Int,
    val appointment_type: String,
    val appointment_date: String,
    val appointment_time: String,
    val reason: String,
    val accepted: Boolean?,
    val status: String,
    val doctor_response: String?,
    val doctor_name: String,
    val doctor_surname: String,
    val specialization: String?,
    val clinic_name: String?,
    val location: String?
) {
    fun getDoctorDisplayName(): String {
        return "Dr $doctor_name $doctor_surname"
    }
}