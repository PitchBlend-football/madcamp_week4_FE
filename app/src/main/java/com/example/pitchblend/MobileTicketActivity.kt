package com.example.pitchblend

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MobileTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_ticket)

        val ticketLogo = findViewById<ImageView>(R.id.big_ticket_logo)
        val ticketStadium = findViewById<TextView>(R.id.big_ticket_stadium)
        val ticketDate = findViewById<TextView>(R.id.big_ticket_date)
        val backBtn = findViewById<ImageButton>(R.id.mobile_ticket_back_btn)

        Picasso.get().load(intent.getStringExtra("teamLogo")).into(ticketLogo)
        ticketStadium.text = intent.getStringExtra("stadium")
        ticketDate.text = intent.getStringExtra("matchDate")

        backBtn.setOnClickListener {
            finish()
        }
    }
}