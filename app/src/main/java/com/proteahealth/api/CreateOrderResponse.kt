package com.proteahealth.api

data class CreateOrderResponse(
    val success: Boolean,
    val message: String,
    val order_id: Int? = null
)