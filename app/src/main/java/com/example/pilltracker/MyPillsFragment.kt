package com.example.pilltracker
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import java.io.IOException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MyPillsFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var myPillsAdapter: MyPillsAdapter

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): MyPillsFragment {
            val fragment = MyPillsFragment()
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
        return inflater.inflate(R.layout.fragment_my_pills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        myPillsAdapter = MyPillsAdapter(listOf()) // Initialize with an empty list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = myPillsAdapter

        // Call the fetchAndParseData() function to fetch and parse the data
        fetchAndParseData("user1")
    }
    private fun fetchAndParseData(userName: String) {
        val url = "https://group8.dhruvaldhameliya.com/my_pills.php"

        val client = OkHttpClient()

        // Create the JSON request body
        val requestBody = JSONObject().apply {
            put("userName", userName)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONArray(responseBody)

                val medicines = ArrayList<MyPills>()

                for (i in 0 until jsonResponse.length()) {
                    val jsonObject = jsonResponse.getJSONObject(i)
                    val mypill = MyPills(
                        id = jsonObject.getInt("id"),
                        userName = jsonObject.getString("userName"),
                        nameOfMedicine = jsonObject.getString("nameOfMedicine"),
                        dose = jsonObject.getString("dose"),
                        timeToTakeMed = jsonObject.getString("timeToTakeMed"),
                        additionalNotes = jsonObject.getString("additionalNotes"),
                        remainingMedicine = jsonObject.getString("remainingMedicine")
                    )
                    medicines.add(mypill)
                }

                activity?.runOnUiThread {
                    recyclerView.adapter = MyPillsAdapter(medicines)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }



}