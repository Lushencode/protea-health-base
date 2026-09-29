package com.proteahealth

enum class UserRole {
    PATIENT,
    DOCTOR,
    PHARMACIST
}

data class PatientProfile(
    val name: String,
    val surname: String,
    val idNumber: String,
    val age: Int,
    val email: String,
    val phone: String,
    val homeAddress: String,
    val emergencyContacts: String,
    val personalConditions: List<String>,
    val prescriptions: List<String>
)

data class DoctorProfile(
    val name: String,
    val surname: String,
    val specialization: String,
    val location: String,
    val clinicName: String,
    val yearsInProfession: Int,
    val consultationPrice: String,
    val certification: String
)

data class PharmacistProfile(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val pharmacyLocation: String,
    val certification: String,
    val workExperienceYears: Int,
    val workingHours: String
)