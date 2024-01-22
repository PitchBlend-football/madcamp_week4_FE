package com.example.pitchblend

import com.google.gson.JsonObject

data class MatchScheduleResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<Schedule>
)


data class Schedule(
    val fixture: Fixture,
    val league: JsonObject,
    val teams: Teams,
    val goals: Goals,
    val score: JsonObject
)

data class Fixture(
    val id: Int,
    val referee: String,
    val timezone: String,
    val date: String,
    val timestamp: Long,
    val periods: JsonObject,
    val venue: JsonObject,
    val status: JsonObject
)

data class Teams(
    val home: MatchTeamInfo,
    val away: MatchTeamInfo
)

data class MatchTeamInfo(
    val id: Int,
    val name: String,
    val logo: String,
    val winner: Boolean?
)

data class Goals(
    val home: Int?,
    val away: Int?
)