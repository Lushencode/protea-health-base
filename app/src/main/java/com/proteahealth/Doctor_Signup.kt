package com.proteahealth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.launch

class Doctor_Signup : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var specializationEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var clinicNameEditText: EditText
    private lateinit var yearsInProfessionEditText: EditText
    private lateinit var consultationPriceEditText: EditText
    private lateinit var certificationEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var signupButton: Button
    private lateinit var loginTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_doctor_signup)


        // Connect XML fields

        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        specializationEditText =
            findViewById(R.id.specializationEditText)

        locationEditText =
            findViewById(R.id.locationEditText)

        clinicNameEditText =
            findViewById(R.id.clinicNameEditText)

        yearsInProfessionEditText =
            findViewById(R.id.yearsInProfessionEditText)

        consultationPriceEditText =
            findViewById(R.id.consultationPriceEditText)

        certificationEditText =
            findViewById(R.id.certificationEditText)

        passwordEditText =
            findViewById(R.id.passwordEditText)

        confirmPasswordEditText =
            findViewById(R.id.confirmPasswordEditText)

        signupButton =
            findViewById(R.id.signupButton)

        loginTextView =
            findViewById(R.id.loginTextView)


        // Create account

        signupButton.setOnClickListener {

            createDoctorAccount()
        }


        // Login

        loginTextView.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }


    private fun createDoctorAccount() {

        val name =
            nameEditText.text.toString().trim()

        val surname =
            surnameEditText.text.toString().trim()

        val email =
            emailEditText.text.toString().trim()

        val specialization =
            specializationEditText.text.toString().trim()

        val location =
            locationEditText.text.toString().trim()

        val clinicName =
            clinicNameEditText.text.toString().trim()

        val yearsInProfession =
            yearsInProfessionEditText.text.toString().trim()

        val consultationPrice =
            consultationPriceEditText.text.toString().trim()

        val certification =
            certificationEditText.text.toString().trim()

        val password =
            passwordEditText.text.toString()

        val confirmPassword =
            confirmPasswordEditText.text.toString()


        // Validation

        if (name.isEmpty()) {
            nameEditText.error = "Enter your name"
            return
        }

        if (surname.isEmpty()) {
            surnameEditText.error = "Enter your surname"
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Enter your email"
            return
        }

        if (specialization.isEmpty()) {
            specializationEditText.error =
                "Enter your specialization"
            return
        }

        if (location.isEmpty()) {
            locationEditText.error =
                "Enter your location"
            return
        }

        if (clinicName.isEmpty()) {
            clinicNameEditText.error =
                "Enter your clinic name"
            return
        }

        if (yearsInProfession.isEmpty()) {
            yearsInProfessionEditText.error =
                "Enter your years in profession"
            return
        }

        if (consultationPrice.isEmpty()) {
            consultationPriceEditText.error =
                "Enter your consultation price"
            return
        }

        if (certification.isEmpty()) {
            certificationEditText.error =
                "Enter your certification"
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error =
                "Enter a password"
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


        // Disable button while sending request

        signupButton.isEnabled = false


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.signupDoctor(

                        name = name,

                        surname = surname,

                        email = email,

                        specialization = specialization,

                        location = location,

                        clinicName = clinicName,

                        yearsInProfessional =
                            yearsInProfession,

                        consultationPrice =
                            consultationPrice,

                        certification = certification,

                        password = password
                    )


                if (response.isSuccessful) {

                    val signupResponse =
                        response.body()


                    if (signupResponse?.success == true) {

                        Toast.makeText(
                            this@Doctor_Signup,
                            "Doctor account created successfully!",
                            Toast.LENGTH_LONG
                        ).show()


                        // Return to login

                        val intent = Intent(
                            this@Doctor_Signup,
                            LoginActivity::class.java
                        )

                        startActivity(intent)

                        finish()

                    } else {

                        Toast.makeText(
                            this@Doctor_Signup,
                            signupResponse?.message
                                ?: "Signup failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@Doctor_Signup,
                        "Server error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@Doctor_Signup,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                signupButton.isEnabled = true
            }
        }
    }
}