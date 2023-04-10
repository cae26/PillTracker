package com.example.pilltracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class LogFragment : Fragment(), LogAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter
    private lateinit var selectAllCheckBox: CheckBox
    private lateinit var inividualCheckBox: CheckBox
    private lateinit var deleteBtn: Button
    private lateinit var sendDoctor: Button


    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): LogFragment{
            val fragment = LogFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.logs)
        logAdapter = LogAdapter(listOf() , this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        selectAllCheckBox = view.findViewById(R.id.checkBox2)
        recyclerView.adapter = logAdapter
        val passedUsername = arguments?.getString(ARG_USERNAME)
        println("Hello, World! $passedUsername")

        fetchAndParseData(passedUsername ?: "defaultUsername")
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            logAdapter.selectAllItems(isChecked)
        }

    }

    private fun fetchAndParseData(userName: String) {
        val url = "https://group8.dhruvaldhameliya.com/logs_send.php"

        val client = OkHttpClient()

        // Create the JSON request body
        val requestBody = JSONObject().apply {
            put("userName", userName)
           // put("userName", )
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)

                val logs = ArrayList<Logs>()

                for (i in 0 until jsonResponse.length()) {
                    val jsonObject = jsonResponse.getJSONObject(i)
                    val log = Logs(
                        id = jsonObject.getInt("id"),
                        userName = jsonObject.getString("userName"),
                        medicineName = jsonObject.getString("medicineName"),
                        status = jsonObject.getString("status"),
                        dateTaken = jsonObject.getString("dateTaken"),
                        additionalNotes = jsonObject.getString("additionalNotes"),

                        )
                    logs.add(log)
                }

                activity?.runOnUiThread {
                    logAdapter.updatelogs(logs) // Update the existing adapter instead of creating a new one
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onItemClick(log: Logs) {
        val fragment = AddNotesFragment.newInstance(log)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.pill_tracker_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

}
