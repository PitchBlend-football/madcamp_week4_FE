package com.example.pitchblend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class MyProfileFragment : Fragment() {

    private lateinit var accessToken: String
    private val api = RetrofitInterface.create()

    private lateinit var teamName: String
    private lateinit var stadium: String
    private lateinit var stadiumImg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        GlobalScope.launch(Dispatchers.Main) {
            getStadiumInfo(accessToken)
            // 결과를 사용하는 로직
            Log.e("MyProfileFragment", "teamName: $teamName")
            Log.e("MyProfileFragment", "stadium: $stadium")
            Log.e("MyProfileFragment", "stadiumImg: $stadiumImg")
            val stadiumIv = view.findViewById<ImageView>(R.id.stadium_img)
            Picasso.get().load("http://ec2-43-202-210-226.ap-northeast-2.compute.amazonaws.com/media/$stadiumImg").into(stadiumIv)
            view.findViewById<TextView>(R.id.stadium_address).text = stadium
        }

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

        // Community ㄱㄱ
        val communityCardView = view.findViewById<ImageButton>(R.id.communityCardView)
        communityCardView.setOnClickListener {
            // Start Community Activity when CardView is clicked
            val intent = Intent(requireContext(), Community::class.java)
            startActivity(intent)
        }

        // ticket ㄱㄱ
        val ticketCardView = view.findViewById<CardView>(R.id.ticket_cardview)
        ticketCardView.setOnClickListener {
            val intent = Intent(requireContext(), BookingActivity::class.java)
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

    private suspend fun getStadiumInfo(accessToken: String, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getStadiumInfo("Bearer $accessToken")
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                teamName = result.get("team_name").asString
                stadium = result.get("stadium").asString
                stadiumImg = result.get("stadium_image").asString
            } else {
                // API 응답이 null인 경우의 처리
                Log.e(ContentValues.TAG, "API 응답이 null입니다.")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e(ContentValues.TAG, "Error: ${e.message}")
        }
    }

    companion object {}
}