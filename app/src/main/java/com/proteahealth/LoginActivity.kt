package com.proteahealth

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
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        roleSpinner = findViewById(R.id.roleSpinner)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signupTextView)

        // User types
        val roles = arrayOf(
            "Patient",
            "Doctor",
            "Pharmacy"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        roleSpinner.adapter = adapter

        // LOGIN BUTTON
        loginButton.setOnClickListener {

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val selectedRole = roleSpinner.selectedItem.toString()

            // Validate email
            if (email.isEmpty()) {
                emailEditText.error = "Enter your email"
                return@setOnClickListener
            }

            // Validate password
            if (password.isEmpty()) {
                passwordEditText.error = "Enter your password"
                return@setOnClickListener
            }

            // Convert dropdown value to PHP role
            val role = when (selectedRole) {

                "Patient" -> "patient"

                "Doctor" -> "doctor"

                "Pharmacy" -> "pharmacy"

                else -> ""
            }

            loginUser(email, password, role)
        }

        // SIGN UP
        signupTextView.setOnClickListener {

            val intent = Intent(
                this@LoginActivity,
                Signup_Role::class.java
            )

            startActivity(intent)
        }


    }

    private fun loginUser(
        email: String,
        password: String,
        role: String
    ) {

        // Prevent multiple login requests
        loginButton.isEnabled = false

        lifecycleScope.launch {

            try {

                val response = RetrofitClient.apiService.login(
                    email,
                    password,
                    role
                )

                if (response.isSuccessful) {

                    val loginResponse = response.body()

                    if (loginResponse?.success == true) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Login successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        when (role) {

                            "patient" -> {
                                val intent = Intent(
                                    this@LoginActivity,
                                    Patient_Home::class.java
                                )
                                startActivity(intent)
                            }

                            "doctor" -> {
                                val intent = Intent(
                                    this@LoginActivity,
                                    DoctorDashboardActivity::class.java
                                )
                                startActivity(intent)
                            }

                          /*  "pharmacy" -> {
                                val intent = Intent(
                                    this@LoginActivity,
                                    Pharmacy_Home::class.java
                                )
                                startActivity(intent)
                            }
                        }

                        finish()*/

                    }



                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            loginResponse?.message ?: "Login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        this@LoginActivity,
                        "Server error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    "Connection error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                loginButton.isEnabled = true
            }
        }
    }
}