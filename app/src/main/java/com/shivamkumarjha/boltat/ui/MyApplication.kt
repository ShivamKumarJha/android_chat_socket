package com.shivamkumarjha.boltat.ui

import android.app.Application
import com.shivamkumarjha.boltat.persistence.PreferenceManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.initialize(applicationContext)
    }
}