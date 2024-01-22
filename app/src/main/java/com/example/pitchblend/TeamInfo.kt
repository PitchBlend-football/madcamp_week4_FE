// TeamInfo.kt

package com.example.pitchblend

import com.google.gson.annotations.SerializedName

data class TeamInfo(
    @SerializedName("logo_url") val logoUrl: String,
    @SerializedName("team_name") val teamName: String,
    @SerializedName("news_text") val newsText: String
)
