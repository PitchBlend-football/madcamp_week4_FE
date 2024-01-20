package com.example.pitchblend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        mRecyclerView = view.findViewById(R.id.my_recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = mLayoutManager

        queue = Volley.newRequestQueue(requireContext())
        getNews()

        return view
    }

    private fun getNews() {
        val url = "https://newsapi.org/v2/top-headlines?country=gb&category=sports&apiKey=913a13e56be549f29ab1a4887d74b80c"

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
                            content = obj.getString("content")
                        }

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
