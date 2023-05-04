package com.example.pilltracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        val nameTextView = view.findViewById<EditText>(R.id.nameEditText)
        val doctorNameTextView = view.findViewById<EditText>(R.id.doctorNameEditText)
        val doctorNumberTextView = view.findViewById<EditText>(R.id.doctorNumberEditText)
        val doctorEmailTextView = view.findViewById<EditText>(R.id.doctorEmailEditText)

        val url = "https://group8.dhruvaldhameliya.com/retrive_profile_info.php"
        val requestBody = JSONObject().apply {
            put("userName", username)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)
                if (jsonResponse.length() > 0) {
                    val profileData = jsonResponse.getJSONObject(0)
                    val firstName = profileData.getString("firstName")
                    val lastName = profileData.getString("lastName")
                    activity?.runOnUiThread {
                        nameTextView.setText("$firstName $lastName")
                        doctorNameTextView.setText(profileData.getString("doctorName"))
                        if (profileData.has("doctorPhone")) {
                            doctorNumberTextView.setText(profileData.getString("doctorPhone"))
                        }
                        if (profileData.has("doctorEmail")) {
                            doctorEmailTextView.setText(profileData.getString("doctorEmail"))
                        }
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
            // Get the text from the EditText views
            val name = nameTextView.text.toString()
            val doctorName = doctorNameTextView.text.toString()
            val doctorNumber = doctorNumberTextView.text.toString()
            val doctorEmail = doctorEmailTextView.text.toString()

            // Create the JSON object
            val requestBody = JSONObject().apply {
                put("firstName", name.split(" ")[0])
                put("lastName", name.split(" ")[1])
                put("userName", username)
                put("doctorName", doctorName)
                put("doctorEmail", doctorEmail)
                put("doctorPhone", doctorNumber)
            }

            // Make an HTTP request to the PHP file to update the profile info
            val url = "https://group8.dhruvaldhameliya.com/edit_profile.php"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {


                    // Handle the response as needed
                }

                override fun onFailure(call: Call, e: IOException) {
                    // Handle the failure as needed
                }
            })
            Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_LONG).show()
        }
    }


}



