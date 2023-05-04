package com.example.pilltracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pilltracker.BuildConfig.MAPS_API_KEY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pilltracker.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONObject

private const val DEFAULT_ZOOM = 14

private const val KEY_CAMERA_POSITION = "camera_position"
private const val KEY_LOCATION = "location"
private const val permissionCode = 1

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var lastKnownLocation: Location? = null

    private var cameraPosition: CameraPosition? = null

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    private var locationPermissionGranted = true

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        /*if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }*/

        Places.initialize(applicationContext, MAPS_API_KEY)
        placesClient = Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                mMap.addMarker(MarkerOptions().position(place.latLng).title(place.name).snippet(place.id))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, DEFAULT_ZOOM.toFloat()))
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMap.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {

        locationPermissionGranted = false

        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        mMap.setOnMarkerClickListener(this)

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                            googleMapsApi(
                                LatLng(
                                    lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                DEFAULT_ZOOM.toFloat()
                            )
                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun googleMapsApi(latlng: LatLng) {

        val nearbyPlaceUrl =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlng.latitude + "%2C" + latlng.longitude + "&radius=2000&type=pharmacy&key=${MAPS_API_KEY}"

        val queue = Volley.newRequestQueue(this)

        Log.e("CUSTOM---->", nearbyPlaceUrl)
        val stringRequest =
            StringRequest(Request.Method.GET, nearbyPlaceUrl, { response ->

                val jsonData = JSONObject(response)

                val nearbyPlacesArray = jsonData.getJSONArray("results")

                for(i in 0 until nearbyPlacesArray.length())
                {
                    val place = nearbyPlacesArray.getJSONObject(i)

                    val pharmacyName = place.getString("name")
                    val pharmacyLat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    val pharmacyLng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    val pharmacyId = place.getString("place_id")
                    val placeLatLng = LatLng(pharmacyLat, pharmacyLng)

                    mMap.addMarker(MarkerOptions().position(placeLatLng).title(pharmacyName).snippet(pharmacyId))
                }

                //Log.e("CUSTOM---->", "__" + response)
            }, { e ->
                Log.e("CUSTOM---->", e.message.toString())

            })


        queue.add(stringRequest)


    }

    override fun onMarkerClick(p0: Marker): Boolean {

        val intent = Intent(this@MapsActivity, MapsDetail::class.java)
        intent.putExtra("pharmId", p0.snippet)
        startActivity(intent)

        return false
    }
}