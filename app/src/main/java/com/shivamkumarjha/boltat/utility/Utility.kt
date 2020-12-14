package com.shivamkumarjha.boltat.utility

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.shivamkumarjha.boltat.BuildConfig
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class Utility {

    private val dateFormat = "yyyy-MM-dd"
    private val tag = "Utility"

    companion object {
        private var INSTANCE: Utility? = null

        fun get(): Utility {
            if (INSTANCE == null) {
                INSTANCE = Utility()
            }
            return INSTANCE!!
        }
    }

    fun getSnackBar(view: View, message: String, length: Int): Snackbar {
        return Snackbar.make(view, message, length)
    }

    fun openLinkInBrowser(context: Context, url: String) {
        if (URLUtil.isValidUrl(url)) {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            context.startActivity(openURL)
        }
    }

    fun debugToast(context: Context, message: String) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            Log.d(tag, message)
        }
    }

    fun getBitmapFromString(image: String): Bitmap {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 999
        return calendar.time
    }

    fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    fun currentDate(): String {
        var utcTime = ""
        try {
            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            utcTime = URLEncoder.encode(sdf.format(getEndOfDay(Date())), "utf-8")
        } catch (e: java.lang.Exception) {
        }
        return utcTime
    }

    fun previousDate(): String {
        var utcTime = ""
        try {
            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            utcTime = URLEncoder.encode(sdf.format(getStartOfDay(Date())), "utf-8")
        } catch (e: Exception) {
        }
        return utcTime
    }
}