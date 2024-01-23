package com.example.pitchblend

// Community.kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.content.Intent

class Community : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community)

        // back_btn 클릭 시 MyProfileFragment로 이동
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            // MyProfileFragment로 이동하는 Intent 생성
            val intent = Intent(this, MyProfileFragment::class.java)
            // 기존의 Community 액티비티 종료
            finish()
        }
    }
}
