package com.example.pitchblend

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class LiveScoreAdapter (private var liveScoreList: ArrayList<Schedule>) :
    RecyclerView.Adapter<LiveScoreAdapter.liveScoreViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): liveScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.livescore_recyclerview, parent, false)
        return liveScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: liveScoreViewHolder, position: Int) {
        holder.bind(liveScoreList[position])

        holder.run {
            cardView.apply {
                scaleX = 0.9f
                scaleY = 0.9f
            }
        }

        holder.cardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AfterMatchActivity::class.java)
            intent.putExtra("matchId", liveScoreList[position].fixture.id)
            intent.putExtra("matchTeams", "${liveScoreList[position].teams.home.name} VS ${liveScoreList[position].teams.away.name}")
            intent.putExtra("matchHomeLogo", liveScoreList[position].teams.home.logo)
            intent.putExtra("matchAwayLogo", liveScoreList[position].teams.away.logo)
            intent.putExtra("matchHomeGoals", liveScoreList[position].goals.home.toString())
            intent.putExtra("matchAwayGoals", liveScoreList[position].goals.away.toString())
            intent.putExtra("matchHomeId", liveScoreList[position].teams.home.id)
            intent.putExtra("matchAwayId", liveScoreList[position].teams.away.id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return liveScoreList.size
    }

    fun updateData(newLiveScoreList: List<Schedule>) {
        liveScoreList.clear()
        val sortedList = newLiveScoreList.sortedByDescending { Instant.ofEpochSecond(it.fixture.timestamp) }
        liveScoreList.addAll(sortedList)
        notifyDataSetChanged()
    }

    fun  convertTimestampToDateString(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(timestamp) // 초 단위
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)
        return localDateTime.format(formatter)
    }

    inner class liveScoreViewHolder(liveScoreView: View) : RecyclerView.ViewHolder(liveScoreView) {
        val cardView = liveScoreView.findViewById<CardView>(R.id.livescore_cardview)
        val homeLogo = liveScoreView.findViewById<ImageView>(R.id.livescore_home_logo)
        val awayLogo = liveScoreView.findViewById<ImageView>(R.id.livescore_away_logo)
        val homeName = liveScoreView.findViewById<TextView>(R.id.livescore_home_name)
        val awayName = liveScoreView.findViewById<TextView>(R.id.livescore_away_name)
        val homeScore = liveScoreView.findViewById<TextView>(R.id.livescore_home_score)
        val awayScore = liveScoreView.findViewById<TextView>(R.id.livescore_away_score)
        val matchDate = liveScoreView.findViewById<TextView>(R.id.livescore_match_date)

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
            val formattedDate = convertTimestampToDateString(timestamp)

            matchDate.text = formattedDate

            homeScore.text = item.goals.home.toString()
            awayScore.text = item.goals.away.toString()

        }
    }
}