package com.proteahealth.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("signup_patient.php")
    suspend fun signupPatient(
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("age") age: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("gender") gender: String,
        @Field("home_address") homeAddress: String,

        @Field("emergency_contact_name")
        emergencyContactName: String,

        @Field("emergency_contact_number")
        emergencyContactNumber: String,

        @Field("emergency_contact_relationship")
        emergencyContactRelationship: String,

        @Field("allergies")
        allergies: String,

        @Field("medical_conditions")
        medicalConditions: String,
        @Field("password") password: String
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("signup_doctor.php")
    suspend fun signupDoctor(
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("email") email: String,
        @Field("specialization") specialization: String,
        @Field("location") location: String,
        @Field("clinic_name") clinicName: String,
        @Field("years_in_professional") yearsInProfessional: String,
        @Field("consultation_price") consultationPrice: String,
        @Field("certification") certification: String,
        @Field("password") password: String
    ): Response<DoctorSignupResponse>

    @FormUrlEncoded
    @POST("signup_pharmacy.php")
    suspend fun signupPharmacy(
        @Field("pharmacy_name") pharmacyName: String,
        @Field("pharmacy_location") pharmacyLocation: String,
        @Field("open_hour") openHour: String,
        @Field("close_hour") closeHour: String,
        @Field("details") details: String,
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("registration_code") registrationCode: String,
        @Field("work_experience_years") workExperienceYears: String,
        @Field("password") password: String
    ): Response<PharmacySignupResponse>

    @FormUrlEncoded
    @POST("get_profile.php")
    suspend fun getProfile(
        @Field("id") id: String,
        @Field("role") role: String
    ): Response<ProfileResponse>
}