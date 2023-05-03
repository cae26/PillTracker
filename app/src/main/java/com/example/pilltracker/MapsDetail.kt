package com.example.pilltracker

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pilltracker.BuildConfig.MAPS_API_KEY
import com.example.pilltracker.databinding.ActivityMapsDetailBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
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
        val pharmacyImage = findViewById<ImageView>(R.id.imageView)
        val day2 = findViewById<TextView>(R.id.day2TV)
        val day3 = findViewById<TextView>(R.id.day3TV)
        val day4 = findViewById<TextView>(R.id.day4TV)
        val day5 = findViewById<TextView>(R.id.day5TV)
        val day6 = findViewById<TextView>(R.id.day6TV)
        val day7 = findViewById<TextView>(R.id.day7TV)

        val pharmacyId = intent.getStringExtra("pharmId")

        val username = sharedPreferences.getString("username", "")
        homePharmacyButton.setOnClickListener {
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

            finish()
        }


        Places.initialize(applicationContext, MAPS_API_KEY)
        val placesClient = Places.createClient(this)

        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.OPENING_HOURS,
            Place.Field.PHOTO_METADATAS
        )

        val request = FetchPlaceRequest.newInstance(pharmacyId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val metada2 = place.photoMetadatas

                if(metada2 != null)
                {
                    val photoMetadata = metada2.first()

                    // Get the attribution text.
                    val attributions = photoMetadata?.attributions

                    // Create a FetchPhotoRequest.
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(700) // Optional.
                        .setMaxHeight(500) // Optional.
                        .build()
                    placesClient.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                            val bitmap = fetchPhotoResponse.bitmap
                            pharmacyImage.setImageBitmap(bitmap)
                        }.addOnFailureListener { exception: Exception ->
                            if (exception is ApiException) {
                                Log.e(ContentValues.TAG, "Place not found: " + exception.message)
                                val statusCode = exception.statusCode
                                TODO("Handle error with given status code.")
                            }
                        }
                }

                if (place.name != null)
                    pharmacyName.text = place.name

                if (place.address != null)
                    pharmacyAddress.text = place.address

                if (place.phoneNumber != null)
                    pharmacyNumber.text = place.phoneNumber

                if (place.openingHours != null) {
                    pharmacyOpening.text = place.openingHours?.weekdayText?.get(0).toString()
                    day2.text = place.openingHours?.weekdayText?.get(1).toString()
                    day3.text = place.openingHours?.weekdayText?.get(2).toString()
                    day4.text = place.openingHours?.weekdayText?.get(3).toString()
                    day5.text = place.openingHours?.weekdayText?.get(4).toString()
                    day6.text = place.openingHours?.weekdayText?.get(5).toString()
                    day7.text = place.openingHours?.weekdayText?.get(6).toString()
                }
            }.addOnFailureListener { e ->
                Log.e("CUSTOM_ERROR---->", e.message.toString())
            }


    }
    //${place.address} ${place.phoneNumber} ${place.openingHours.weekdayText[0]}
}
