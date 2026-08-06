package com.example.panaqet

import android.app.Application
import com.paystack.android.core.Paystack
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PanaQetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Paystack.builder()
            .setPublicKey("pk_test_1d9466561fc23dd7fbd21901b159a802e0ea0fff")
            .setLoggingEnabled(true)
            .build()
    }
}
