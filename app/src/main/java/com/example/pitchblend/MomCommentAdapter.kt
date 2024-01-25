package com.example.pitchblend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class MomCommentAdapter (private var momCommentList: ArrayList<MomCommentRequest>) :
    RecyclerView.Adapter<MomCommentAdapter.momCommentViewHolder>() {

    // 서버에서 불러오기
    val api = RetrofitInterface.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): momCommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mom_comment_recyclerview, parent, false)
        return momCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: momCommentViewHolder, position: Int) {
        holder.bind(momCommentList[position])

    }

    override fun getItemCount(): Int {
        return momCommentList.size
    }

    // 데이터를 업데이트하는 메서드 추가
    fun updateData(newMomCommentList: List<MomCommentRequest>) {
        momCommentList.clear()
        momCommentList.addAll(newMomCommentList)
        notifyDataSetChanged()
    }

    inner class momCommentViewHolder(momCommentView: View) : RecyclerView.ViewHolder(momCommentView) {
        val momIsLike: ImageView = momCommentView.findViewById(R.id.comment_is_like)
        val momNickname: TextView = momCommentView.findViewById(R.id.comment_nickname)
        val momComment: TextView = momCommentView.findViewById(R.id.comment_comment)


        fun bind(item: MomCommentRequest) {

            if (item.commentLike) {
                momIsLike.setImageResource(R.drawable.like_soccerball)
            } else {
                momIsLike.setImageResource(R.drawable.dislike_soccerball)
            }
            momNickname.text = item.commentNickname
            momComment.text = item.comment

        }

    }
}