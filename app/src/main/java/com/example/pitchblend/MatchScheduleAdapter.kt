package com.example.pitchblend

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MatchScheduleAdapter (private var matchScheduleList: ArrayList<Schedule>) :
    RecyclerView.Adapter<MatchScheduleAdapter.matchScheduleViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()
    lateinit var formattedDate: String
    lateinit var formattedTime: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): matchScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_schedule_recyclerview, parent, false)
        return matchScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: matchScheduleViewHolder, position: Int) {
        holder.bind(matchScheduleList[position])

        holder.matchScheduleCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailMatchActivity::class.java)
            intent.putExtra("matchId", matchScheduleList[position].fixture.id)
            intent.putExtra("matchTeams", "${matchScheduleList[position].teams.home.name} VS ${matchScheduleList[position].teams.away.name}")
            intent.putExtra("matchHomeLogo", matchScheduleList[position].teams.home.logo)
            intent.putExtra("matchAwayLogo", matchScheduleList[position].teams.away.logo)
            intent.putExtra("matchDate", formattedDate)
            intent.putExtra("matchTime", formattedTime)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return matchScheduleList.size
    }

    // 데이터를 업데이트하는 메서드 추가
    fun updateData(newMatchScheduleList: List<Schedule>) {
        matchScheduleList.clear()
        matchScheduleList.addAll(newMatchScheduleList)
        notifyDataSetChanged()
    }

    fun  convertTimestampToDateString(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp) // 초 단위
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        return localDateTime.format(formatter)
    }

    fun  convertTimestampToTimeString(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp) // 초 단위
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
        return localDateTime.format(formatter)
    }

    inner class matchScheduleViewHolder(matchScheduleView: View) : RecyclerView.ViewHolder(matchScheduleView) {
        val matchScheduleCard = matchScheduleView.findViewById<CardView>(R.id.match_schedule_card_view)
        val homeLogo = matchScheduleView.findViewById<ImageView>(R.id.home_logo)
        val awayLogo = matchScheduleView.findViewById<ImageView>(R.id.away_logo)
        val homeName = matchScheduleView.findViewById<TextView>(R.id.home_name)
        val awayName = matchScheduleView.findViewById<TextView>(R.id.away_name)
        val matchDate = matchScheduleView.findViewById<TextView>(R.id.match_date)
        val matchTime = matchScheduleView.findViewById<TextView>(R.id.match_time)

        fun bind(item: Schedule) {

            val homeLogoUrl = item.teams.home.logo
            val awayLogoUrl = item.teams.away.logo
            Picasso.get().load("$homeLogoUrl").into(homeLogo)
            Picasso.get().load("$awayLogoUrl").into(awayLogo)

            when (item.teams.home.name) {
                "Nottingham Forest" -> {
                    homeName.text = "Nottm Forest"
                }
                "Manchester United" -> {
                    homeName.text = "Man United"
                }
                "Manchester City" -> {
                    homeName.text = "Man City"
                }
                "Sheffield Utd" -> {
                    homeName.text = "Sheff Utd"
                }
                else -> {
                    homeName.text = item.teams.home.name
                }
            }

            when (item.teams.away.name) {
                "Nottingham Forest" -> {
                    awayName.text = "Nottm Forest"
                }
                "Manchester United" -> {
                    awayName.text = "Man United"
                }
                "Manchester City" -> {
                    awayName.text = "Man City"
                }
                "Sheffield Utd" -> {
                    awayName.text = "Sheff Utd"
                }
                else -> {
                    awayName.text = item.teams.away.name
                }
            }

            Log.e("timestamp", "timestamp: ${item.fixture.timestamp}")

            // 이런 식으로 timestamp를 쓰면 된다.
            val timestamp = item.fixture.timestamp
            formattedDate = convertTimestampToDateString(timestamp)
            formattedTime = convertTimestampToTimeString(timestamp)

            matchDate.text = formattedDate
            matchTime.text = formattedTime

        }

    }
}