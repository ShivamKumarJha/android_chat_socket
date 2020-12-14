package com.shivamkumarjha.boltat.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shivamkumarjha.boltat.ui.dialog.OfflineDialog

open class BaseApplication : AppCompatActivity() {

    private lateinit var offlineDialog: OfflineDialog
    var isInternetAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        offlineDialog = OfflineDialog(this)
        offlineDialog.setCanceledOnTouchOutside(false)
        observeConnection()
    }

    private fun observeConnection() {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectionLiveData = ConnectionLiveData(connectivityManager)
        connectionLiveData.observe(this,
            {
                isInternetAvailable = it
                if (it) {
                    offlineDialog.dismiss()
                } else {
                    offlineDialog.show()
                }
            })
    }
}