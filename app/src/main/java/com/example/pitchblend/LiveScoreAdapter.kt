package com.example.pitchblend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class LiveScoreAdapter (private var liveScoreList: ArrayList<String>, private val onClickItem: (String) -> Unit
): RecyclerView.Adapter<LiveScoreAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.livescore_recyclerview, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = liveScoreList[position]

        holder.run {
            cardView.apply {
                scaleX = 0.9f
                scaleY = 0.9f
            }

            cardView.setOnClickListener {
                onClickItem(item)
            }

            textView.text = item
        }
    }

    override fun getItemCount(): Int {
        return liveScoreList.size
    }

    inner class RecyclerViewHolder(liveScoreItemView: View) : RecyclerView.ViewHolder(liveScoreItemView) {
        val cardView = liveScoreItemView.findViewById<CardView>(R.id.livescore_cardview)
        val textView = liveScoreItemView.findViewById<TextView>(R.id.livescore_score)
    }
}