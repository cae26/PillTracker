package com.example.pilltracker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class PharmacyFragment : Fragment() {
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pharmacy, container, false)
        val mapButton = view.findViewById<Button>(R.id.accessMapButton)

        mapButton.setOnClickListener {
            val intent = Intent(this@PharmacyFragment.requireContext(), MapsActivity::class.java)
            startActivity(intent)
        }

        val homePharmName = view.findViewById<TextView>(R.id.homePharmacyNameTV)
        val homePharmAddress = view.findViewById<TextView>(R.id.homePharmacyAddressTV)
        val homePharmPhoneNumber = view.findViewById<TextView>(R.id.homePharmacyPhoneNumberTV)
        val homePharmOpening = view.findViewById<TextView>(R.id.homePharmacyOpeningHoursTV)
        val homePharmacyPic = view.findViewById<ImageView>(R.id.pharmacyPic)
        val day2 = view.findViewById<TextView>(R.id.day2TV)
        val day3 = view.findViewById<TextView>(R.id.day3TV)
        val day4 = view.findViewById<TextView>(R.id.day4TV)
        val day5 = view.findViewById<TextView>(R.id.day5TV)
        val day6 = view.findViewById<TextView>(R.id.day6TV)
        val day7 = view.findViewById<TextView>(R.id.day7TV)


        var username = sharedPreferences.getString("username", "")
        println(username)
        if (username != null) {
            sendJsonRequest(username) { pharmacyId ->
                if (pharmacyId != null) {
                    // Use the retrieved pharmacy ID value as needed
                    Places.initialize(this.requireContext(), BuildConfig.MAPS_API_KEY)

                    val placesClient = Places.createClient(this.requireContext())

                    val placeFields = listOf(
                        Place.Field.NAME,
                        Place.Field.ADDRESS,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.OPENING_HOURS,
                        Place.Field.PHOTO_METADATAS
                    )

                    val request = FetchPlaceRequest.newInstance(pharmacyId.toString(), placeFields)

                    placesClient.fetchPlace(request)
                        .addOnSuccessListener { response: FetchPlaceResponse ->
                            val place = response.place
                            val metada = place.photoMetadatas

                            if(metada != null)
                            {
                                val photoMetadata = metada.first()

                                // Get the attribution text.
                                val attributions = photoMetadata?.attributions

                                // Create a FetchPhotoRequest.
                                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(600) // Optional.
                                    .setMaxHeight(400) // Optional.
                                    .build()
                                placesClient.fetchPhoto(photoRequest)
                                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                                        val bitmap = fetchPhotoResponse.bitmap
                                        homePharmacyPic.setImageBitmap(bitmap)
                                    }.addOnFailureListener { exception: Exception ->
                                        if (exception is ApiException) {
                                            Log.e(TAG, "Place not found: " + exception.message)
                                            val statusCode = exception.statusCode
                                            TODO("Handle error with given status code.")
                                        }
                                    }
                            }

                            if (place.name != null)
                                homePharmName.text = place.name

                            if (place.address != null)
                                homePharmAddress.text = place.address

                            if (place.phoneNumber != null)
                                homePharmPhoneNumber.text = place.phoneNumber

                            if (place.openingHours != null) {
                                homePharmOpening.text = place.openingHours?.weekdayText?.get(0).toString()
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
                    Log.d("MainActivity", "Pharmacy ID: $pharmacyId")
                } else {
                    // Handle the error
                    Log.e("MainActivity", "Failed to fetch pharmacy ID")
                }
            }
        }

        return view
    }


    private fun sendJsonRequest(userName: String, callback: (String?) -> Unit) {
        val url = "https://group8.dhruvaldhameliya.com/retrive_placeid.php"

        val client = OkHttpClient()

        // Create the JSON request body
        val requestBody = JSONObject().apply {
            put("userName", userName)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonArray = JSONArray(responseBody)

                val jsonObject = jsonArray.optJSONObject(0)
                val placeId = jsonObject?.optString("placeId")

                // Pass the place ID value to the callback function
                callback(placeId)
            }

            override fun onFailure(call: Call, e: IOException) {
                // Pass null to the callback function in case of failure
                callback(null)
            }
        })
    }


    companion object {
        fun newInstance(): PharmacyFragment {
            return PharmacyFragment()
        }
    }
}