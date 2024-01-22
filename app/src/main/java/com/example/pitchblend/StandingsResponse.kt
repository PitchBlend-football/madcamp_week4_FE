package com.example.pitchblend

import com.google.gson.JsonObject

data class StandingsResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<League>
)

data class League(
    val league: LeagueData
)

data class LeagueData(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: String,
    val standings: ArrayList<ArrayList<Team>>
)

data class Team(
    val rank: Int,
    val team: TeamInfo,
    val points: Int,
    val goalsDiff: Int,
    val group: String,
    val form: String,
    val status: String,
    val description: String,
    val all: All,
    val home: JsonObject,
    val away: JsonObject,
    val update: String
)

data class TeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)

data class All(
    val played: Int,
    val win: Int,
    val draw: Int,
    val lose: Int,
    val goals: JsonObject
)