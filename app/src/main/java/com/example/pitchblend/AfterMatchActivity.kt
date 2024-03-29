package com.example.pitchblend

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.pitchblend.databinding.ActivityAfterMatchBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.json.JSONException
import org.jsoup.Jsoup
import java.util.Timer
import java.util.TimerTask

class AfterMatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfterMatchBinding
    private lateinit var backBtn: ImageButton
    private val api = RetrofitInterface.create() // 서버에서 불러오기

    private lateinit var homeTeamScorers: TextView
    private lateinit var awayTeamScorers: TextView
    private lateinit var xGHome: TextView
    private lateinit var xGAway: TextView
    private lateinit var shootingHome: TextView
    private lateinit var shootingAway: TextView
    private lateinit var attacksHome: TextView
    private lateinit var attacksAway: TextView
    private lateinit var cornersHome: TextView
    private lateinit var cornersAway: TextView
    private lateinit var offsideHome: TextView
    private lateinit var offsideAway: TextView
    private lateinit var totalPassesHome: TextView
    private lateinit var totalPassesAway: TextView
    private lateinit var percentPassesHome: TextView
    private lateinit var percentPassesAway: TextView
    private lateinit var possessionHome: TextView
    private lateinit var possessionAway: TextView
    private lateinit var possessionGraphHome: CardView
    private lateinit var possessionGraphAway: CardView
    private lateinit var foulsHome: TextView
    private lateinit var foulsAway: TextView
    private lateinit var yellowHome: TextView
    private lateinit var yellowAway: TextView
    private lateinit var redHome: TextView
    private lateinit var redAway: TextView

    private lateinit var homeStat: ArrayList<Statistics>
    private lateinit var awayStat: ArrayList<Statistics>

    private var homeScorers = ""
    private var awayScorers = ""

    private var homeId = 0
    private var awayId = 0

    private var homeScorersName = ArrayList<String>()
    private var awayScorersName = ArrayList<String>()
    private var homeScorersTime = ArrayList<String>()
    private var awayScorersTime = ArrayList<String>()
    private var homeScorersType = ArrayList<String>()
    private var awayScorersType = ArrayList<String>()
    private lateinit var homeDuplicateIndices : Map<String, List<String>>
    private lateinit var awayDuplicateIndices : Map<String, List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val matchId = intent.getIntExtra("matchId", -1)
        initiation()
        // AsyncTask를 사용하여 백그라운드 스레드에서 네트워크 요청 수행
        MyAsyncTask().execute()
        clickBackBtn()

