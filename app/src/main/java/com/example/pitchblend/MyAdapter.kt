package com.example.pitchblend

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView

class MyAdapter(private val mDataset: List<NewsData>, private val context: Context) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var TextView_title: TextView = v.findViewById(R.id.TextView_title)
        var TextView_content: TextView = v.findViewById(R.id.TextView_content)
        var ImageView_news: SimpleDraweeView = v.findViewById(R.id.ImageView_news)
    }

    init {
        Fresco.initialize(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_news, parent, false) as androidx.constraintlayout.widget.ConstraintLayout
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = mDataset[position]

        holder.TextView_title.text = news.title
        holder.TextView_content.text = news.content

        val uri = Uri.parse(news.urlToImage)
        holder.ImageView_news.setImageURI(uri)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }
}