package com.example.pilltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.typeOf


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testTV = findViewById<TextView>(R.id.textViewResult)

        fun run() {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://medicine-search-and-autocomplete.p.rapidapi.com/api/medicine/1")
                .get()
                .addHeader("X-RapidAPI-Key", "a0ed068df0mshe9a4c601fa4afcep1fee3cjsn0921916ebd40")
                .addHeader("X-RapidAPI-Host", "medicine-search-and-autocomplete.p.rapidapi.com")
                .build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {


                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")


                        /*testTV.setText(response.body!!.string())*/
                        println(response.body!!.string())




                    }
                }
            })
        }

        run()



    }
}