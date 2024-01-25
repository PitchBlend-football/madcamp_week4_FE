package com.example.pitchblend

import com.google.gson.annotations.SerializedName

data class PostVoteRequest(
    @SerializedName("player")
    val player: Int,
    @SerializedName("is_like")
    val isLike: Boolean,
    @SerializedName("comment")
    val comment: String
)

data class VoteResultRequest(
    @SerializedName("vote_percentage")
    val votePercentage: IsLike,
    val comments: ArrayList<MomCommentRequest>
)

data class IsLike(
    val likes: Float,
    val dislikes: Float
)
data class MomCommentRequest(
    val id: Int,
    @SerializedName("is_like")
    val commentLike: Boolean,
    val comment: String,
    @SerializedName("user")
    val commentNickname: String,
    @SerializedName("created_at")
    val createdAt: String
)
