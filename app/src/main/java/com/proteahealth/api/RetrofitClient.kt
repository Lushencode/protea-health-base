package com.proteahealth.api

import android.content.Context
import com.proteahealth.data.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // if mysql stops  sudo /usr/local/mysql/support-files/mysql.server start
// after emulator restart ~/Library/Android/sdk/platform-tools/adb reverse tcp:8000 tcp:8000
    private const val BASE_URL = "http://127.0.0.1:8000/"

    private var retrofit: Retrofit? = null

    fun initialize(context: Context) {

        val sessionManager =
            SessionManager(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->

                val token = sessionManager.getAccessToken()

                val request = chain.request()
                    .newBuilder()
                    .apply {
                        if (token.isNotEmpty()) {
                            header(
                                "Authorization",
                                "Bearer $token"
                            )
                        }
                    }
                    .build()

                chain.proceed(request)
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val apiService: ApiService
        get() = requireNotNull(retrofit) {
            "RetrofitClient.initialize(context) must be called first"
        }.create(ApiService::class.java)
}