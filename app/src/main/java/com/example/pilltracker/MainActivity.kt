package com.example.pilltracker
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), LoginFragment.LoginSuccessListener {
    private var loggedInUsername: String? = null
    private var loggedInPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PillTrackerAPI().run()

        val logInPage = LoginFragment.newInstance(this)
        replaceFragment(logInPage)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Remove the initialization of myPillsPage from here
       // val logPage: Fragment = LogFragment()
        val pharmacyPage: Fragment = PharmacyFragment()

        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.nav_myPills -> {
                    if (loggedInUsername != null) {
                        fragment = MyPillsFragment.newInstance(loggedInUsername!!)
                    } else {
                        return@setOnItemSelectedListener false
                    }
                }
                R.id.nav_log -> {
                    if (loggedInUsername != null) {
                        fragment = LogFragment.newInstance(loggedInUsername!!)
                    } else {
                        return@setOnItemSelectedListener false
                    }
                }
                R.id.nav_pharmacy -> {
                    fragment = PharmacyFragment()
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
            replaceFragment(fragment)
            true
        }


        disableNavigationButtons()
    }

    override fun onLoginSuccess(username: String, password: String) {
        loggedInUsername = username
        loggedInPassword = password

        enableNavigationButtons()
        val logPage= LogFragment.newInstance(loggedInUsername!!)
        val myPillsPage = MyPillsFragment.newInstance(loggedInUsername!!)
        replaceFragment(myPillsPage)
    }

    private fun replaceFragment(pillTrackerFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pill_tracker_frame_layout, pillTrackerFragment)
        fragmentTransaction.commit()
    }

    private fun disableNavigationButtons() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.nav_myPills).isEnabled = false
        bottomNavigationView.menu.findItem(R.id.nav_log).isEnabled = false
        bottomNavigationView.menu.findItem(R.id.nav_pharmacy).isEnabled = false
    }

    private fun enableNavigationButtons() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.menu.findItem(R.id.nav_myPills).isEnabled = true
        bottomNavigationView.menu.findItem(R.id.nav_log).isEnabled = true
        bottomNavigationView.menu.findItem(R.id.nav_pharmacy).isEnabled = true
        bottomNavigationView.selectedItemId = R.id.nav_myPills
    }
}
