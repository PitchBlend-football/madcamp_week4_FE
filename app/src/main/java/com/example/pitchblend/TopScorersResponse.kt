package com.example.pitchblend

import com.google.gson.JsonObject

data class TopScorersResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String?>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<Players>
)

data class Players(
    val player: PlayerInfo,
    val statistics: ArrayList<PlayerStats>
)

data class PlayerInfo(
    val id: Int,
    val name: String,
    val firstname: String,
    val lastname: String,
    val age: String,
    val birth: JsonObject,
    val nationality: Any,
    val height: Any,
    val weight: Any,
    val injured: Boolean?,
    val photo: Any
)

data class PlayerStats(
    val team: JsonObject,
    val league: JsonObject,
    val games: JsonObject,
    val substitutes: JsonObject,
    val shots: JsonObject,
    val goals: PlayerGoals,
    val passes: JsonObject,
    val tackles: JsonObject,
    val duels: JsonObject,
    val dribbles: JsonObject,
    val fouls: JsonObject,
    val cards: JsonObject,
    val penalty: JsonObject
)

data class PlayerGoals(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
)