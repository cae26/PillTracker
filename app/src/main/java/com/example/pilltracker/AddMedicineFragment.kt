package com.example.pilltracker

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class AddMedicineFragment : Fragment() {

    private lateinit var username: String
    private lateinit var medicineName: String
    private lateinit var doseEditText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var additionalNotesEditText: EditText
    private lateinit var pillCountEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            username = it.getString(ARG_USERNAME) ?: ""
            medicineName = it.getString(ARG_MEDICINE_NAME) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_medicine_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medicineNameTextView: TextView = view.findViewById(R.id.medicine_name)
        val doseEditText: EditText = view.findViewById(R.id.dose_edit_text)
        val additionalNotesEditText: EditText = view.findViewById(R.id.additional_notes_edit_text)
        val pillCountEditText: EditText = view.findViewById(R.id.pill_count_edit_text)
        val timePicker: TimePicker = view.findViewById(R.id.time_picker)
        val addMedicationButton: Button = view.findViewById(R.id.add_medication_button)

        medicineNameTextView.text = "Medicine Name: $medicineName"

        addMedicationButton.setOnClickListener {
            val dose = doseEditText.text.toString()
            val additionalNotes = additionalNotesEditText.text.toString()
            val pillCount = pillCountEditText.text.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute
            val timeToTakeMed = "$hour:$minute"

            val json = JSONObject()
            json.put("userName", username)
            json.put("nameOfMedicine", medicineName)
            json.put("dose", dose)
            json.put("timeToTakeMed", timeToTakeMed)
            json.put("additionalNotes", additionalNotes)
            json.put("remainingMedicine", pillCount)

            val url = "https://group8.dhruvaldhameliya.com/my_pills.php"
            val requestQueue = Volley.newRequestQueue(requireContext())

            val request = JsonObjectRequest(
                Request.Method.POST, url, json,
                { response ->
                    // Handle successful response
                    Log.d(TAG, "Response: $response")



                },
                { error ->
                    // Handle error
                    Log.e(TAG, "Error: ${error.message}")
                    Log.d(TAG, "Response:")
                    val navController = findNavController()
                    navController.navigate(R.id.nav_myPills)
                }
            )

            requestQueue.add(request)
        }
    }

    companion object {
        const val ARG_USERNAME = "username"
        const val ARG_MEDICINE_NAME = "medicine_name"

        fun newInstance(username: String, medicineName: String): AddMedicineFragment {
            val fragment = AddMedicineFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putString(ARG_MEDICINE_NAME, medicineName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
