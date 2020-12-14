package com.shivamkumarjha.boltat.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.shivamkumarjha.boltat.R

class OfflineDialog(activity: Activity) : Dialog(activity),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_offline)

    }

    override fun onClick(v: View) {

    }
}