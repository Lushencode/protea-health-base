package com.proteahealth.api


import com.google.gson.annotations.SerializedName

data class UpdatePharmacyOrderStatusRequest(
    @SerializedName("order_id")
    val orderId: Int,

    @SerializedName("status")
    val status: String
)