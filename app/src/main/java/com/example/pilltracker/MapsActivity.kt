package com.example.pilltracker

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pilltracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()
    }

    private fun getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&    // Checks if we have FINE_LOCATION access
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  // Checks if we have COARSE_LOCATION access
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            location ->
            if(location != null){
                currentLocation = location

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            permissionCode -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))*/

        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Current Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 8f))

    }
}