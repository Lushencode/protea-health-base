package com.proteahealth.api



import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.proteahealth.api.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

object PharmacyOrderStatusUpdater {

    interface Callback {
        fun onSuccess(message: String)
        fun onError(message: String)
    }

    @JvmStatic
    fun update(
        owner: LifecycleOwner,
        orderId: Int,
        newStatus: String,
        callback: Callback
    ) {
        owner.lifecycleScope.launch {
            try {
                val request = UpdatePharmacyOrderStatusRequest(
                    orderId = orderId,
                    status = newStatus
                )

                val response = RetrofitClient.apiService
                    .updatePharmacyOrderStatus(request)

                val body = response.body()

                if (response.isSuccessful &&
                    body?.success == true
                ) {
                    callback.onSuccess(
                        body.message ?: "Order updated successfully"
                    )
                } else {
                    val errorText = response.errorBody()?.string()

                    val message = try {
                        JSONObject(errorText ?: "{}")
                            .optString(
                                "message",
                                "Failed to update order"
                            )
                    } catch (e: Exception) {
                        "Failed to update order"
                    }

                    callback.onError(message)
                }

            } catch (e: Exception) {
                callback.onError(
                    e.message ?: "Network error"
                )
            }
        }
    }
}
