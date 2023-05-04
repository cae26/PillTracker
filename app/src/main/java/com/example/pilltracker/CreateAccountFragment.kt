package com.example.pilltracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class CreateAccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_account_fragment, container, false)
        val createAccountButton = view?.findViewById<Button>(R.id.button_create_account)
        val firstNameEditText = view?.findViewById<EditText>(R.id.edittext_first_name)
        val lastNameEditText = view?.findViewById<EditText>(R.id.edittext_last_name)
        val userNameEditText = view?.findViewById<EditText>(R.id.edittext_username)
        val passwordEditText = view?.findViewById<EditText>(R.id.edittext_password)
        val dateOfBirthEditText = view?.findViewById<EditText>(R.id.edittext_date_of_birth)
        val emailEditText = view?.findViewById<EditText>(R.id.edittext_email)
        val doctorNameEditText = view?.findViewById<EditText>(R.id.edittext_doctor_name)
        val doctorEmailEditText = view?.findViewById<EditText>(R.id.edittext_doctor_email)
        val doctorPhoneEditText = view?.findViewById<EditText>(R.id.edittext_doctor_phone)
//        val homePharmacyEditText = view?.findViewById<EditText>(R.id.edittext_home_pharmacy)
//        val pharmacyAddressEditText = view?.findViewById<EditText>(R.id.edittext_pharmacy_address)
//        val pharmacyPhoneEditText = view?.findViewById<EditText>(R.id.edittext_pharmacy_phone)
        // Inflate the layout for this fragment
        if (createAccountButton != null) {
            createAccountButton.setOnClickListener {
                val firstName = firstNameEditText?.text.toString()
                val lastName = lastNameEditText?.text.toString()
                val userName = userNameEditText?.text.toString()
                val password = passwordEditText?.text.toString()
                val dateOfBirth = dateOfBirthEditText?.text.toString()
                val email = emailEditText?.text.toString()
                val doctorName = doctorNameEditText?.text.toString()
                val doctorEmail = doctorEmailEditText?.text.toString()
                val doctorPhone = doctorPhoneEditText?.text.toString()
//                val homePharmacy = homePharmacyEditText?.text.toString()
//                val pharmacyAddress = pharmacyAddressEditText?.text.toString()
//                val pharmacyPhone = pharmacyPhoneEditText?.text.toString()

                // TODO: Add validation for form fields

                val jsonObject = JSONObject()
                jsonObject.put("firstName", firstName)
                jsonObject.put("lastName", lastName)
                jsonObject.put("userName", userName)
                jsonObject.put("password", password)
                jsonObject.put("dateOfBirth", dateOfBirth)
                jsonObject.put("email", email)
                jsonObject.put("doctorName", doctorName)
                jsonObject.put("doctorEmail", doctorEmail)
                jsonObject.put("doctorPhone", doctorPhone)
//                jsonObject.put("homePharmacy", homePharmacy)
//                jsonObject.put("pharmacyAddr", pharmacyAddress)
//                jsonObject.put("pharmacyPhone", pharmacyPhone)

                val requestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("https://group8.dhruvaldhameliya.com/create_account.php")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("CreateAccountFragment", "Failed to create account: " + e.message)
                        // TODO: Show error message to user
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d("CreateAccountFragment", "Response received: " + response.body?.string())
                        // TODO: Show confirmation message to user or navigate to next screen
                        requireActivity().supportFragmentManager.popBackStack()

                    }
                })
            }
        }


        return view
    }

}