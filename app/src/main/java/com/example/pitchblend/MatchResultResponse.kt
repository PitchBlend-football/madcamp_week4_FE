package com.example.pitchblend

import com.google.gson.JsonObject

data class MatchResultResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String?>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<MatchResult>
)

data class MatchResult(
    val team: MatchResultTeam,
    val statistics: ArrayList<Statistics>
)

data class MatchResultTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class Statistics(
    val type: String,
    val value: Any
)