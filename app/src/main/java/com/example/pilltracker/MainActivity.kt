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
import org.json.JSONException



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testResult = findViewById<TextView>(R.id.textViewResult)
        var name:String
        lateinit var price:String
        lateinit var content:String
        lateinit var companyName:String




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


                        var jsonString = response.body!!.string()

                        val obj = JSONObject(jsonString)
                        val data: JSONObject = obj.getJSONObject("data")
                        name = data.getString("name")
                        price = data.getString("price")
                        content = data.getString("content")
                        companyName = data.getString("companyName")
                        println(data.toString())
                        println("PillName: " + name)
                        println("Price: " + price)
                        println("Content: " + content)
                        println("CompanyName: " + companyName)

                        /*testTV.text = response.body!!.string()*/
                        /*println(response.body!!.string())*/


                    }
                }
            })
        }

        run()



    }
}