package com.proteahealth.api


import com.google.gson.annotations.SerializedName

data class UpdatePharmacyOrderStatusResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("order_id")
    val orderId: Int?,

    @SerializedName("previous_status")
    val previousStatus: String?,

    @SerializedName("new_status")
    val newStatus: String?
)