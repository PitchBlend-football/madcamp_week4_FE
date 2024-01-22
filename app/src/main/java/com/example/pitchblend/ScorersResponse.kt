package com.example.pitchblend

import com.google.gson.JsonObject

data class ScorersResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String?>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<Events>
)

data class Events(
    val time: EventTime,
    val team: EventTeam,
    val player: EventPlayer,
    val assist: EventAssist?,
    val type: String,
    val detail: String,
    val comment: String?
)

data class EventTime(
    val elapsed: Int,
    val extra: Int? // 추가시간
)

data class EventTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class EventPlayer(
    val id: Int,
    val name: String // 교체 아웃
)

data class EventAssist(
    val id: Int?,
    val name: String? // 교체 인
)
