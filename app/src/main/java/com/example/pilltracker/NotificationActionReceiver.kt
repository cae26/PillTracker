package com.example.pilltracker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Get the action from the intent
        val action = intent.action

        // Check the action and handle it accordingly
        when (action) {
            "com.example.pilltracker.TAKEN" -> {
                val title = intent.getStringExtra("title")
                val message = intent.getStringExtra("message")

                // Get the username from the SharedPreferences
                val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val user = sharedPreferences.getString("username", "")
                val taken = "Taken"

                // Call the insertLog function to insert a log for the taken action
                insertLog(context, taken, title!!, message!!, user!!)

                // Dismiss the notification
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }

            "com.example.pilltracker.NOT_TAKEN" -> {
                val title = intent.getStringExtra("title")
                val message = intent.getStringExtra("message")

                // Get the username from the SharedPreferences
                val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val user = sharedPreferences.getString("username", "")
                val taken = "Not Taken"

                // Call the insertLog function to insert a log for the not taken action
                insertLog(context, taken, title!!, message!!, user!!)

                // Dismiss the notification
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }
    }

    fun date(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun insertLog(context: Context, taken: String, title: String, message: String, user: String) {
        val url = "https://group8.dhruvaldhameliya.com/insert_log.php"

        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("userName", user)
            put("medicineName", message)
            put("status", taken)
            put("dateTaken", date())
        }

        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    requestBody.toString()
                )
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)

                    // Handle the response here
                } else {
                    // Handle the error here
                    Log.e("Error", "HTTP error code: ${response.code}")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "Error: Failed to insert log",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle the error here
                Log.e("Error", "Failed to fetch data: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

}
