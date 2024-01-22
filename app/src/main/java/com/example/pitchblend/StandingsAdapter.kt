package com.example.pitchblend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class StandingsAdapter (private var standingsList: ArrayList<Team>) :
    RecyclerView.Adapter<StandingsAdapter.standingsViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): standingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.standings_recyclerview, parent, false)
        return standingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: standingsViewHolder, position: Int) {
        holder.bind(standingsList[position])

    }

    override fun getItemCount(): Int {
        return standingsList.size
    }

    // 데이터를 업데이트하는 메서드 추가
    fun updateData(newStandingsList: List<Team>) {
        standingsList.clear()
        standingsList.addAll(newStandingsList)
        notifyDataSetChanged()
    }

    inner class standingsViewHolder(standingsView: View) : RecyclerView.ViewHolder(standingsView) {
        val standingsLogo = standingsView.findViewById<ImageView>(R.id.standings_logo)
        val standingsTeamName = standingsView.findViewById<TextView>(R.id.standings_team_name)
        val standingsPL = standingsView.findViewById<TextView>(R.id.standings_pl)
        val standingsW = standingsView.findViewById<TextView>(R.id.standings_w)
        val standingsD = standingsView.findViewById<TextView>(R.id.standings_d)
        val standingsL = standingsView.findViewById<TextView>(R.id.standings_l)
        val standingsGD = standingsView.findViewById<TextView>(R.id.standings_gd)
        val standingsPTS = standingsView.findViewById<TextView>(R.id.standings_pts)


        fun bind(item: Team) {

            // 이미지 URL이 있는지 확인하고 Picasso를 사용하여 ImageView에 로드합니다.
            val logoUrl = item.team.logo
            //Log.e("imageURL", "$imageUrl")
            Picasso.get().load("$logoUrl").into(standingsLogo)

            when (item.team.name) {
                "Nottingham Forest" -> {
                    standingsTeamName.text = "Nottm Forest"
                }
                "Manchester United" -> {
                    standingsTeamName.text = "Man United"
                }
                "Manchester City" -> {
                    standingsTeamName.text = "Man City"
                }
                "Sheffield Utd" -> {
                    standingsTeamName.text = "Sheff Utd"
                }
                else -> {
                    standingsTeamName.text = item.team.name
                }
            }

            standingsPL.text = item.all.played.toString()
            standingsW.text = item.all.win.toString()
            standingsD.text = item.all.draw.toString()
            standingsL.text = item.all.lose.toString()
            standingsGD.text = item.goalsDiff.toString()
            standingsPTS.text = item.points.toString()

        }

    }
}