package com.proteahealth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.proteahealth.data.SessionManager
import androidx.appcompat.app.AppCompatActivity
import com.proteahealth.Patient.EmergencyActivity
import com.proteahealth.Patient.MedicationPriceComparisonActivity
import com.proteahealth.Patient.MedicationsActivity
import com.proteahealth.Patient.Patient_Home
import com.proteahealth.api.NotificationResponse
import com.proteahealth.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationContainer: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notification)

        notificationContainer =
            findViewById(R.id.notificationContainer)

        setupBottomNavigation()

        // Get logged-in user
        val sessionManager = SessionManager(this)

        val userId = sessionManager.getUserId()
        val role = sessionManager.getRole()

        // Make sure the logged-in user is a patient
        if (role.equals("patient", ignoreCase = true)) {

            val patientId = userId?.toIntOrNull()

            if (patientId != null) {
                loadNotifications(patientId)
            } else {
                Toast.makeText(
                    this,
                    "Invalid patient ID",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "This notification page is for patients only.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun loadNotifications(patientId: Int) {

        RetrofitClient.apiService
            .getNotifications(patientId)
            .enqueue(object : Callback<NotificationResponse> {

                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {

                    if (!response.isSuccessful) {

                        Toast.makeText(
                            this@NotificationActivity,
                            "Server error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val notificationResponse = response.body()

                    if (notificationResponse == null) {

                        Toast.makeText(
                            this@NotificationActivity,
                            "Empty server response",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    if (!notificationResponse.success) {

                        Toast.makeText(
                            this@NotificationActivity,
                            notificationResponse.message
                                ?: "Could not load notifications.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    val notifications =
                        notificationResponse.notifications

                    notificationContainer.removeAllViews()

                    if (notifications.isNullOrEmpty()) {

                        val emptyMessage =
                            TextView(this@NotificationActivity).apply {

                                text = "You have no notifications."

                                textSize = 16f

                                setPadding(
                                    20,
                                    40,
                                    20,
                                    40
                                )
                            }

                        notificationContainer.addView(emptyMessage)

                        return
                    }

                    for (notification in notifications) {

                        val notificationView: View =
                            LayoutInflater
                                .from(this@NotificationActivity)
                                .inflate(
                                    R.layout.item_notification,
                                    notificationContainer,
                                    false
                                )

                        val messageText =
                            notificationView.findViewById<TextView>(
                                R.id.txtNotificationMessage
                            )

                        val dateText =
                            notificationView.findViewById<TextView>(
                                R.id.txtNotificationDate
                            )

                        messageText.text =
                            notification.message

                        dateText.text =
                            notification.sent_at

                        notificationContainer.addView(
                            notificationView
                        )
                    }
                }

                override fun onFailure(
                    call: Call<NotificationResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@NotificationActivity,
                        "Connection error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun setupBottomNavigation() {

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


        navProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        navSetting.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MedicationPriceComparisonActivity::class.java
                )
            )
        }

        navHome.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    Patient_Home::class.java
                )
            )
        }

        navEmergency.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EmergencyActivity::class.java
                )
            )
        }

        navMed.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MedicationsActivity::class.java
                )
            )
        }
    }
}