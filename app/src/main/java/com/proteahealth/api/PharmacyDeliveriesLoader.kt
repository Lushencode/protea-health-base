
package com.proteahealth.api

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object PharmacyDeliveriesLoader {

    interface Callback {
        fun onSuccess(response: PharmacyOrdersResponse)
        fun onError(message: String)
    }

    @JvmStatic
    fun load(owner: LifecycleOwner, callback: Callback) {
        owner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService
                    .getPharmacyDeliveryOrders("delivery")

                if (response.isSuccessful) {
                    val result = response.body()

                    if (result?.success == true) {
                        callback.onSuccess(result)
                    } else {
                        callback.onError(
                            result?.message
                                ?: "Unable to retrieve deliveries"
                        )
                    }
                } else {
                    val errorText = response.errorBody()?.string()

                    val message = try {
                        JSONObject(errorText ?: "{}")
                            .optString(
                                "message",
                                "Unable to retrieve deliveries"
                            )
                    } catch (e: Exception) {
                        "Unable to retrieve deliveries"
                    }

                    callback.onError(message)
                }

            } catch (e: Exception) {
                Log.e(
                    "PHARMACY_DELIVERIES",
                    "Failed to load deliveries",
                    e
                )

                callback.onError(
                    "Connection error. Please try again."
                )
            }
        }
    }
}
