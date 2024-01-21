// LoginActivity.kt
package com.example.pitchblend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.pitchblend.databinding.ActivityLoginBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var signInBtn: Button
    private lateinit var bottomSignInBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var bottomSignUpBtn: Button
    private lateinit var parentLayout: ConstraintLayout
    private lateinit var emailTxt: EditText
    private lateinit var passwordTxt: EditText

    private fun loginUser(email: String, password: String) {
        val url = "http://ec2-43-202-210-226.ap-northeast-2.compute.amazonaws.com/accounts/login/"

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        // HashMap을 Java의 Map으로 변환
        val paramMap: Map<String, String> = params

        val jsonObject = JSONObject(paramMap)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                try {
                    val accessToken = response.getString("access_token")
                    saveAccessToken(accessToken)
                    Log.d("LoginActivity", "Login success. Access Token: $accessToken")

                    // 로그인 성공 후 HomeFragment로 전환
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    val homeFragment = HomeFragment()

                    // (Optional) 전환 애니메이션을 설정하려면 아래 주석 해제
                    // fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)

                    fragmentTransaction.replace(R.id.bottom_sheet, homeFragment)
                    fragmentTransaction.addToBackStack(null)  // 백 스택에 추가하려면 주석 해제
                    fragmentTransaction.commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("LoginActivity", "Login failed. Exception: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // 에러 처리 코드
                error.printStackTrace()
            }
        )

        // Volley 큐에 요청을 추가
        Volley.newRequestQueue(this).add(request)
    }

    private fun saveAccessToken(accessToken: String) {
        // 예시 (SharedPreferences 사용):
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.apply()
    }

    private fun getAccessToken(): String? {
        // TODO: 저장된 엑세스 토큰을 불러오는 코드
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("accessToken", null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiation()
        clickSignIn()
        clickSignUp()
    }

    private fun initiation() {
        signInBtn = binding.signInBtn
        parentLayout = binding.parentConstraintLayout
        bottomSignInBtn = binding.bottomsheetSignInBtn
        signUpBtn = binding.signUpBtn
        bottomSignUpBtn = binding.bottomsheetSignUpBtn
        emailTxt = binding.emailTxt
        passwordTxt = binding.passwordTxt
    }

    private fun clickSignIn() {
        val bottomSheetView = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        val dimmer: View = findViewById(R.id.dimmer)

        val fadeInAnimator = ObjectAnimator.ofFloat(dimmer, "alpha", 0f, 0.8f)
        fadeInAnimator.duration = 300

        val fadeOutAnimator = ObjectAnimator.ofFloat(dimmer, "alpha", 0.8f, 0f)
        fadeOutAnimator.duration = 300

        fadeInAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                dimmer.visibility = View.VISIBLE
            }
        })

        signInBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            fadeInAnimator.start()
        }

        parentLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> fadeOutAnimator.start()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSignInBtn.setOnClickListener {
            val email = emailTxt.text.toString()
            val password = passwordTxt.text.toString()
            loginUser(email, password)
        }
    }

    private fun clickSignUp() {
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        bottomSignUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
}
