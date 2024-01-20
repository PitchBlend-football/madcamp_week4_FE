package com.example.pitchblend

import com.google.gson.JsonObject

data class FixtureResponse(
    val get: String,
    val parameters: JsonObject,
    val errors: ArrayList<String>,
    val results: Int,
    val paging: JsonObject,
    val response: ArrayList<JsonObject>,
    val fixtureIds: ArrayList<Int>
)