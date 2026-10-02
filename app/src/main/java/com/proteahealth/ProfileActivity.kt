package com.proteahealth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.widget.SwitchCompat
import com.proteahealth.Patient.EmergencyActivity
import com.proteahealth.Patient.MedicationsActivity
import com.proteahealth.Patient.Patient_Home
import com.proteahealth.api.RetrofitClient
import com.proteahealth.data.SessionManager
import com.proteahealth.Patient.PharmacyActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import android.app.Activity

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        // ------------------------------------------------
        // COMMON PROFILE VIEWS
        // ------------------------------------------------

        val tvProfileRole =
            findViewById<TextView>(R.id.tvProfileRole)

        val tvName =
            findViewById<TextView>(R.id.tvName)

        val tvSurname =
            findViewById<TextView>(R.id.tvSurname)

        val tvEmail =
            findViewById<TextView>(R.id.tvEmail)


        // ------------------------------------------------
        // ROLE SECTIONS
        // ------------------------------------------------

        val patientSection =
            findViewById<LinearLayout>(R.id.patientSection)

        val doctorSection =
            findViewById<LinearLayout>(R.id.doctorSection)

        val pharmacySection =
            findViewById<LinearLayout>(R.id.pharmacySection)

        val patientBottomNav =
            findViewById<View>(R.id.patientBottomNav)

        val doctorBottomNav =
            findViewById<View>(R.id.doctorBottomNav)



        // ------------------------------------------------
        // PATIENT VIEWS
        // ------------------------------------------------

        val tvPatientAge =
            findViewById<TextView>(R.id.tvPatientAge)

        val tvPatientPhone =
            findViewById<TextView>(R.id.tvPatientPhone)

        val tvPatientGender =
            findViewById<TextView>(R.id.tvPatientGender)

        val tvPatientAddress =
            findViewById<TextView>(R.id.tvPatientAddress)

        val tvEmergencyContactName =
            findViewById<TextView>(R.id.tvEmergencyContactName)

        val tvEmergencyContactNumber =
            findViewById<TextView>(R.id.tvEmergencyContactNumber)

        val tvEmergencyContactRelationship =
            findViewById<TextView>(R.id.tvEmergencyContactRelationship)

        val tvAllergies =
            findViewById<TextView>(R.id.tvAllergies)

        val tvMedicalConditions =
            findViewById<TextView>(R.id.tvMedicalConditions)

        val tvRiskLevel =
            findViewById<TextView>(R.id.tvRiskLevel)


        // ------------------------------------------------
        // DOCTOR VIEWS
        // ------------------------------------------------

        val tvSpecialization =
            findViewById<TextView>(R.id.tvSpecialization)

        val tvDoctorLocation =
            findViewById<TextView>(R.id.tvDoctorLocation)

        val tvClinicName =
            findViewById<TextView>(R.id.tvClinicName)

        val tvDoctorExperience =
            findViewById<TextView>(R.id.tvDoctorExperience)

        val tvConsultationPrice =
            findViewById<TextView>(R.id.tvConsultationPrice)

        val tvDoctorCertification =
            findViewById<TextView>(R.id.tvDoctorCertification)

        val tvDoctorVerification =
            findViewById<TextView>(R.id.tvDoctorVerification)


        // ------------------------------------------------
        // PHARMACY / PHARMACIST VIEWS
        // ------------------------------------------------

        val tvPharmacyLocation =
            findViewById<TextView>(R.id.tvPharmacyLocation)

        val tvPharmacistPhone =
            findViewById<TextView>(R.id.tvPharmacistPhone)

        val tvRegistrationCode =
            findViewById<TextView>(R.id.tvRegistrationCode)

        val tvPharmacistExperience =
            findViewById<TextView>(R.id.tvPharmacistExperience)

        val tvPharmacyVerification =
            findViewById<TextView>(R.id.tvPharmacyVerification)


        // ------------------------------------------------
        // SETTINGS
        // ------------------------------------------------

        val switchDarkMode =
            findViewById<SwitchCompat>(R.id.switchDarkMode)

        val btnLogout =
            findViewById<Button>(R.id.btnLogout)


        // ------------------------------------------------
        // SESSION
        // ------------------------------------------------

        val id = sessionManager.getUserId()

        val role = sessionManager
            .getRole()
            .trim()
            .lowercase()


        // Show role at top
        tvProfileRole.text =
            role.replaceFirstChar { it.uppercase() }


        // ------------------------------------------------
        // ROLE VISIBILITY
        // ------------------------------------------------

        patientSection.visibility = View.GONE
        doctorSection.visibility = View.GONE
        pharmacySection.visibility = View.GONE

        patientBottomNav.visibility = View.GONE
        doctorBottomNav.visibility = View.GONE

        when (role) {

            "patient" -> {
                patientSection.visibility = View.VISIBLE
                patientBottomNav.visibility = View.VISIBLE
            }

            "doctor" -> {
                doctorSection.visibility = View.VISIBLE
                doctorBottomNav.visibility = View.VISIBLE
            }

            "pharmacist", "pharmacy" -> {
                pharmacySection.visibility = View.VISIBLE
            }
        }

        // ------------------------------------------------
