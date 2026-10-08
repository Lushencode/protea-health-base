package com.proteahealth.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val user: User? = null,
    @SerializedName("access_token")
val accessToken: String? = null,

@SerializedName("expires_at")
val expiresAt: String? = null
)

data class User(
    val id: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null
)
