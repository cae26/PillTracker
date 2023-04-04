package com.example.pilltracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.Request




class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when(item.itemId){
                R.id.nav_myPills -> fragment = MyPillsFragment()
                R.id.nav_log -> fragment = LogFragment()
                R.id.nav_pharmacy -> fragment = PharmacyFragment()
            }
            replaceFragment(fragment)
            true
        }

        // Disable navigation buttons by default
        setNavigationButtonsEnabled(false)

        // Show LoginFragment initially
        replaceFragment(LoginFragment.newInstance(::onLoginSuccess))
    }

    private fun replaceFragment(pillTrackerFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pill_tracker_frame_layout, pillTrackerFragment)
        fragmentTransaction.commit()
    }

    private fun setNavigationButtonsEnabled(isEnabled: Boolean) {
        bottomNavigationView.menu.findItem(R.id.nav_myPills).isEnabled = isEnabled
        bottomNavigationView.menu.findItem(R.id.nav_log).isEnabled = isEnabled
        bottomNavigationView.menu.findItem(R.id.nav_pharmacy).isEnabled = isEnabled
    }

    private fun onLoginSuccess() {
        setNavigationButtonsEnabled(true)
        bottomNavigationView.selectedItemId = R.id.nav_myPills
    }
}