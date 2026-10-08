package com.proteahealth.api

import com.proteahealth.data.PharmacyOrder

import com.google.gson.annotations.SerializedName

data class PharmacyOrdersResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("pharmacy_name")
    val pharmacyName: String?,

    @SerializedName("total_orders")
    val totalOrders: Int,

    @SerializedName("orders")
    val orders: List<PharmacyOrder>?
)