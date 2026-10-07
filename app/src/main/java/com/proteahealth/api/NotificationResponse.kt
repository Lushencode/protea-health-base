package com.proteahealth.api

data class NotificationResponse(
    val success: Boolean,
    val notifications: List<NotificationItem>?,
    val message: String?
)

data class NotificationItem(
    val notification_id: Int,
    val patient_id: Int,
    val doctor_id: Int?,
    val pharmacist_id: Int?,
    val message: String,
    val sent_at: String,
    val is_read: Boolean
)