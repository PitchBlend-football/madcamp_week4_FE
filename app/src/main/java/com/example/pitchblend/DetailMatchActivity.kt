package com.example.pitchblend

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.pitchblend.databinding.ActivityDetailMatchBinding
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow
import kotlin.math.roundToInt

class DetailMatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMatchBinding
    private lateinit var backBtn: ImageButton
    private lateinit var seeAiExpectBtn: ImageButton // AI expect 토글 보는 버튼
    private lateinit var aiExpectCardView: CardView // AI expect 메인 카드뷰 부분
    private val api = RetrofitInterface.create() // 서버에서 불러오기

    private lateinit var home_win_percent: String
    private lateinit var away_win_percent: String
    private lateinit var draw_percent: String
    private var home_win_length: Int = 0
    private var away_win_length: Int = 0
    private var draw_length: Int = 0
    private lateinit var home_expected_goals: String
    private lateinit var away_expected_goals: String

    private lateinit var home_win_percent_txt: TextView
    private lateinit var away_win_percent_txt: TextView
    private lateinit var draw_percent_txt: TextView
    private lateinit var home_win_percent_graph: CardView
    private lateinit var away_win_percent_graph: CardView
    private lateinit var draw_percent_graph: CardView
    private lateinit var home_expected_goals_txt: TextView
    private lateinit var away_expected_goals_txt: TextView

    private lateinit var home_recent_3_matches: String
    private lateinit var away_recent_3_matches: String
    private lateinit var home_avg_goals: String
    private lateinit var away_avg_goals: String
    private lateinit var home_avg_conceded: String
    private lateinit var away_avg_conceded: String
    private var home_encounters_count = 0
    private var away_encounters_count = 0
    private var draw_encounters_count = 0
    private var home_encounters_length = 0
    private var draw_encounters_length = 0

    private lateinit var home_recent_3rd: ImageView
    private lateinit var home_recent_2nd: ImageView
    private lateinit var home_recent_1st: ImageView
    private lateinit var home_recent_1st_bottom: CardView
    private lateinit var away_recent_3rd: ImageView
    private lateinit var away_recent_2nd: ImageView
    private lateinit var away_recent_1st: ImageView
    private lateinit var away_recent_1st_bottom: CardView

    private lateinit var home_avg_goal_txt: TextView
    private lateinit var away_avg_goal_txt: TextView
    private lateinit var home_avg_conceded_txt: TextView
    private lateinit var away_avg_conceded_txt: TextView

    private lateinit var home_recent_encounters_percent: TextView
    private lateinit var away_recent_encounters_percent: TextView
    private lateinit var home_win_recent_graph: CardView
    private lateinit var away_win_recent_graph: CardView
    private lateinit var draw_recent_graph: CardView
    private lateinit var home_recent_encounters_num: TextView
    private lateinit var away_recent_encounters_num: TextView
    private lateinit var draw_recent_encounters_num: TextView

    private var homeId = 0
    private var awayId = 0

    private var goals = 0
    private var topScorerPlayer = ""
    private lateinit var top_scorers_home: TextView
    private lateinit var top_scorers_away: TextView
    private lateinit var image_card_home: ImageView
    private lateinit var image_card_away: ImageView
    private lateinit var expected_linup_pic: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiation()
        clickBackBtn()
        clickAiExpectBtn()
        val matchId = intent.getIntExtra("matchId", -1)
        Log.e("matchId", "matchId: $matchId")
        GlobalScope.launch(Dispatchers.Main) {
            getMatchExpect(matchId)
            // 결과를 사용하는 로직
            matchExpectedResultsAi()
            matchPreview()
        }
        GlobalScope.launch(Dispatchers.Main) {
            getTopScorerHome(homeId)
            //getTopScorerAway(awayId)
            // 결과를 사용하는 로직
            //top_scorers_home.text = "$topScorerPlayer $goals" // 나중에 이거 아예 빼기. api 자체가 이상하다 선수가 없음.
            setExtra()
        }
    }

    private fun initiation() {
        backBtn = binding.backBtn
        seeAiExpectBtn = binding.aiDownBtn
        aiExpectCardView = binding.aiCardview

        // intent로 넘겨온 애들 모두 ㄱㄱ
        binding.matchTitle.text = intent.getStringExtra("matchTeams")
        Picasso.get().load("${intent.getStringExtra("matchHomeLogo")}").into(binding.homeTeamLogo)
        Picasso.get().load("${intent.getStringExtra("matchAwayLogo")}").into(binding.awayTeamLogo)
        binding.matchStartDate.text = intent.getStringExtra("matchDate")
        binding.matchStartTime.text = intent.getStringExtra("matchTime")

        homeId = intent.getIntExtra("matchHomeId", 0)
        awayId = intent.getIntExtra("matchAwayId", 0)


        home_win_percent_txt = binding.cardHomePercent
        away_win_percent_txt = binding.cardAwayPercent
        draw_percent_txt = binding.cardDrawPercent
        home_win_percent_graph = binding.expectedResultGraphHome
        away_win_percent_graph = binding.expectedResultGraphAway
        draw_percent_graph = binding.expectedResultGraphDraw
        home_expected_goals_txt = binding.expectedGoalsHome
        away_expected_goals_txt = binding.expectedGoalsAway

        home_recent_3rd = binding.recentHome3rdImg
        home_recent_2nd = binding.recentHome2ndImg
        home_recent_1st = binding.recentHome1stImg
        home_recent_1st_bottom = binding.recentHome1stBottom
        away_recent_3rd = binding.recentAway3rdImg
        away_recent_2nd = binding.recentAway2ndImg
        away_recent_1st = binding.recentAway1stImg
        away_recent_1st_bottom = binding.recentAway1stBottom

        home_avg_goal_txt = binding.avgGoalHome
        away_avg_goal_txt = binding.avgGoalAway
        home_avg_conceded_txt = binding.avgConcededHome
        away_avg_conceded_txt = binding.avgConcededAway

        home_recent_encounters_percent = binding.recentEncountersHome
        away_recent_encounters_percent = binding.recentEncountersAway
        home_win_recent_graph = binding.recentEncountersGraphHome
        away_win_recent_graph = binding.recentEncountersGraphAway
        draw_recent_graph = binding.recentEncountersGraphDraw
        home_recent_encounters_num = binding.recentEncountersCardHomeNum
        away_recent_encounters_num = binding.recentEncountersCardAwayNum
        draw_recent_encounters_num = binding.recentEncountersCardDrawNum

        top_scorers_home = binding.topScorersHome
        top_scorers_away = binding.topScorersAway
        image_card_home = binding.imageCardHome
        image_card_away = binding.imageCardAway
        expected_linup_pic = binding.expectedLinupPic

    }

    private fun clickBackBtn() {
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun clickAiExpectBtn() {
        val valueAnimator = if (aiExpectCardView.visibility == View.VISIBLE) {
            0 // 이미 펼쳐져 있으면 나중에 0으로 축소해야
        } else {
            aiExpectCardView.layoutParams.height // 접혀져 있으면 나중에 펼쳐야 함
        }

        seeAiExpectBtn.setOnClickListener {
            aiExpectCardView.pivotY = 0f
            if (aiExpectCardView.visibility == View.VISIBLE) {
//                aiExpectCardView.visibility = View.GONE
                seeAiExpectBtn.animate().setDuration(200).rotation(0f)
                aiExpectCardView.animate().setDuration(200).scaleY(0f)
                    .withEndAction { aiExpectCardView.visibility = View.GONE }
            } else {
                aiExpectCardView.visibility = View.VISIBLE
                aiExpectCardView.scaleY = 0f
                aiExpectCardView.animate().setDuration(200).scaleY(1f)
                seeAiExpectBtn.animate().setDuration(200).rotation(180f)
            }
        }
    }

//    private suspend fun getMatchId(home: String, away: String): Int? {
//        return withContext(Dispatchers.IO) {
//            var matchId: Int? = null
//            val call = api.getOneDayMatchInfo("2023-09-24", "2023")
//            try {
//                val response = call.execute()
//                if (response.isSuccessful) {
//                    val result = response.body()
//                    if (result != null) {
//                        for (index in result.response) {
//                            val matchTeams = index.getAsJsonObject("teams")
//                            if (matchTeams.getAsJsonObject("home")?.get("name")?.asString == home || matchTeams.getAsJsonObject("home")?.get("name")?.asString == away) {
//                                matchId = index.getAsJsonObject("fixture")?.get("id")?.asInt
//                                break
//                            }
//                        }
//                    } else {
//                        // API 응답이 null인 경우의 처리
//                        Log.e(ContentValues.TAG, "API 응답이 null입니다.")
//                    }
//                } else {
//                    // HTTP 요청이 실패한 경우의 처리
//                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                // 예외 처리
//                Log.e(ContentValues.TAG, "Error: ${e.message}")
//            }
//            matchId
//        }
//    }

    private suspend fun getMatchExpect(matchId: Int?, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getMatchPredictions(matchId!!)
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                val predictions = result.getAsJsonArray("response")[0].asJsonObject.getAsJsonObject("predictions")
                val getPercent = predictions.getAsJsonObject("percent")
                home_win_percent = getPercent.get("home").asString
                away_win_percent = getPercent.get("away").asString
                draw_percent = getPercent.get("draw").asString
                val getGoals = predictions.getAsJsonObject("goals")
                home_expected_goals = getGoals.get("home").asString
                away_expected_goals = getGoals.get("away").asString

                val preview = result.getAsJsonArray("response")[0].asJsonObject.getAsJsonObject("teams")
                home_avg_goals = preview.getAsJsonObject("home").getAsJsonObject("last_5").getAsJsonObject("goals").getAsJsonObject("for").get("average").asString
                away_avg_goals = preview.getAsJsonObject("away").getAsJsonObject("last_5").getAsJsonObject("goals").getAsJsonObject("for").get("average").asString
                home_avg_conceded = preview.getAsJsonObject("home").getAsJsonObject("last_5").getAsJsonObject("goals").getAsJsonObject("against").get("average").asString
                away_avg_conceded = preview.getAsJsonObject("away").getAsJsonObject("last_5").getAsJsonObject("goals").getAsJsonObject("against").get("average").asString
                home_recent_3_matches = preview.getAsJsonObject("home").getAsJsonObject("league").get("form").asString
                away_recent_3_matches = preview.getAsJsonObject("away").getAsJsonObject("league").get("form").asString

                val homeTeamName = preview.getAsJsonObject("home").get("name").asString
                val awayTeamName = preview.getAsJsonObject("away").get("name").asString

                // 이길 확률은 전체에서
                // h2h - teams - winner:true인 애들이 이긴거. null이면 무승부. 그거 하나하나 count하면 됨.
                val h2h = result.getAsJsonArray("response")[0].asJsonObject.getAsJsonArray("h2h")

                // home team 승리 & draw 횟수 모두 더하기
                for (match in h2h) {
                    val h2hTeams = match.asJsonObject.getAsJsonObject("teams")
                    if (h2hTeams.getAsJsonObject("home").get("name").asString == homeTeamName) {
                        if (h2hTeams.getAsJsonObject("home").get("winner").isJsonNull) {
                            draw_encounters_count += 1
                        } else  {
                            if (h2hTeams.getAsJsonObject("home").get("winner").asBoolean == true) {
                                home_encounters_count += 1
                            }
                        }
                    } else if (h2hTeams.getAsJsonObject("away").get("name").asString == homeTeamName) {
                        if (h2hTeams.getAsJsonObject("away").get("winner").isJsonNull) {
                            draw_encounters_count += 1
                        } else  {
                            if (h2hTeams.getAsJsonObject("away").get("winner").asBoolean == true) {
                                home_encounters_count += 1
                            }
                        }
                    }
                }

                // away team 승리 횟수 모두 더하기
                for (match in h2h) {
                    val h2hTeams = match.asJsonObject.getAsJsonObject("teams")
                    if (h2hTeams.getAsJsonObject("home").get("name").asString == awayTeamName) {
                        if (h2hTeams.getAsJsonObject("home").get("winner").isJsonNull) {
                            continue
                        } else  {
                            if (h2hTeams.getAsJsonObject("home").get("winner").asBoolean == true) {
                                away_encounters_count += 1
                            }
                        }
                    } else if (h2hTeams.getAsJsonObject("away").get("name").asString == awayTeamName) {
                        if (h2hTeams.getAsJsonObject("away").get("winner").isJsonNull) {
                            continue
                        } else  {
                            if (h2hTeams.getAsJsonObject("away").get("winner").asBoolean == true) {
                                away_encounters_count += 1
                            }
                        }
                    }
                }

//                // draw 횟수 모두 더하기
//                for (match in h2h) {
//                    val h2hTeams = match.asJsonObject.getAsJsonObject("teams")
//                    if (h2hTeams.getAsJsonObject("home").get("winner").isJsonNull) {
//                        draw_encounters_count += 1
//                    }
//                }

            } else {
                // API 응답이 null인 경우의 처리
                Log.e(ContentValues.TAG, "API 응답이 null입니다.")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e(ContentValues.TAG, "Error: ${e.message}")
        }
    }

    private fun matchExpectedResultsAi() {
        home_win_length = dpToPx300(home_win_percent.replace("%", "").toInt() / 100f)
        draw_length = dpToPx300((draw_percent.replace("%", "").toInt() / 100f) + (home_win_percent.replace("%", "").toInt() / 100f))

        // 각 % 부분 match해주기
        home_win_percent_txt.text = home_win_percent
        away_win_percent_txt.text = away_win_percent
        draw_percent_txt.text = draw_percent
        
        // 그래프 길이 조정하기
        val homeLayoutParam = home_win_percent_graph.layoutParams
        val drawLayoutParam = draw_percent_graph.layoutParams
        homeLayoutParam.width = home_win_length
        drawLayoutParam.width = draw_length
        home_win_percent_graph.layoutParams = homeLayoutParam
        draw_percent_graph.layoutParams = drawLayoutParam

        // expected goals match해주기
        home_expected_goals_txt.text = home_expected_goals.replace("-", "")
        away_expected_goals_txt.text = away_expected_goals.replace("-", "")

    }

    private fun matchPreview() {

        // Recent 3 Matche
        home_recent_3_matches = home_recent_3_matches.substring(home_recent_3_matches.length - 3)
        away_recent_3_matches = away_recent_3_matches.substring(away_recent_3_matches.length - 3)
        var homeCount = 0
        var awayCount = 0

        for (char in home_recent_3_matches) {
            if (homeCount == 0) {
                when (char) {
                    'W' -> { home_recent_3rd.setImageResource(R.drawable.recent_win) }
                    'D' -> { home_recent_3rd.setImageResource(R.drawable.recent_draw) }
                    'L' -> { home_recent_3rd.setImageResource(R.drawable.recent_lose) }
                }
            } else if (homeCount == 1) {
                when (char) {
                    'W' -> { home_recent_2nd.setImageResource(R.drawable.recent_win) }
                    'D' -> { home_recent_2nd.setImageResource(R.drawable.recent_draw) }
                    'L' -> { home_recent_2nd.setImageResource(R.drawable.recent_lose) }
                }
            } else if (homeCount == 2) {
                when (char) {
                    'W' -> {
                        home_recent_1st.setImageResource(R.drawable.recent_win)
                        home_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(home_recent_1st_bottom.context, R.color.win))
                    }
                    'D' -> {
                        home_recent_1st.setImageResource(R.drawable.recent_draw)
                        home_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(home_recent_1st_bottom.context, R.color.draw))
                    }
                    'L' -> {
                        home_recent_1st.setImageResource(R.drawable.recent_lose)
                        home_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(home_recent_1st_bottom.context, R.color.lose))
                    }
                }
            }
            homeCount += 1
        }

        for (char in away_recent_3_matches) {
            if (awayCount == 0) {
                when (char) {
                    'W' -> { away_recent_3rd.setImageResource(R.drawable.recent_win) }
                    'D' -> { away_recent_3rd.setImageResource(R.drawable.recent_draw) }
                    'L' -> { away_recent_3rd.setImageResource(R.drawable.recent_lose) }
                }
            } else if (awayCount == 1) {
                when (char) {
                    'W' -> { away_recent_2nd.setImageResource(R.drawable.recent_win) }
                    'D' -> { away_recent_2nd.setImageResource(R.drawable.recent_draw) }
                    'L' -> { away_recent_2nd.setImageResource(R.drawable.recent_lose) }
                }
            } else if (awayCount == 2) {
                when (char) {
                    'W' -> {
                        away_recent_1st.setImageResource(R.drawable.recent_win)
                        away_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(away_recent_1st_bottom.context, R.color.win))
                    }
                    'D' -> {
                        away_recent_1st.setImageResource(R.drawable.recent_draw)
                        away_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(away_recent_1st_bottom.context, R.color.draw))
                    }
                    'L' -> {
                        away_recent_1st.setImageResource(R.drawable.recent_lose)
                        away_recent_1st_bottom.setCardBackgroundColor(ContextCompat.getColor(away_recent_1st_bottom.context, R.color.lose))
                    }
                }
            }
            awayCount += 1
        }


        // Average Goals
        home_avg_goal_txt.text = home_avg_goals
        away_avg_goal_txt.text = away_avg_goals

        // Average Conceded
        home_avg_conceded_txt.text = home_avg_conceded
        away_avg_conceded_txt.text = away_avg_conceded

        // Recent Encounters
        Log.d("DetailMatchActivity", home_encounters_count.toString())
        Log.d("DetailMatchActivity", away_encounters_count.toString())
        Log.d("DetailMatchActivity", draw_encounters_count.toString())
        home_recent_encounters_num.text = home_encounters_count.toString()
        away_recent_encounters_num.text = away_encounters_count.toString()
        draw_recent_encounters_num.text = draw_encounters_count.toString()
        val whole_count = home_encounters_count + away_encounters_count + draw_encounters_count
        val homePercentage: Double = (home_encounters_count.toDouble() / whole_count.toDouble()) * 100.0
        val awayPercentage: Double = (away_encounters_count.toDouble() / whole_count.toDouble()) * 100.0
        val drawPercentage: Double = (draw_encounters_count.toDouble() / whole_count.toDouble()) * 100.0
        val homeRoundedPercentage: Int = homePercentage.round(0).toInt()
        val awayRoundedPercentage: Int = awayPercentage.round(0).toInt()
        val drawRoundedPercentage: Int = drawPercentage.round(0).toInt()
        home_recent_encounters_percent.text = "$homeRoundedPercentage%"
        away_recent_encounters_percent.text = "$awayRoundedPercentage%"

        home_encounters_length = dpToPx300(homeRoundedPercentage / 100f)
        draw_encounters_length = dpToPx300((drawRoundedPercentage / 100f) + (homeRoundedPercentage / 100f))
        // 그래프 길이 조정하기
        val homeLayoutParam = home_win_recent_graph.layoutParams
        val drawLayoutParam = draw_recent_graph.layoutParams
        homeLayoutParam.width = home_encounters_length
        drawLayoutParam.width = draw_encounters_length
        home_win_recent_graph.layoutParams = homeLayoutParam
        draw_recent_graph.layoutParams = drawLayoutParam

    }

    private suspend fun getTopScorerHome(homeId: Int?, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getTopScorer(homeId!!) // goal 넣은 사람 나타냄
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                val players = result.response
                for (player in players) {
                    val playerGoal = if (player.statistics[0].goals.total != null) { player.statistics[0].goals.total } else { 0 }
                    if (goals <= playerGoal!!) {
                        goals = playerGoal
                        topScorerPlayer = player.player.name
                    }
                }
            } else {
                // API 응답이 null인 경우의 처리
                Log.e(ContentValues.TAG, "API 응답이 null입니다.")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e(ContentValues.TAG, "Error: ${e.message}")
        }
    }

    private fun setExtra() {
        if (intent.getStringExtra("matchTeams") == "Nottingham Forest VS Arsenal") {
            image_card_home.setImageResource(R.drawable.chris_wood)
            top_scorers_home.text = "8 Goals"
            image_card_away.setImageResource(R.drawable.bukayo_saka_card)
            top_scorers_away.text = "6 Goals"
            expected_linup_pic.setImageResource(R.drawable.nfo_ars)
        } else if (intent.getStringExtra("matchTeams") == "Luton VS Brighton") {
            image_card_home.setImageResource(R.drawable.adebayo_card)
            top_scorers_home.text = "5 Goals"
            image_card_away.setImageResource(R.drawable.pedro_card)
            top_scorers_away.text = "7 Goals"
            expected_linup_pic.setImageResource(R.drawable.lut_bha)
        } else if (intent.getStringExtra("matchTeams") == "Fulham VS Everton") {
            image_card_home.setImageResource(R.drawable.jimenez_card)
            top_scorers_home.text = "5 Goals"
            image_card_away.setImageResource(R.drawable.doucoure_card)
            top_scorers_away.text = "6 Goals"
            expected_linup_pic.setImageResource(R.drawable.ful_eve)
        }
    }

    private fun dpToPx300(float: Float): Int {
        val desiredHeightInDp = 300 * float
        val pixels = (desiredHeightInDp * resources.displayMetrics.density).toInt()
        return pixels
    }

    private fun Double.round(decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (this * factor).roundToInt() / factor
    }

}