// DARK MODE
// ------------------------------------------------

        val themePreferences =
            getSharedPreferences(
                "theme_preferences",
                MODE_PRIVATE
            )

        val darkModeEnabled =
            themePreferences.getBoolean(
                "dark_mode",
                false
            )

        switchDarkMode.setOnCheckedChangeListener(null)
        switchDarkMode.isChecked = darkModeEnabled

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            themePreferences.edit()
                .putBoolean("dark_mode", isChecked)
                .apply()

            val newMode =
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }

            if (AppCompatDelegate.getDefaultNightMode() != newMode) {

                AppCompatDelegate.setDefaultNightMode(newMode)

                // Remove the activity transition animation
                overrideActivityTransition(
                    Activity.OVERRIDE_TRANSITION_OPEN,
                    0,
                    0
                )
            }
        }

        // ------------------------------------------------
        // LOAD PROFILE FROM MYSQL
        // ------------------------------------------------

            lifecycleScope.launch {

                try {

                    val response =
                        RetrofitClient.apiService.getProfile(
                            id = id,
                            role = role
                        )

                    if (response.isSuccessful) {

                        val profileResponse =
                            response.body()

                        if (
                            profileResponse?.success == true &&
                            profileResponse.user != null
                        ) {

                            val user =
                                profileResponse.user


                            // COMMON DATA

                            tvName.text =
                                user.name ?: "-"

                            tvSurname.text =
                                user.surname ?: "-"

                            tvEmail.text =
                                user.email ?: "-"


                            // --------------------------------
                            // PATIENT
                            // --------------------------------

                            if (role == "patient") {

                                tvPatientAge.text =
                                    "Age: ${user.age ?: "-"}"

                                tvPatientPhone.text =
                                    "Phone: ${user.phone ?: "-"}"

                                tvPatientGender.text =
                                    "Gender: ${user.gender ?: "-"}"

                                tvPatientAddress.text =
                                    "Home Address: ${user.home_address ?: "-"}"

                                tvEmergencyContactName.text =
                                    "Emergency Contact Name: ${user.emergency_contact_name ?: "-"}"

                                tvEmergencyContactNumber.text =
                                    "Emergency Contact Number: ${user.emergency_contact_number ?: "-"}"

                                tvEmergencyContactRelationship.text =
                                    "Relationship: ${user.emergency_contact_relationship ?: "-"}"

                                tvAllergies.text =
                                    "Allergies: ${user.allergies ?: "-"}"

                                tvMedicalConditions.text =
                                    "Medical Conditions: ${user.medical_conditions ?: "-"}"

                                tvRiskLevel.text =
                                    "Risk Level: ${user.risk_level ?: "-"}"
                            }


                            // --------------------------------
                            // DOCTOR
                            // --------------------------------

                            if (role == "doctor") {

                                tvSpecialization.text =
                                    "Specialization: ${user.specialization ?: "-"}"

                                tvDoctorLocation.text =
                                    "Location: ${user.location ?: "-"}"

                                tvClinicName.text =
                                    "Clinic: ${user.clinic_name ?: "-"}"

                                tvDoctorExperience.text =
                                    "Professional Experience: ${user.years_in_professional ?: "-"} years"

                                tvConsultationPrice.text =
                                    "Consultation Price: R${user.consultation_price ?: "-"}"

                                tvDoctorCertification.text =
                                    "Certification: ${user.certification ?: "-"}"

                                tvDoctorVerification.text =
                                    "Verification: ${user.verification ?: "-"}"
                            }


                            // --------------------------------
                            // PHARMACIST
                            // --------------------------------

                            if (role == "pharmacist") {

                                tvPharmacyLocation.text =
                                    "Location: ${user.pharmacy_location ?: "-"}"

                                tvPharmacistPhone.text =
                                    "Phone: ${user.phone ?: "-"}"

                                tvRegistrationCode.text =
                                    "Registration Code: ${user.registration_code ?: "-"}"

                                tvPharmacistExperience.text =
                                    "Work Experience: ${user.work_experience_years ?: "-"} years"

                                tvPharmacyVerification.text =
                                    "Verification: ${user.verification ?: "-"}"
                            }


                            // --------------------------------
                            // PHARMACY
                            // --------------------------------

                            if (role == "pharmacy") {

                                tvPharmacyLocation.text =
                                    "Location: ${user.location ?: "-"}"

                                tvPharmacistPhone.text =
                                    "Opening Hour: ${user.open_hour ?: "-"}"

                                tvRegistrationCode.text =
                                    "Closing Hour: ${user.close_hours ?: "-"}"

                                tvPharmacistExperience.text =
                                    "Details: ${user.details ?: "-"}"

                                tvPharmacyVerification.visibility =
                                    View.GONE
                            }

                        } else {

                            Toast.makeText(
                                this@ProfileActivity,
                                profileResponse?.message
                                    ?: "Unable to load profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        Toast.makeText(
                            this@ProfileActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: CancellationException) {
                    // Normal when the Activity is recreated or closed.
                    // Do not show a connection error.
                    throw e
                } catch (e: Exception) {

                    android.util.Log.e(
                        "PROFILE_ERROR",
                        "Failed to load profile",
                        e
                    )

                    Toast.makeText(
                        this@ProfileActivity,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        // ------------------------------------------------
        // PATIENT BOTTOM NAV BUTTONS
        // ------------------------------------------------
// PATIENT BOTTOM NAV BUTTONS
// ------------------------------------------------

        if (role == "patient") {

            val navProfile =
                findViewById<ImageButton>(R.id.navProfile)

            val navSetting =
                findViewById<ImageButton>(R.id.navSetting)

            val navHome =
                findViewById<ImageButton>(R.id.navhome)

            val navEmergency =
                findViewById<ImageButton>(R.id.navEmergency)

            val navMed =
                findViewById<ImageButton>(R.id.navMed)


            // PROFILE - already on this page
            navProfile.setOnClickListener {
                // Do nothing
            }


            // HOME
            navHome.setOnClickListener {

                val intent = Intent(
                    this,
                    Patient_Home::class.java
                )

                startActivity(intent)
            }


            // MEDICATION
            navMed.setOnClickListener {

                val intent = Intent(
                    this,
                    MedicationsActivity::class.java
                )

                startActivity(intent)
            }


            // SETTINGS
            navSetting.setOnClickListener {

                val intent = Intent(
                    this,
                    PharmacyActivity::class.java
                )
                startActivity(intent)
            }


            // EMERGENCY
            navEmergency.setOnClickListener {

                val intent = Intent(
                    this,
                    EmergencyActivity::class.java
                )
                startActivity(intent)
            }
            navProfile.setBackgroundResource(R.drawable.nav_icon_glow)

        }

        // ------------------------------------------------
// DOCTOR BOTTOM NAV BUTTONS
// ------------------------------------------------

        if (role == "doctor") {

            val navDoctorProfile =
                findViewById<ImageButton>(R.id.navDoctorProfile)

            val navDoctorPatients =
                findViewById<ImageButton>(R.id.navDoctorPatients)

            val navDoctorHome =
                findViewById<ImageButton>(R.id.navDoctorHome)

            val navDoctorAppointments =
                findViewById<ImageButton>(R.id.navDoctorAppointments)

            val navDoctorQuestions =
                findViewById<ImageButton>(R.id.navDoctorQuestions)


            // PROFILE - already on this page
            navDoctorProfile.setOnClickListener {
                // Do nothing
            }


            // PATIENTS
            navDoctorPatients.setOnClickListener {

                Toast.makeText(
                    this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }


            // HOME
            navDoctorHome.setOnClickListener {

                val intent = Intent(
                    this,
                    DoctorDashboardActivity::class.java
                )

                startActivity(intent)
            }


            // APPOINTMENTS
            navDoctorAppointments.setOnClickListener {

                Toast.makeText(
                    this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }


            // QUESTIONS
            navDoctorQuestions.setOnClickListener {

                Toast.makeText(
                    this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
                ).show()
            }


            // PROFILE GLOW
            navDoctorProfile.setBackgroundResource(
                R.drawable.nav_icon_glow
            )
        }


        // ------------------------------------------------
        // LOGOUT
        // ------------------------------------------------

        btnLogout.setOnClickListener {

            sessionManager.logout()

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}