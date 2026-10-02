package com.proteahealth.Signup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proteahealth.LoginActivity
import com.proteahealth.R
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.launch

class Patient_Signup : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var homeAddressEditText: EditText

    private lateinit var emergencyContactNameEditText: EditText

    private lateinit var emergencyContactNumberEditText: EditText

    private lateinit var emergencyContactRelationshipEditText: EditText

    private lateinit var allergiesEditText: EditText

    private lateinit var medicalConditionsEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_patient_signup)


        // Connect Kotlin variables to XML
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)

        genderSpinner = findViewById(R.id.genderSpinner)

        homeAddressEditText = findViewById(R.id.homeAddressEditText)

        emergencyContactNameEditText =
            findViewById(R.id.emergencyContactNameEditText)

        emergencyContactNumberEditText =
            findViewById(R.id.emergencyContactNumberEditText)

        emergencyContactRelationshipEditText =
            findViewById(R.id.emergencyContactRelationshipEditText)

        allergiesEditText =
            findViewById(R.id.allergiesEditText)

        medicalConditionsEditText =
            findViewById(R.id.medicalConditionsEditText)

        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText =
            findViewById(R.id.confirmPasswordEditText)

        signupButton = findViewById(R.id.signupButton)
        loginTextView = findViewById(R.id.loginTextView)


        // Gender dropdown
        val genders = arrayOf(
            "Male",
            "Female",
            "Other",
            "Prefer not to say"
        )

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genders
        )

        genderSpinner.adapter = genderAdapter


        // Create account button
        signupButton.setOnClickListener {

            createPatientAccount()
        }


        // Login link
        loginTextView.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }


    private fun createPatientAccount() {

        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        val homeAddress =
            homeAddressEditText.text.toString().trim()

        val emergencyContactName =
            emergencyContactNameEditText.text.toString().trim()

        val emergencyContactNumber =
            emergencyContactNumberEditText.text.toString().trim()

        val emergencyContactRelationship =
            emergencyContactRelationshipEditText.text.toString().trim()

        val allergies =
            allergiesEditText.text.toString().trim()

        val medicalConditions =
            medicalConditionsEditText.text.toString().trim()

        val password =
            passwordEditText.text.toString()

        val confirmPassword =
            confirmPasswordEditText.text.toString()


        // Basic validation

        if (name.isEmpty()) {
            nameEditText.error = "Enter your name"
            return
        }

        if (surname.isEmpty()) {
            surnameEditText.error = "Enter your surname"
            return
        }

        if (age.isEmpty()) {
            ageEditText.error = "Enter your age"
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Enter your email"
            return
        }

        if (phone.isEmpty()) {
            phoneEditText.error = "Enter your phone number"
            return
        }

        if (homeAddress.isEmpty()) {
            homeAddressEditText.error = "Enter your home address"
            return
        }
        if (emergencyContactName.isEmpty()) {
            emergencyContactNameEditText.error =
                "Enter emergency contact name"
            return
        }

        if (emergencyContactNumber.isEmpty()) {
            emergencyContactNumberEditText.error =
                "Enter emergency contact number"
            return
        }

        if (emergencyContactRelationship.isEmpty()) {
            emergencyContactRelationshipEditText.error =
                "Enter relationship"
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Enter a password"
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error =
                "Confirm your password"
            return
        }

        if (password != confirmPassword) {

            confirmPasswordEditText.error =
                "Passwords do not match"

            return
        }


        // Convert spinner value to database value

        val gender = when (
            genderSpinner.selectedItem.toString()
        ) {

            "Male" -> "male"

            "Female" -> "female"

            "Other" -> "other"

            "Prefer not to say" -> "prefer_not_to_say"

            else -> "other"
        }


        signupButton.isEnabled = false


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.signupPatient(

                        name = name,

                        surname = surname,

                        age = age,

                        email = email,

                        phone = phone,

                        gender = gender,

                        homeAddress = homeAddress,

                        emergencyContactName = emergencyContactName,

                        emergencyContactNumber = emergencyContactNumber,

                        emergencyContactRelationship = emergencyContactRelationship,

                        allergies = allergies,

                        medicalConditions = medicalConditions,

                        password = password
                    )


                if (response.isSuccessful) {

                    val signupResponse =
                        response.body()


                    if (signupResponse?.success == true) {

                        Toast.makeText(
                            this@Patient_Signup,
                            "Account created successfully!",
                            Toast.LENGTH_LONG
                        ).show()


                        // Send user back to Login

                        val intent = Intent(
                            this@Patient_Signup,
                            LoginActivity::class.java
                        )

                        startActivity(intent)

                        finish()

                    } else {

                        Toast.makeText(
                            this@Patient_Signup,
                            signupResponse?.message
                                ?: "Signup failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@Patient_Signup,
                        "Server error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@Patient_Signup,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                signupButton.isEnabled = true
            }
        }
    }
}