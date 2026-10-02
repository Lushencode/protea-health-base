package com.proteahealth.data

import com.proteahealth.api.ProfileUser
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object EmergencyProfileLoader {

    @JvmStatic
    fun loadPatientProfile(
        patientId: String,
        onSuccess: (ProfileUser) -> Unit,
        onError: (String) -> Unit
    ): Job {

        return CoroutineScope(Dispatchers.IO).launch {

            try {

                val response =
                    RetrofitClient.apiService.getProfile(
                        id = patientId,
                        role = "patient"
                    )

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body?.success == true && body.user != null) {

                        withContext(Dispatchers.Main) {
                            onSuccess(body.user)
                        }

                    } else {

                        withContext(Dispatchers.Main) {
                            onError(
                                body?.message
                                    ?: "Unable to load patient"
                            )
                        }
                    }

                } else {

                    withContext(Dispatchers.Main) {
                        onError(
                            "Server error: ${response.code()}"
                        )
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    onError(
                        e.message ?: "Connection error"
                    )
                }
            }
        }
    }
}