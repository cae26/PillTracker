package com.example.pilltracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment : Fragment() {

    private lateinit var username: String

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the views in the layout
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val doctorNameTextView = view.findViewById<TextView>(R.id.doctorNameTextView)
        val doctorNumberTextView = view.findViewById<TextView>(R.id.doctorNumberTextView)
        val doctorEmailTextView = view.findViewById<TextView>(R.id.doctorEmailTextView)

        // Make an HTTP request to the PHP file and retrieve the response
        val url = "https://group8.dhruvaldhameliya.com/retrive_profile_info.php"

        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("userName", username)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)
                if (jsonResponse.length() > 0) {
                    val profileData = jsonResponse.getJSONObject(0)
                    nameTextView.text = profileData.getString("firstName") +" " + profileData.getString("lastName")
                    doctorNameTextView.text = profileData.getString("doctorName")
                    if (profileData.has("doctorPhone")) {
                        doctorNumberTextView.text = profileData.getString("doctorPhone")
                    }
                    if (profileData.has("doctorEmail")) {
                        doctorEmailTextView.text = profileData.getString("doctorEmail")
                    }
                }
            }


            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure as needed
            }
        })

        // Add a click listener to the edit profile button
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            val editProfileFragment = EditProfileFragment.newInstance(username)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.pill_tracker_frame_layout, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }
    }


}



