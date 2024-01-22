// ExploreFragment.kt

package com.example.pitchblend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

// ExploreFragment.kt
class ExploreFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var queue: RequestQueue
    private lateinit var toggleLayout: LinearLayout
    private var aiTitleTextView: TextView? = null

    private lateinit var secondRecyclerView: RecyclerView
    private lateinit var secondAdapter: RecyclerView.Adapter<*>
    private lateinit var secondLayoutManager: RecyclerView.LayoutManager
    private lateinit var teamName: String


    private lateinit var accessToken: String

    private val api = RetrofitInterface.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val sharedPref= requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        accessToken = sharedPref.getString("accessToken", "")!!
        Log.e("accessToken", "accessToken: $accessToken")

        mRecyclerView = view.findViewById(R.id.my_recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        secondRecyclerView = view.findViewById(R.id.second_recycler_view)
        secondRecyclerView.setHasFixedSize(true)
        secondLayoutManager = LinearLayoutManager(requireContext())
        secondRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        queue = Volley.newRequestQueue(requireContext())
        getNews()
        getSecondNews()

        setupToggleAnimation(view)
        getTeamInfo()

        return view
    }

    private fun getTeamInfo() {
        Log.e("getTeamInfo", "실행되냐")
        if (accessToken != null) {
            Log.e("getTeamInfo", "accesstoken이 null이냐")
            api.getTeamInfo("Bearer $accessToken").enqueue(object : Callback<TeamInfo> {
                override fun onResponse(
                    call: Call<TeamInfo>,
                    response: retrofit2.Response<TeamInfo>
                ) {
                    if (response.isSuccessful) {
                        val teamInfo = response.body()
                        Log.d("TeamInfo", "${teamInfo}")// 이 부분이 호출되어야 UI가 업데이트됨
                        if (teamInfo != null) {
                            teamName = teamInfo.teamName
                            updateUI(teamInfo)
                            val searchQuery = "$teamName man latest interview with couch"
                            searchYoutube(searchQuery)
                            Log.e("youtubeSearch", searchQuery)
                        } else {
                            // 팀 정보가 null인 경우 처리
                            Log.e(ContentValues.TAG, "null이냐")
                        }
                    } else {
                        // 서버 응답이 실패한 경우 처리
                        Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<TeamInfo>, t: Throwable) {
                    // 통신 실패 처리
                    Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                }
            })
        } else {

        }
    }

    private fun updateUI(teamInfo: TeamInfo) {
        // Initialize the TextView and ImageView
        val aiTitleTextView: TextView? = view?.findViewById(R.id.ai_title)
        val aiIconImageView: ImageView? = view?.findViewById(R.id.ai_icon)

        // UI 업데이트 코드 작성
        aiTitleTextView?.text = teamInfo.newsText // TextView가 Nullable이므로 안전한 호출 사용

        // Glide를 사용하여 이미지 로드
        aiIconImageView?.let {
            Glide.with(this)
                .load(teamInfo.logoUrl)
                .into(it)
        }

        // 추가: 로그로 확인
        Log.d("ExploreFragment", "UI Updated - Title: ${teamInfo.newsText}, Logo URL: ${teamInfo.logoUrl}")
    }

    private fun setupToggleAnimation(rootView: View) {
        val toggleCardView: CardView = rootView.findViewById(R.id.toggle)
        val aiDownButton: ImageButton = rootView.findViewById(R.id.ai_down_btn)
        val aiMainLayout: ConstraintLayout = rootView.findViewById(R.id.ai_main)

        aiDownButton.setOnClickListener {
            val isExpanded = (toggleCardView.visibility == View.VISIBLE)

            if (isExpanded) {
                toggleCardView.animate()
                    .setDuration(200)
                    .scaleY(0f)
                    .withEndAction {
                        toggleCardView.visibility = View.GONE
                        toggleCardView.scaleY = 1f
                    }
                aiDownButton.animate().setDuration(200).rotation(0f)
            } else {
                toggleCardView.visibility = View.VISIBLE
                toggleCardView.scaleY = 0f
                toggleCardView.animate()
                    .setDuration(200)
                    .scaleY(1f)
                aiDownButton.animate().setDuration(200).rotation(180f)
            }

            // ai_main에 맞춰서 위치하도록 설정
            val params = toggleCardView.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.ai_main)
            params.marginStart = aiMainLayout.marginStart
            params.marginEnd = aiMainLayout.marginEnd
            toggleCardView.layoutParams = params
        }
    }

    private fun searchYoutube(query: String) {
//        // YouTube API 호출 URL 설정
//        val searchURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&key=AIzaSyDGnaQkcHDw8-7QVzKs5VtCXIbkI8JFIZ8"
//        val keyword = "&q=" + query
//        val maxResultsParam = "&maxResults=1"  // 여기를 1로 수정하여 하나의 비디오만 가져오도록 함
//        val youtubeSearchURL = searchURL + keyword + maxResultsParam
//
//        // 네트워크 통신을 위한 객체 생성
//        val requestQueue = Volley.newRequestQueue(requireContext())
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, youtubeSearchURL, null,
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
//                        val thumbnailImageView: ImageView? = view?.findViewById(R.id.youtubeThumbnailImageView)
//                        val titleTextView: TextView? = view?.findViewById(R.id.youtubeTitleTextView)
//
//                        thumbnailImageView?.let {
//                            Glide.with(requireContext())
//                                .load("https://i.ytimg.com/vi/$videoId/hqdefault.jpg") // 썸네일 이미지 URL
//                                .into(it)
//                        }
//
//                        titleTextView?.text = jData.optJSONObject("snippet")?.optString("title")
//
//                        // 클릭 시 유튜브 비디오를 재생하기 위한 Intent 생성
//                        titleTextView?.setOnClickListener {
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
//
//                            // Intent를 처리할 수 있는 액티비티가 있는지 확인 후 실행
//                            if (intent.resolveActivity(requireContext().packageManager) != null) {
//                                startActivity(intent)
//                            } else {
//                                // 유튜브 앱이나 웹 페이지가 없는 경우 사용자에게 메시지 표시
//                                Toast.makeText(requireContext(), "YouTube app not installed", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } else {
//                        // 검색 결과가 없을 경우 처리
//                        val thumbnailImageView: ImageView? = view?.findViewById(R.id.youtubeThumbnailImageView)
//                        val titleTextView: TextView? = view?.findViewById(R.id.youtubeTitleTextView)
//
//                        titleTextView?.text = "No videos found on YouTube"
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            },
//            { error -> Log.i("onErrorResponse", "" + error) })
//
//        requestQueue.add(jsonObjectRequest)
    }
    private fun getSecondNews() {
        val url = "https://newsapi.org/v2/everything?q=epl&language=en&apiKey=913a13e56be549f29ab1a4887d74b80c"

        val stringRequest = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                Log.d("SECOND_NEWS", response)

                try {
                    val jsonObj = JSONObject(response)
                    val arrayArticles = jsonObj.getJSONArray("articles")

                    val news = ArrayList<NewsData>()

                    for (i in 0 until arrayArticles.length()) {
                        val obj = arrayArticles.getJSONObject(i)

                        Log.d("SECOND_NEWS", obj.toString())

                        // urlToImage가 null 또는 빈 문자열이면 해당 뉴스를 건너뜁니다.
                        val imageUrl = obj.getString("urlToImage")
                        if (imageUrl.isNullOrBlank()) {
                            continue
                        }

                        val newsData = NewsData().apply {
                            title = obj.getString("title")
                            urlToImage = imageUrl
                        }

                        newsData.url = obj.getString("url")
                        news.add(newsData)
                    }

                    secondAdapter = MySecondAdapter(news, requireContext())
                    secondRecyclerView.adapter = secondAdapter

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("VOLLEY_ERROR", "Volley error: $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    private fun getNews() {
        val url = "https://newsapi.org/v2/everything?q=premierleague&language=en&apiKey=913a13e56be549f29ab1a4887d74b80c"

        val stringRequest = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                Log.d("NEWS", response)

                try {
                    val jsonObj = JSONObject(response)
                    val arrayArticles = jsonObj.getJSONArray("articles")

                    val news = ArrayList<NewsData>()

                    for (i in 0 until arrayArticles.length()) {
                        val obj = arrayArticles.getJSONObject(i)

                        Log.d("NEWS", obj.toString())

                        val newsData = NewsData().apply {
                            title = obj.getString("title")
                            urlToImage = obj.getString("urlToImage")
                        }

                        newsData.url = obj.getString("url")
                        news.add(newsData)
                    }

                    mAdapter = MyAdapter(news, requireContext())
                    mRecyclerView.adapter = mAdapter

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("VOLLEY_ERROR", "Volley error: $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                // 필요한 경우 다른 헤더도 추가 가능
                return headers
            }
        }

        queue.add(stringRequest)
    }
}
