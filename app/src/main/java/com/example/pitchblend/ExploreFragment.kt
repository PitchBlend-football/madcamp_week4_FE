// ExploreFragment.kt

package com.example.pitchblend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class ExploreFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<*>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var queue: RequestQueue
    private lateinit var toggleLayout: LinearLayout

    private lateinit var secondRecyclerView: RecyclerView
    private lateinit var secondAdapter: RecyclerView.Adapter<*>
    private lateinit var secondLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        mRecyclerView = view.findViewById(R.id.my_recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        secondRecyclerView = view.findViewById(R.id.second_recycler_view)
        secondRecyclerView.setHasFixedSize(true)
        secondLayoutManager = LinearLayoutManager(requireContext())
        secondRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        queue = Volley.newRequestQueue(requireContext())
        getNews()
        getSecondNews()

        setupToggleAnimation(view)

        return view
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
                // Handle error
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
