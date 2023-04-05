package com.example.pilltracker

import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PillTrackerAPI {

    lateinit var name:String
    lateinit var price:String
    lateinit var content:String
    lateinit var companyName:String
    var searhTerm = "ibuprofen"




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
                    /*println(message.toString())*/
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


}