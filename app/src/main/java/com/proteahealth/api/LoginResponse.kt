package com.proteahealth.api

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String? = null,
    val user: User? = null
)

data class User(
    val id: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null
)