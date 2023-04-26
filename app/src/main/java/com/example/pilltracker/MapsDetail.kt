package com.example.pilltracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pilltracker.BuildConfig.MAPS_API_KEY
import com.example.pilltracker.databinding.ActivityMapsDetailBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class MapsDetail : AppCompatActivity() {

    private lateinit var binding: ActivityMapsDetailBinding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        binding = ActivityMapsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pharmacyName = findViewById<TextView>(R.id.pharmacyNameTV)
        val pharmacyAddress = findViewById<TextView>(R.id.pharmacyAddressTV)
        val pharmacyNumber = findViewById<TextView>(R.id.pharmacyPhoneNumberTV)
        val pharmacyOpening = findViewById<TextView>(R.id.pharmacyOpeningHoursTV)
        val homePharmacyButton = findViewById<Button>(R.id.homePharmacyButton)

        val pharmacyId = intent.getStringExtra("pharmId")


        // Testing out sending data back to fragment for home pharmacy
//        val mFragmentManager = supportFragmentManager
//        val mFragmentTransaction = mFragmentManager.beginTransaction()
//        val mFragment = PharmacyFragment()

        // val username = intent.getStringExtra("username")
        //  val data = intent.getStringExtra("myData") ?: ""
        //val username = intent.getStringExtra("username") ?: ""
        val username = sharedPreferences.getString("username", "")
        homePharmacyButton.setOnClickListener {
//            val mBundle = Bundle()
//            mBundle.putString("pharmId", pharmacyId)
//            mFragment.arguments = mBundle
            //val intent = Intent(this@MapsDetail, PharmacyFragment::class.java)
            //intent.putExtra("pharmId", pharmacyId)
            //startActivity(intent)
            println(username)
            println(pharmacyId)
            val url = "https://group8.dhruvaldhameliya.com/insert_placeid.php"

            val client = OkHttpClient()

            // Create the JSON request body
            val requestBody = JSONObject().apply {
                put("userName", username)
                put("placeId", pharmacyId)
            }

            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)

                    val success = jsonResponse.optBoolean("success")

                    // Pass the success value to the callback function
                   // callback(if (success) pharmacyId else null)
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Pass null to the callback function in case of failure
                   // callback(null)
                }
            })
        }


        Places.initialize(applicationContext, MAPS_API_KEY)
        val placesClient = Places.createClient(this)

        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.OPENING_HOURS
        )

        val request = FetchPlaceRequest.newInstance(pharmacyId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                if (place.name != null)
                    pharmacyName.text = place.name

                if (place.address != null)
                    pharmacyAddress.text = place.address

                if (place.phoneNumber != null)
                    pharmacyNumber.text = place.phoneNumber

                if (place.openingHours != null)
                    pharmacyOpening.text = place.openingHours?.weekdayText.toString()
            }.addOnFailureListener { e ->
                Log.e("CUSTOM_ERROR---->", e.message.toString())
            }


    }
    //${place.address} ${place.phoneNumber} ${place.openingHours.weekdayText[0]}
}
