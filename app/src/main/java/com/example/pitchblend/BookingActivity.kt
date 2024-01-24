package com.example.pitchblend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import com.example.pitchblend.databinding.ActivityBookingBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private var teamId = 0
    private val api = RetrofitInterface.create()
    private var matchHomeTeam = ArrayList<String>()
    private var matchAwayTeam = ArrayList<String>()
    private var matchHomeTeamIni = ArrayList<String>()
    private var matchAwayTeamIni = ArrayList<String>()
    private var matchStadium = ArrayList<String>()
    private var matchDateLong = ArrayList<Long>()
    private var matchDateString = ArrayList<String>()
    private var matchHomeLogo = ArrayList<String>()

    private lateinit var ticket1Home: TextView
    private lateinit var ticket2Home: TextView
    private lateinit var ticket3Home: TextView

    private lateinit var ticket1Away: TextView
    private lateinit var ticket2Away: TextView
    private lateinit var ticket3Away: TextView

    private lateinit var ticket1Stadium: TextView
    private lateinit var ticket2Stadium: TextView
    private lateinit var ticket3Stadium: TextView

    private lateinit var ticket1: ImageButton
    private lateinit var ticket2: ImageButton
    private lateinit var ticket3: ImageButton

    private lateinit var ticket1Price: TextView
    private lateinit var ticket2Price: TextView
    private lateinit var ticket3Price: TextView

    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiation()
    }


    private fun initiation() {
        val sharedPref = getSharedPreferences("teamId", Context.MODE_PRIVATE)
        teamId = sharedPref.getInt("teamId", 0)
        Log.e("teamId", "teamId: $teamId")

        backBtn = binding.bookdingBackBtn
        backBtn.setOnClickListener {
            finish()
        }

        ticket1Home = binding.ticket1Home
        ticket2Home = binding.ticket2Home
        ticket3Home = binding.ticket3Home

        ticket1Away = binding.ticket1Away
        ticket2Away = binding.ticket2Away
        ticket3Away = binding.ticket3Away

        ticket1Stadium = binding.ticket1Stadium
        ticket2Stadium = binding.ticket2Stadium
        ticket3Stadium = binding.ticket3Stadium

        ticket1 = binding.ticket1
        ticket2 = binding.ticket2
        ticket3 = binding.ticket3

        ticket1Price = binding.ticket1Price
        ticket2Price = binding.ticket2Price
        ticket3Price = binding.ticket3Price

        GlobalScope.launch(Dispatchers.Main) {
            getTeamSchedule(teamId)
            // 결과를 사용하는 로직
            for (home in matchHomeTeam) {
                matchHomeTeamIni.add(CommonHelper.getTeamIni(this, home))
            }
            for (away in matchAwayTeam) {
                matchAwayTeamIni.add(CommonHelper.getTeamIni(this, away))
            }
            for (date in matchDateLong) {
                matchDateString.add(convertTimestampToString(date))
            }

            ticket1Home.text = matchHomeTeamIni[0]
            ticket2Home.text = matchHomeTeamIni[1]
            ticket3Home.text = matchHomeTeamIni[2]

            ticket1Away.text = matchAwayTeamIni[0]
            ticket2Away.text = matchAwayTeamIni[1]
            ticket3Away.text = matchAwayTeamIni[2]

            ticket1Stadium.text = matchStadium[0]
            ticket2Stadium.text = matchStadium[1]
            ticket3Stadium.text = matchStadium[2]

            ticket1Price.text = "€ 250"
            ticket2Price.text = "€ 280"
            ticket3Price.text = "€ 240"

            val intent = Intent(this@BookingActivity, MobileTicketActivity::class.java)

            ticket1.setOnClickListener {
                intent.putExtra("teamLogo", matchHomeLogo[0])
                intent.putExtra("stadium", matchStadium[0])
                intent.putExtra("matchDate", matchDateString[0])
                startActivity(intent)
            }

            ticket2.setOnClickListener {
                intent.putExtra("teamLogo", matchHomeLogo[1])
                intent.putExtra("stadium", matchStadium[1])
                intent.putExtra("matchDate", matchDateString[1])
                startActivity(intent)
            }

            ticket3.setOnClickListener {
                intent.putExtra("teamLogo", matchHomeLogo[2])
                intent.putExtra("stadium", matchStadium[2])
                intent.putExtra("matchDate", matchDateString[2])
                startActivity(intent)
            }


        }
    }

    private suspend fun getTeamSchedule(teamId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getTeamSchedule(teamId)
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                val teamSchedule = result.response
                for (schedule in teamSchedule) {
                    matchHomeTeam.add(schedule.teams.home.name)
                    matchAwayTeam.add(schedule.teams.away.name)
                    matchStadium.add(schedule.fixture.venue.get("name").asString)
                    matchDateLong.add(schedule.fixture.timestamp)
                    matchHomeLogo.add(schedule.teams.home.logo)
                }
            } else {
                // API 응답이 null인 경우의 처리
                Log.e(ContentValues.TAG, "API 응답이 null입니다.")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e(ContentValues.TAG, "Error: ${e.message}")
        }
    }

    fun  convertTimestampToString(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp) // 초 단위
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)
        return localDateTime.format(formatter)
    }
}