//        if (intent.getStringExtra("matchTeams")!! == "Bournemouth VS Liverpool") {
//            searchYoutube()
//        }
        //searchYoutube()


        GlobalScope.launch(Dispatchers.Main) {
            getScorersResult(matchId)
            //getScorersResult(1035087)
            // 결과를 사용하는 로직
            Log.e("AfterMatchActivity", homeScorersName.toString())
            Log.e("AfterMatchActivity", awayScorersName.toString())
            homeDuplicateIndices = homeFindDuplicateIndices(homeScorersName)
            awayDuplicateIndices = awayFindDuplicateIndices(awayScorersName)
            for ((name, indices) in homeDuplicateIndices) {
                Log.e("AfterMatchActivity", "$name: $indices")
            }
            for ((name, indices) in awayDuplicateIndices) {
                Log.e("AfterMatchActivity", "$name: $indices")
            }
            writeHomeScorers()
            writeAwayScorers()
            Log.e("AfterMatchActivity", homeScorers)
            Log.e("AfterMatchActivity", awayScorers)
        }
        GlobalScope.launch(Dispatchers.Main) {
            getMatchResult(matchId)

            // 결과를 사용하는 로직
            for (stat in homeStat) {
                when (stat.type) {
                    "Shots on Goal" -> { attacksHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Total Shots" -> { shootingHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Fouls" -> { foulsHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Corner Kicks" -> { cornersHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Offsides" -> { offsideHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Ball Possession" -> {
                        possessionHome.text = if (stat.value != null) { stat.value.toString() } else { "0%" }
                        // 그래프 길이 조정하기
                        val homeLayoutParam = possessionGraphHome.layoutParams
                        if (stat.value != null) {
                            val home_possession_length = dpToPx300(stat.value.toString().replace("%", "").toInt() / 100f)
                            homeLayoutParam.width = home_possession_length
                            possessionGraphHome.layoutParams = homeLayoutParam
                        } else {
                            homeLayoutParam.width = 0
                            possessionGraphHome.layoutParams = homeLayoutParam
                        }
                    }
                    "Yellow Cards" -> { yellowHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Red Cards" -> { redHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Total passes" -> { totalPassesHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Passes %" -> { percentPassesHome.text = if (stat.value != null) { stat.value.toString() } else { "0%" }}
                    "expected_goals" -> { xGHome.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                }
            }
            for (stat in awayStat) {
                when (stat.type) {
                    "Shots on Goal" -> { attacksAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Total Shots" -> { shootingAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Fouls" -> { foulsAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Corner Kicks" -> { cornersAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Offsides" -> { offsideAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Ball Possession" -> {
                        possessionAway.text = if (stat.value != null) { stat.value.toString() } else { "0%" }
//                        // 그래프 길이 조정하기
//                        val awayLayoutParam = possessionGraphAway.layoutParams
//                        if (stat.value != null) {
//                            val away_possession_length = dpToPx300(stat.value.toString().replace("%", "").toInt() / 100f)
//                            awayLayoutParam.width = away_possession_length
//                            possessionGraphAway.layoutParams = awayLayoutParam
//                        } else {
//                            awayLayoutParam.width = 0
//                            possessionGraphAway.layoutParams = awayLayoutParam
//                        }
                    }
                    "Yellow Cards" -> { yellowAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Red Cards" -> { redAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Total passes" -> { totalPassesAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                    "Passes %" -> { percentPassesAway.text = if (stat.value != null) { stat.value.toString() } else { "0%" }}
                    "expected_goals" -> { xGAway.text = if (stat.value != null) { stat.value.toString().replace(".0", "") } else { "0" }}
                }
            }
        }
    }

    private fun initiation() {
        backBtn = binding.backBtn

        // intent로 넘겨온 애들 모두 ㄱㄱ
        binding.matchTitle.text = intent.getStringExtra("matchTeams")
        Picasso.get().load("${intent.getStringExtra("matchHomeLogo")}").into(binding.homeTeamLogo)
        Picasso.get().load("${intent.getStringExtra("matchAwayLogo")}").into(binding.awayTeamLogo)
        binding.matchHomeGoals.text = intent.getStringExtra("matchHomeGoals")
        binding.matchAwayGoals.text = intent.getStringExtra("matchAwayGoals")

        homeId = intent.getIntExtra("matchHomeId", 0)
        awayId = intent.getIntExtra("matchAwayId", 0)
//        homeId = 42
//        awayId = 47

        homeTeamScorers = binding.homeTeamScorers
        awayTeamScorers = binding.awayTeamScorers
        xGHome = binding.xGHome
        xGAway = binding.xGAway
        shootingHome = binding.shootingHome
        shootingAway = binding.shootingAway
        attacksHome = binding.attacksHome
        attacksAway = binding.attacksAway
        cornersHome = binding.cornersHome
        cornersAway = binding.cornersAway
        offsideHome = binding.offsideHome
        offsideAway = binding.offsideAway
        totalPassesHome = binding.totalPassesHome
        totalPassesAway = binding.totalPassesAway
        percentPassesHome = binding.passesPercentHome
        percentPassesAway = binding.passesPercentAway
        possessionHome = binding.possessionHome
        possessionAway = binding.possessionAway
        possessionGraphHome = binding.possessionGraphHome
        possessionGraphAway = binding.possessionGraphAway
        foulsHome = binding.foulsHome
        foulsAway = binding.foulsAway
        yellowHome = binding.yellowcardHome
        yellowAway = binding.yellowcardAway
        redHome = binding.redcardHome
        redAway = binding.redcardAway
    }

    private fun clickBackBtn() {
        backBtn.setOnClickListener {
            finish()
        }
    }

    private suspend fun getScorersResult(matchId: Int?, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getScorers(matchId!!) // goal 넣은 사람 나타냄
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                for (event in result.response) {
                    if (event.type == "Goal") {
                        if (event.team.id == homeId) {
                            homeScorersName.add(event.player.name)
                            if (event.time.extra == null) {
                                homeScorersTime.add("${event.time.elapsed}'")
                            } else {
                                homeScorersTime.add("${event.time.elapsed}'+${event.time.extra}'")
                            }
                            when (event.detail) {
                                "Penalty" -> {
                                    homeScorersType.add("PEN")
                                }
                                "Own Goal" -> {
                                    homeScorersType.add("OG")
                                }
                                else -> {
                                    homeScorersType.add("")
                                }
                            }
                        } else if (event.team.id == awayId) {
                            awayScorersName.add(event.player.name)
                            if (event.time.extra == null) {
                                awayScorersTime.add("${event.time.elapsed}'")
                            } else {
                                awayScorersTime.add("${event.time.elapsed}'+${event.time.extra}'")
                            }
                            when (event.detail) {
                                "Penalty" -> {
                                    awayScorersType.add("PEN")
                                }
                                "Own Goal" -> {
                                    awayScorersType.add("OG")
                                }
                                else -> {
                                    awayScorersType.add("")
                                }
                            }
                        }
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

    private fun homeFindDuplicateIndices(homeScorersName: ArrayList<String>): Map<String, List<String>> {
        val indexMap = mutableMapOf<String, MutableList<String>>()
        homeScorersName.forEachIndexed { index, scorerName ->
            // 이미 등장한 요소인 경우
            if (indexMap.containsKey(scorerName)) {
                // 해당 요소의 인덱스 목록에 현재 인덱스를 추가
                if (homeScorersType[index] != "") {
                    indexMap[scorerName]!!.add("${homeScorersTime[index]}"+" (${homeScorersType[index]})")
                } else {
                    indexMap[scorerName]!!.add("${homeScorersTime[index]}")
                }
            } else {
                // 처음 등장하는 요소인 경우, 새로운 리스트를 만들어 현재 인덱스를 추가
                if (homeScorersType[index] != "") {
                    indexMap[scorerName] = mutableListOf("${homeScorersTime[index]}"+" (${homeScorersType[index]})")
                } else {
                    indexMap[scorerName] = mutableListOf("${homeScorersTime[index]}")
                }
            }
        }

        // 인덱스 목록을 모두 반환
        return indexMap
    }
    private fun awayFindDuplicateIndices(awayScorersName: ArrayList<String>): Map<String, List<String>> {
        val indexMap = mutableMapOf<String, MutableList<String>>()
        awayScorersName.forEachIndexed { index, scorerName ->
            // 이미 등장한 요소인 경우
            if (indexMap.containsKey(scorerName)) {
                // 해당 요소의 인덱스 목록에 현재 인덱스를 추가
                if (awayScorersType[index] != "") {
                    indexMap[scorerName]!!.add("${awayScorersTime[index]}"+" (${awayScorersType[index]})")
                } else {
                    indexMap[scorerName]!!.add("${awayScorersTime[index]}")
                }
            } else {
                // 처음 등장하는 요소인 경우, 새로운 리스트를 만들어 현재 인덱스를 추가
                if (awayScorersType[index] != "") {
                    indexMap[scorerName] = mutableListOf("${awayScorersTime[index]}"+" (${awayScorersType[index]})")
                } else {
                    indexMap[scorerName] = mutableListOf("${awayScorersTime[index]}")
                }
            }
        }

        // 인덱스 목록을 모두 반환
        return indexMap
    }

    private fun writeHomeScorers() {
        for ((name, indices) in homeDuplicateIndices) {
            val goalTime = indices.toString().replace("[", "").replace("]", "")
            if (homeScorers == "") {
                homeScorers = name + " " + goalTime
            } else {
                homeScorers = homeScorers + "\n" + name + " " + goalTime
            }
        }
        homeTeamScorers.text = homeScorers
    }
    private fun writeAwayScorers() {
        for ((name, indices) in awayDuplicateIndices) {
            val goalTime = indices.toString().replace("[", "").replace("]", "")
            if (awayScorers == "") {
                awayScorers = name + " " + goalTime
            } else {
                awayScorers = awayScorers + "\n" + name + " " + goalTime
            }
        }
        awayTeamScorers.text = awayScorers
    }

    private suspend fun getMatchResult(matchId: Int?, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getMatchResult(matchId!!) // 전체 결과 stat 나타냄
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                homeStat = result.response[0].statistics
                awayStat = result.response[1].statistics

            } else {
                // API 응답이 null인 경우의 처리
                Log.e(ContentValues.TAG, "API 응답이 null입니다.")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e(ContentValues.TAG, "Error: ${e.message}")
        }
    }

    private fun dpToPx300(float: Float): Int {
        val desiredHeightInDp = 300 * float
        val pixels = (desiredHeightInDp * resources.displayMetrics.density).toInt()
        return pixels
    }


//    private fun searchYoutube() {
//        // YouTube API 호출 URL 설정
//        val searchURL =
//            "https://www.googleapis.com/youtube/v3/search?part=snippet&key=AIzaSyDk0muXlsKIlY5vCBd35KvCOZlx9HDyw8M"
//        val keyword = "&q=" + intent.getStringExtra("matchTeams")!! + " highlight video"
//        val maxResultsParam = "&maxResults=1"  // 여기를 1로 수정하여 하나의 비디오만 가져오도록 함
//        val youtubeSearchURL = searchURL + keyword + maxResultsParam
//
//        // 네트워크 통신을 위한 객체 생성
//        val requestQueue = Volley.newRequestQueue(this)
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, youtubeSearchURL, null,
//            { response ->
//                // API 호출 결과 실행
//                try {
//                    val jArr = response.getJSONArray("items")
//
//                    if (jArr.length() > 0) {
//                        val jData = jArr.optJSONObject(0)
//                        val jid = jData.optJSONObject("id")
//                        val videoId = jid.optString("videoId")
//
//                        // 썸네일 이미지 및 제목을 해당 뷰에 설정
//                        val thumbnailImageView: ImageView? = findViewById(R.id.trial_thumbnail)
//
//                        thumbnailImageView?.let {
//                            Glide.with(this)
//                                .load("https://i.ytimg.com/vi/$videoId/hqdefault.jpg") // 썸네일 이미지 URL
//                                .into(it)
//                        }
//                        thumbnailImageView?.setOnClickListener {
//                            val intent = Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("https://www.youtube.com/watch?v=$videoId")
//                            )
//
//                            // Intent를 처리할 수 있는 액티비티가 있는지 확인 후 실행
//                            if (intent.resolveActivity(this.packageManager) != null) {
//                                startActivity(intent)
//                            } else {
//                                // 유튜브 앱이나 웹 페이지가 없는 경우 사용자에게 메시지 표시
//                                Toast.makeText(
//                                    this,
//                                    "YouTube app not installed",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } else {
//                        // 검색 결과가 없을 경우 처리
//                        val thumbnailImageView: ImageView? = findViewById(R.id.youtubeThumbnailImageView)
//                        //val titleTextView: TextView? = findViewById(R.id.youtubeTitleTextView)
//
//                        //titleTextView?.text = "No videos found on YouTube"
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            },
//            { error -> Log.i("onErrorResponse", "" + error) })
//
//        requestQueue.add(jsonObjectRequest)
//    }






    private inner class MyAsyncTask : AsyncTask<Void, Void, String>() {

        private fun logIn() {
            val webView = WebView(this@AfterMatchActivity)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.savePassword = true
            webView.settings.saveFormData = true
            webView.loadUrl("https://accounts.google.com/ServiceLogin?service=youtube&uilel=3&passive=true&continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3Faction_handle_signin%3Dtrue%26app%3Dm%26hl%3Dtr%26next%3Dhttps%253A%252F%252Fm.youtube.com%252F")
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    // if webview redirects to youtube.com it means we're logged in
                    if (request?.url.toString().startsWith("https://m.youtube.com") ||
                        request?.url.toString().startsWith("https://www.youtube.com")
                    ) {
                        Log.d(TAG, "Logged in")
                        Toast.makeText(this@AfterMatchActivity, "Logged in", Toast.LENGTH_SHORT).show()
                        return false
                    }
                    return false
                }
            }
        }


        override fun doInBackground(vararg params: Void?): String {
            try {
                // 네트워크 요청 수행
                var makingUrl = ""
                if (homeId == 62) {
                    // 셰필드임. 셰필드를 Utd로 해놔서, 이것을 찾으셨나요? Sheffield United VS West Ham highlight video 이렇게 뜸.
                    makingUrl = "https://www.google.com/search?q=" + intent.getStringExtra("matchTeams")!!.replace("Utd", "United").replace(" ", "+") + "+highlight+video"
                } else {
                    makingUrl = "https://www.google.com/search?q=" + intent.getStringExtra("matchTeams")!!.replace(" ", "+") + "+highlight+video"
                }
                val url = makingUrl
                Log.e("google Url", url)
                val doc = Jsoup.connect(url).get()

                // body 태그 안에 있는 모든 링크 찾기
                val links = doc.select("a")

                // 12번째 링크의 href 가져오기
                if (links.size >= 12) {
                    val twelfthLink = links[11]
                    return twelfthLink.attr("href")
                } else {
                    return "Not enough links found."
                }
            } catch (e: Exception) {
                return "Failed to retrieve the page."
            }
            logIn()
        }

        override fun onPostExecute(result: String) {
            // 결과 출력
            Log.e("google HTML", result)
            logIn()
            // XML에서 정의한 YouTubePlayerView에 대한 참조 획득
            val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
            // 동적으로 videoId 설정
            val videoId = result.replace("https://www.youtube.com/watch?v=", "")
//            val youtubeThumbnail: ImageView = findViewById(R.id.google_trial_thumbnail)
//            val thumbnailUrl = "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"
//            Picasso.get().load(thumbnailUrl).into(youtubeThumbnail)
            Log.e("google HTML", videoId)
//            youtubeThumbnail.setOnClickListener {
//                youtubeThumbnail.visibility = View.GONE
//            }
//            Handler(Looper.getMainLooper()).postDelayed({
//                youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                    override fun onReady(youTubePlayer: YouTubePlayer) {
//                        youTubePlayer.cueVideo(videoId, 0f)
//                    }
//                })
//            }, 700)

            val webView = WebView(this@AfterMatchActivity)

            // Load ayp_youtube_player.html from the raw resource folder
            //val htmlString = resources.openRawResource(R.raw.ayp_youtube_player).bufferedReader().use { it.readText() }
            //webView.loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html", "UTF-8", null)
            //webView.loadUrl("javascript:initializeAdBlock()")

            //.loadUrl("javascript:initializeAdBlock()")
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    //webView.loadUrl("javascript:initializeAdBlock()")
                    youTubePlayer.cueVideo(videoId, 0f)
                    webView.loadUrl("javascript:initializeAdBlock()")
                }
//                override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
//                    webView.loadUrl("javascript:initializeAdBlock()")
//                    // 플레이어 상태 변경을 처리하는 메서드가 있다고 가정합니다.
//                }
            })




            // 만약에 play 되게 하고 싶다면, 이 주석을 풀어주면 됨
//            youtubeThumbnail.setOnClickListener {
//                youtubeThumbnail.visibility = View.GONE
//            }
//            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    youTubePlayer.cueVideo(videoId, 0f)
//                }
//            })

        }


    }

}