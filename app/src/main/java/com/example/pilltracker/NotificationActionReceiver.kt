package com.example.pilltracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "taken" || action == "not_taken") {
            val userName = "John Doe" // replace with actual user name
            val medicineName = "Medicine A" // replace with actual medicine name
            val status = if (action == "taken") "taken" else "not taken"
            val dateTaken = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

            val url = "https://group8.dhruvaldhameliya.com/insert_log.php" // replace with your actual API URL
            val data = "user=$userName&medicine_name=$medicineName&status=$status&date_taken=$dateTaken"
            println("action")
            SendHttpPostRequest().execute(url, data)
        }
    }

    private inner class SendHttpPostRequest : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val urlString = params[0]
            val postData = params[1]

            var result = ""

            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val outputStreamWriter = OutputStreamWriter(conn.outputStream)
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val inputStreamReader = InputStreamReader(conn.inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                bufferedReader.close()
                result = stringBuilder.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                Log.d("NotificationAction", "API response: $result")
            } else {
                Log.e("NotificationAction", "Error: API response is null")
            }
        }
    }
}
