package com.example.pilltracker

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.MapView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class PharmacyFragment : Fragment() {

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
        val bundle = arguments


        if (bundle != null) {
            val pharmacyId = bundle.getString("pharmId")
            if (pharmacyId != null) {
                if (pharmacyId.isNotEmpty()) {
                    //val bundle = arguments
                    //val pharmacyId = bundle!!.getString("pharmId")
                    Places.initialize(this.requireContext(), BuildConfig.MAPS_API_KEY)

                    val placesClient = Places.createClient(this.requireContext())

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

                }
            }

        }

        return view
    }

    companion object {
        fun newInstance(): PharmacyFragment {
            return PharmacyFragment()
        }
    }
}