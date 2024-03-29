// MyThirdAdapter.kt

package com.example.pitchblend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView

class MyThirdAdapter(private val mDataset: List<NewsData>, private val context: Context) :
    RecyclerView.Adapter<MyThirdAdapter.MyThirdViewHolder>() {

    class MyThirdViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var TextView_title: TextView = v.findViewById(R.id.third_TextView_title)
        var ImageView_news: SimpleDraweeView = v.findViewById(R.id.third_ImageView_news)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyThirdViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_third_news, parent, false) as ConstraintLayout
        return MyThirdViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyThirdViewHolder, position: Int) {
        val news = mDataset[position]

        holder.TextView_title.text = news.title

        // urlToImage가 null인 경우 기본 이미지 URL로 대체
        val imageUrl = news.urlToImage ?: "https://media.api-sports.io/football/leagues/39.png"
        val uri = Uri.parse(imageUrl)
        holder.ImageView_news.setImageURI(uri)

        holder.TextView_title.setOnClickListener {
            // 해당 뉴스의 URL을 가져와서 웹 브라우저로 열기
            val newsUrl = news.url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }
}
