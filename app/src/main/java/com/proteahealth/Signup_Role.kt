package com.proteahealth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Signup_Role : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup_role)

        val patientButton = findViewById<Button>(R.id.patientButton)
        val doctorButton = findViewById<Button>(R.id.doctorButton)
        val pharmacyButton = findViewById<Button>(R.id.pharmacyButton)

        patientButton.setOnClickListener {

            startActivity(
                Intent(this, Patient_Signup::class.java)
            )
        }

        doctorButton.setOnClickListener {

            startActivity(
                Intent(this, Doctor_Signup::class.java)
            )
        }

        pharmacyButton.setOnClickListener {

            startActivity(
                Intent(this, Pharmacy_Signup::class.java)
            )
        }
    }
}