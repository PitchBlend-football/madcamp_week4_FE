package com.example.pitchblend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class MyProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref= requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        accessToken = sharedPref.getString("accessToken", "")!!
        Log.e("accessToken", "accessToken: $accessToken")
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userProfile = UserProfileManager.getUserProfile()
        userProfile?.let {
            view.findViewById<TextView>(R.id.nickname).text = it.nickname
            view.findViewById<TextView>(R.id.email_txt).text = it.email

            // team_name을 대문자로 변환하여 TextView에 설정
            view.findViewById<TextView>(R.id.teamName).text = it.teamName.toUpperCase()

            // logo_url을 ImageView에 설정 (Glide 사용)
            val teamLogoImageView = view.findViewById<ImageView>(R.id.teamLogo)
            Glide.with(this)
                .load(it.logoUrl)
                .into(teamLogoImageView)

            //프사
            val profileImageView = view.findViewById<ImageView>(R.id.profile_img)
            Glide.with(this)
                .load(it.logoUrl)
                .into(profileImageView)
        }

        // Logout button click listener
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Call the function to perform logout
            logoutUser()
        }

        // CardView click listener
        val communityCardView = view.findViewById<CardView>(R.id.communityCardView)
        communityCardView.setOnClickListener {
            // Start Community Activity when CardView is clicked
            val intent = Intent(requireContext(), Community::class.java)
            startActivity(intent)
        }

    }

    // Function to perform logout
    private fun logoutUser() {
        val url = "http://ec2-43-202-210-226.ap-northeast-2.compute.amazonaws.com/accounts/logout/"

        // Headers for the request
        val headers = HashMap<String, String>()
        headers["Authorization"] = "Bearer $accessToken"

        // Create a JsonObjectRequest for the logout API
        val request = object : JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                // Handle successful logout
                handleLogoutSuccess(response)
            },
            Response.ErrorListener { error ->
                // Handle logout error
                handleLogoutError(error)
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }

// Add the request to the Volley queue
        Volley.newRequestQueue(requireContext()).add(request)
    }

    // Handle successful logout
    private fun handleLogoutSuccess(response: JSONObject) {
        // Log the success message
        Log.d("MyProfileFragment", "Logout success: ${response.getString("detail")}")

        // Start LoginActivity after successful logout
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        requireActivity().finish() // Finish the current activity
    }

    // Handle logout error
    private fun handleLogoutError(error: Exception) {
        // Log the error message
        error.printStackTrace()
        Log.e("MyProfileFragment", "Logout failed. Exception: ${error.message}")

        // You can add additional logic for handling the logout error here
    }



    companion object {
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}