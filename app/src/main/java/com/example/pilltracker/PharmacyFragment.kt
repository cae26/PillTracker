package com.example.pilltracker

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
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.MapView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
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

        //val pharmacyId = intent.getStringExtra("pharmId")
        //val pharmacyId = intent

    }

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
       // val bundle = arguments
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
                        Place.Field.OPENING_HOURS
                    )

                    val request = FetchPlaceRequest.newInstance(pharmacyId.toString(), placeFields)

                    placesClient.fetchPlace(request)
                        .addOnSuccessListener { response: FetchPlaceResponse ->
                            val place = response.place

                            if (place.name != null)
                                homePharmName.text = place.name

                            if (place.address != null)
                                homePharmAddress.text = place.address

                            if (place.phoneNumber != null)
                                homePharmPhoneNumber.text = place.phoneNumber

                            if (place.openingHours != null)
                                homePharmOpening.text = place.openingHours?.weekdayText.toString()
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
      //  if (bundle != null) {
            //val pharmacyId = bundle.getString("pharmId")
            //if (pharmacyId != null) {
                //if (pharmacyId.isNotEmpty()) {
                    //val bundle = arguments
                    //val pharmacyId = bundle!!.getString("pharmId")


        //        }
        //    }

      //  }


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