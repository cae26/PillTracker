package com.example.pilltracker
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginFragment : Fragment() {
    private lateinit var loginSuccessListener: LoginSuccessListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var logsAdapter: LogAdapter

    // Initialize Shared Preferences
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    }

    interface LoginSuccessListener {
        fun onLoginSuccess(username: String, password: String)
    }

    companion object {
        fun newInstance(loginSuccessListener: LoginSuccessListener): LoginFragment {
            val fragment = LoginFragment()
            fragment.loginSuccessListener = loginSuccessListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText: EditText = view.findViewById(R.id.edittext_username)
        val passwordEditText: EditText = view.findViewById(R.id.edittext_password)
        val loginButton: Button = view.findViewById(R.id.button_login)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate inputs
            if (username.isEmpty() || password.isEmpty()) {
                // Show an error message to the user
                Toast.makeText(requireContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(username, password) { jsonResponse ->
                    if (jsonResponse.getBoolean("status")) {
                        // Save username and password in Shared Preferences after successful login
                        with(sharedPreferences.edit()) {
                            putString("username", username)
                            putString("password", password)
                            apply()
                        }
                        loginSuccessListener.onLoginSuccess(username, password)
                    } else {
                        Toast.makeText(requireContext(), "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val createAccountButton: Button = view.findViewById(R.id.button_create_account)
        createAccountButton.setOnClickListener {
            val createAccountFragment = CreateAccountFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.pill_tracker_frame_layout, createAccountFragment)
                .addToBackStack(null)
                .commit()
        }

        // Check if the user is already logged in every time the app is opened
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            loginUser(username, password) { jsonResponse ->
                if (jsonResponse.getBoolean("status")) {
                    loginSuccessListener.onLoginSuccess(username, password)
                }
            }
        }
    }

    private fun loginUser(username: String, password: String, callback: (JSONObject) -> Unit) {
        val url = "https://group8.dhruvaldhameliya.com/log_in.php"

        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("userName", username)
            put("password", password)
        }

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody)

                activity?.runOnUiThread {
                    callback(jsonResponse)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Log.e("Error", "Failed to fetch data: ${e.message}")
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
