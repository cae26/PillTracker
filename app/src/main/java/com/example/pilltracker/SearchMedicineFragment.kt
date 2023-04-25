package com.example.pilltracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SearchMedicineFragment : Fragment() {

    companion object {
        private const val TAG = "SearchMedicineFragment"
        const val ARG_USERNAME = "username"
        fun newInstance(username: String): SearchMedicineFragment {
            val fragment = SearchMedicineFragment()
            val args = Bundle()
            args.putString(SearchMedicineFragment.ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var searchEditText: EditText
    private lateinit var detailsTextView: TextView // add this

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passedUsername = arguments?.getString(SearchMedicineFragment.ARG_USERNAME)

        println("Hello, World! $passedUsername")
        val searchAutoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.search_auto_complete_text)
        val searchButton: Button = view.findViewById(R.id.search_button)
        val addButton: Button = view.findViewById(R.id.add_button)

        fetchAndParseData("") { medicineNames ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, medicineNames)
            searchAutoCompleteTextView.setAdapter(adapter)
        }

        searchButton.setOnClickListener {
            val searchQuery = searchAutoCompleteTextView.text.toString().trim()
            if (searchQuery.isEmpty()) {
                return@setOnClickListener
            }
            fetchAndGetDetails(searchQuery)
        }

        addButton.setOnClickListener {
            val searchQuery = searchAutoCompleteTextView.text.toString().trim()
            if (searchQuery.isEmpty()) {
                return@setOnClickListener
            }
//            val action = SearchMedicineFragmentDirections.actionSearchMedicineFragmentToAddMedicineFragment(passedUsername, searchQuery)
//            findNavController().navigate(action)

            val fragment =
                passedUsername?.let { it1 -> AddMedicineFragment.newInstance(it1, searchQuery) }
            if (fragment != null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.pill_tracker_frame_layout, fragment)
                    .addToBackStack(null)
                    .commit()
            }

        }

        searchAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fetchAndParseData(s.toString()) { medicineNames ->
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, medicineNames)
                    searchAutoCompleteTextView.setAdapter(adapter)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchAndParseData(searchQuery: String, callback: (List<String>) -> Unit) {
        val url = "https://group8.dhruvaldhameliya.com/auto_complete_medicineAPI.php"
        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("medicineName", searchQuery)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)

                    // Parse the response and extract medicine names
                    val medicineNames = mutableListOf<String>()
                    try {
                        val jsonArray = JSONArray(responseBody)
                        for (i in 0 until jsonArray.length()) {
                            val medicineName = jsonArray.getString(i)
                            medicineNames.add(medicineName)
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error parsing JSON: ${e.message}")
                    }

                    activity?.runOnUiThread {
                        callback(medicineNames)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error fetching data: ${e.message}")
                activity?.runOnUiThread {
                    callback(emptyList())
                }
            }
        })
    }
    private fun fetchAndGetDetails(searchQuery: String) {
        val url = "https://group8.dhruvaldhameliya.com/medicineAPI.php"
        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("medicineName", searchQuery)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    Log.d(TAG, responseBody)

                    // Parse the JSON response
                    try {
                        val jsonArray = JSONArray(responseBody)
                        if (jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)

                            // Extract the details from the JSON object
                            val drugName = jsonObject.getString("drugName")
                            val medicalCondition = jsonObject.getString("medicalCondition")
                            val genericName = jsonObject.getString("genericName")
                            val drugClass = jsonObject.getString("drugClass")
                            val rxOtc = jsonObject.getString("rxOtc")
                            val drugLink = jsonObject.getString("drugLink")
                            val medicalConditionLink = jsonObject.getString("medicalConditionLink")

                            // Update the UI with the details
                            // Update the UI with the details
                            activity?.runOnUiThread {
                                view?.findViewById<TextView>(R.id.drug_name)?.text = "Drug Name: $drugName"
                                view?.findViewById<TextView>(R.id.medical_condition)?.text = "Medical Condition: $medicalCondition"
                                view?.findViewById<TextView>(R.id.generic_name)?.text = "Generic Name: $genericName"
                                view?.findViewById<TextView>(R.id.drug_class_label)?.text = "Drug Class:"
                                view?.findViewById<TextView>(R.id.drug_class)?.text = drugClass
                                //view?.findViewById<TextView>(R.id.rx_otc_label)?.text = "RX/OTC:"
                                ///view?.findViewById<TextView>(R.id.rx_otc)?.text = rxOtc
                                view?.findViewById<TextView>(R.id.drug_link)?.apply {
                                    text = "Drug Link: $drugLink"
                                    autoLinkMask = Linkify.WEB_URLS
                                    movementMethod = LinkMovementMethod.getInstance()
                                }

                            }

                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "Error parsing JSON: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error fetching data: ${e.message}")
            }
        })
    }



}
