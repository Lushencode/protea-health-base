package com.proteahealth.Signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proteahealth.LoginActivity
import com.proteahealth.R
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.launch

class Pharmacy_Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pharmacy_signup)

        // Pharmacy fields
        val pharmacyNameEditText =
            findViewById<EditText>(R.id.pharmacyNameEditText)

        val pharmacyLocationEditText =
            findViewById<EditText>(R.id.pharmacyLocationEditText)

        val openTimePicker =
            findViewById<TimePicker>(R.id.openTimePicker)

        val closeTimePicker =
            findViewById<TimePicker>(R.id.closeTimePicker)

        val pharmacyDetailsEditText =
            findViewById<EditText>(R.id.pharmacyDetailsEditText)

        // Pharmacist fields
        val nameEditText =
            findViewById<EditText>(R.id.nameEditText)

        val surnameEditText =
            findViewById<EditText>(R.id.surnameEditText)

        val emailEditText =
            findViewById<EditText>(R.id.emailEditText)

        val phoneEditText =
            findViewById<EditText>(R.id.phoneEditText)

        val registrationCodeEditText =
            findViewById<EditText>(R.id.registrationCodeEditText)

        val workExperienceEditText =
            findViewById<EditText>(R.id.workExperienceEditText)

        val passwordEditText =
            findViewById<EditText>(R.id.passwordEditText)

        val confirmPasswordEditText =
            findViewById<EditText>(R.id.confirmPasswordEditText)

        val signupButton =
            findViewById<Button>(R.id.signupButton)

        val loginTextView =
            findViewById<TextView>(R.id.loginTextView)


        signupButton.setOnClickListener {

            val pharmacyName =
                pharmacyNameEditText.text.toString().trim()

            val pharmacyLocation =
                pharmacyLocationEditText.text.toString().trim()

            val details =
                pharmacyDetailsEditText.text.toString().trim()

            val name =
                nameEditText.text.toString().trim()

            val surname =
                surnameEditText.text.toString().trim()

            val email =
                emailEditText.text.toString().trim()

            val phone =
                phoneEditText.text.toString().trim()

            val registrationCode =
                registrationCodeEditText.text.toString().trim()

            val workExperience =
                workExperienceEditText.text.toString().trim()

            val password =
                passwordEditText.text.toString()

            val confirmPassword =
                confirmPasswordEditText.text.toString()


            // Convert opening time to HH:mm:ss
            val openHour = String.format(
                "%02d:%02d:00",
                openTimePicker.hour,
                openTimePicker.minute
            )

            // Convert closing time to HH:mm:ss
            val closeHour = String.format(
                "%02d:%02d:00",
                closeTimePicker.hour,
                closeTimePicker.minute
            )


            // Validation
            if (
                pharmacyName.isEmpty() ||
                pharmacyLocation.isEmpty() ||
                name.isEmpty() ||
                surname.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                registrationCode.isEmpty() ||
                workExperience.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please complete all required fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val experience = workExperience.toIntOrNull()

            if (experience == null || experience < 0) {
                Toast.makeText(
                    this,
                    "Please enter a valid number of years",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (openHour == closeHour) {
                Toast.makeText(
                    this,
                    "Opening and closing times cannot be the same",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Prevent multiple submissions
            signupButton.isEnabled = false


            lifecycleScope.launch {

                try {

                    val response =
                        RetrofitClient.apiService.signupPharmacy(
                            pharmacyName = pharmacyName,
                            pharmacyLocation = pharmacyLocation,
                            openHour = openHour,
                            closeHour = closeHour,
                            details = details,
                            name = name,
                            surname = surname,
                            email = email,
                            phone = phone,
                            registrationCode = registrationCode,
                            workExperienceYears = workExperience,
                            password = password
                        )


                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(
                            this@Pharmacy_Signup,
                            "Pharmacy account created! Awaiting admin approval.",
                            Toast.LENGTH_LONG
                        ).show()

                        startActivity(
                            Intent(
                                this@Pharmacy_Signup,
                                LoginActivity::class.java
                            )
                        )

                        finish()

                    } else {

                        Toast.makeText(
                            this@Pharmacy_Signup,
                            response.body()?.message
                                ?: "Registration failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        this@Pharmacy_Signup,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                } finally {

                    signupButton.isEnabled = true
                }
            }
        }


        // Login link
        loginTextView.setOnClickListener {

            startActivity(
                Intent(
                    this@Pharmacy_Signup,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }
}