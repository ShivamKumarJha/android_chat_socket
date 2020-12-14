package com.shivamkumarjha.boltat.persistence

import android.content.Context
import android.content.SharedPreferences
import com.shivamkumarjha.boltat.config.Constants

class PreferenceManager(context: Context) {

    private val pref: SharedPreferences
    private var privateMode = 0

    companion object {
        private var INSTANCE: PreferenceManager? = null

        fun initialize(context: Context): PreferenceManager? {
            if (INSTANCE == null) {
                INSTANCE = PreferenceManager(context)
            }
            return INSTANCE
        }

        fun get(): PreferenceManager {
            return if (INSTANCE != null) {
                INSTANCE!!
            } else {
                throw IllegalStateException("Please initialize PreferenceManager before getting the instance!")
            }
        }
    }

    init {
        pref = context.getSharedPreferences(Constants.PREF_NAME, privateMode)
    }

    var userName: String
        get() = pref.getString(Constants.PREFERENCE_NAME, "")!!
        set(userName) {
            pref.edit().putString(Constants.PREFERENCE_NAME, userName).apply()
        }
}
