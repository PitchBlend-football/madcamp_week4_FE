package com.example.pitchblend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.pitchblend.databinding.ActivityDetailMatchBinding
import com.example.pitchblend.databinding.ActivityLoginBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var signInBtn: Button
    private lateinit var bottomSignInBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var bottomSignUpBtn: Button
    private lateinit var parentLayout: ConstraintLayout

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
            fadeInAnimator.start() // start()가 두 번 있으니까 얘가 깜빡깜빡하는 것처럼 보임.
            // 얘를 주석처리하고 아래쪽을 주석처리 안하면, 약간 반응속도가 느린 것 같아서 ㅇㅇ 그냥 얘로 ㄱㄱ
        }

        // parentLayout의 클릭 이벤트 처리
        parentLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                //fadeOutAnimator.start()
            }
        }

        // bottom sheet의 상태 변화 콜백
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    //BottomSheetBehavior.STATE_EXPANDED -> fadeInAnimator.start()
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> fadeOutAnimator.start()
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        bottomSignInBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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