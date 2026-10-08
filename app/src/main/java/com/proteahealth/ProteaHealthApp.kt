package com.proteahealth

import android.app.Application
import com.proteahealth.api.RetrofitClient

class ProteaHealthApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}