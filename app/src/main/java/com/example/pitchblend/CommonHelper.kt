package com.example.pitchblend

import kotlinx.coroutines.CoroutineScope

class CommonHelper {
    companion object {
        fun getTeamIni(context: CoroutineScope, teamName: String): String {
            var teamIni = ""
            when (teamName) {
                "Liverpool" -> { teamIni = "LIV" }
                "Manchester City" -> { teamIni = "MCI" }
                "Arsenal" -> { teamIni = "ARS" }
                "Aston Villa" -> { teamIni = "AVL" }
                "Tottenham" -> { teamIni = "TOT" }
                "West Ham" -> { teamIni = "WHU" }
                "Brighton" -> { teamIni = "BHA" }
                "Manchester United" -> { teamIni = "MCU" }
                "Chelsea" -> { teamIni = "CHE" }
                "Newcastle" -> { teamIni = "NEW" }
                "Wolves" -> { teamIni = "WOL" }
                "Bournemouth" -> { teamIni = "BOU" }
                "Fulham" -> { teamIni = "FUL" }
                "Brentford" -> { teamIni = "BRE" }
                "Crystal Palace" -> { teamIni = "CRY" }
                "Nottingham Forest" -> { teamIni = "NFO" }
                "Everton" -> { teamIni = "EVE" }
                "Luton" -> { teamIni = "LUT" }
                "Burnley" -> { teamIni = "BUR" }
                "Sheffield Utd" -> { teamIni = "SHU" }
            }

            return teamIni
        }
    }
}