package com.proteahealth.data

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "ProteaHealthSession",
        Context.MODE_PRIVATE
    )

    fun saveUser(
        id: String,
        name: String,
        surname: String,
        email: String,
        role: String
    ) {
        preferences.edit()
            .putString("user_id", id)
            .putString("user_name", name)
            .putString("user_surname", surname)
            .putString("user_email", email)
            .putString("user_role", role)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun getUserId(): String {
        return preferences.getString("user_id", "") ?: ""
    }

    fun getName(): String {
        return preferences.getString("user_name", "") ?: ""
    }

    fun getSurname(): String {
        return preferences.getString("user_surname", "") ?: ""
    }

    fun getEmail(): String {
        return preferences.getString("user_email", "") ?: ""
    }

    fun getRole(): String {
        return preferences.getString("user_role", "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean("is_logged_in", false)
    }

    fun logout() {
        preferences.edit().clear().apply()
    }
}