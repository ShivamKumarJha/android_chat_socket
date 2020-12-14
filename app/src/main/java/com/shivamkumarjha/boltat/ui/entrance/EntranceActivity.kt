package com.shivamkumarjha.boltat.ui.entrance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shivamkumarjha.boltat.R
import com.shivamkumarjha.boltat.config.Constants
import com.shivamkumarjha.boltat.persistence.PreferenceManager
import com.shivamkumarjha.boltat.ui.BaseApplication
import com.shivamkumarjha.boltat.ui.chatroom.ChatRoomActivity
import com.shivamkumarjha.boltat.utility.Utility
import com.shivamkumarjha.boltat.utility.afterTextChanged

class EntranceActivity : BaseApplication() {

    private lateinit var rootLayout: LinearLayout
    private lateinit var nameEditText: EditText
    private lateinit var startButton: Button
    private lateinit var entranceViewModel: EntranceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
        initializer()
        setupPermissions()
        observeData()
    }

    private fun initializer() {
        rootLayout = findViewById(R.id.root_layout)
        nameEditText = findViewById(R.id.user_name_id)
        startButton = findViewById(R.id.start_button_id)
        entranceViewModel = ViewModelProvider(this).get(EntranceViewModel::class.java)

        if (PreferenceManager.get().userName != "") {
            nameEditText.setText(PreferenceManager.get().userName)
            startButton.isEnabled = true
        }
        nameEditText.afterTextChanged { entranceViewModel.nameListener(nameEditText.text.toString()) }
        startButton.setOnClickListener {
            PreferenceManager.get().userName = nameEditText.text.toString()
            val intent = Intent(this, ChatRoomActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeData() {
        entranceViewModel.entranceFormState.observe(this, {
            startButton.isEnabled = it.isDataValid
            if (it.nameError != null) {
                nameEditText.error = getString(it.nameError)
            }
        })
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(resources.getString(R.string.permission_request_storage))
                    .setTitle(resources.getString(R.string.permission_required))
                builder.setPositiveButton(
                    resources.getString(R.string.ok)
                ) { _, _ ->
                    requestPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Constants.PERMISSION_STORAGE
                    )
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Constants.PERMISSION_STORAGE
                )
            }
        }
    }

    private fun requestPermission(permission: String, permissionCode: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            permissionCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSION_STORAGE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Utility.get()
                        .getSnackBar(
                            rootLayout,
                            resources.getString(R.string.permission_storage_denied),
                            Snackbar.LENGTH_LONG
                        )
                        .show()
                } else {
                    Utility.get()
                        .getSnackBar(
                            rootLayout,
                            resources.getString(R.string.permission_storage_granted),
                            Snackbar.LENGTH_LONG
                        )
                        .show()
                }
            }
        }
    }
}