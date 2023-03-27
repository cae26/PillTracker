package com.example.pilltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myPillsPage: Fragment = MyPillsFragment()
        val logPage: Fragment = LogFragment()
        val pharmacyPage: Fragment = PharmacyFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when(item.itemId){
                R.id.nav_myPills -> fragment = myPillsPage
                R.id.nav_log -> fragment = logPage
                R.id.nav_pharmacy -> fragment = pharmacyPage
            }
            replaceFragment(fragment)
            true
        }

        bottomNavigationView.selectedItemId = R.id.nav_myPills
    }

    private fun replaceFragment(pillTrackerFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pill_tracker_frame_layout, pillTrackerFragment)
        fragmentTransaction.commit()
    }
}