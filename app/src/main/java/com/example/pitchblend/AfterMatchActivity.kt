package com.example.pitchblend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.pitchblend.databinding.ActivityAfterMatchBinding

class AfterMatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterMatchBinding
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiation()
        clickBackBtn()
    }

    private fun initiation() {
        backBtn = binding.backBtn
    }

    private fun clickBackBtn() {
        backBtn.setOnClickListener {
            finish()
        }
    }
}