package com.example.pitchblend

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pitchblend.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val api = RetrofitInterface.create()

    private lateinit var liveScoreRv: RecyclerView
    private var liveScoreList = ArrayList<Schedule>()
    private lateinit var liveScoreAdapter: LiveScoreAdapter

    private lateinit var standingsRv: RecyclerView
    private var standingsList = ArrayList<Team>()
    private lateinit var standingsAdapter: StandingsAdapter

    private lateinit var matchScheduleRv: RecyclerView
    private var matchScheduleList = ArrayList<Schedule>()
    private lateinit var matchScheduleAdapter: MatchScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        //swipeRefreshLayout = binding.trashSwipeLayout
        //swipeRefreshLayout.setOnRefreshListener(this)

        initiation()

        return binding.root
    }

    private fun initiation() {
        // liveScore data들 데리고 오기
        liveScoreRv = binding.livescoreRecyclerview
        liveScoreAdapter = LiveScoreAdapter(liveScoreList)
        liveScoreRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        liveScoreRv.adapter = liveScoreAdapter

        api.getLastMatches().enqueue(object: Callback<MatchScheduleResponse> {
            override fun onResponse(
                call: Call<MatchScheduleResponse>,
                response: Response<MatchScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e(ContentValues.TAG, "result: $result")
                    result?.let {
                        liveScoreAdapter.updateData(result.response)
                        //standingsAdapter.notifyDataSetChanged()
                        Log.e("liveScoreList", "${liveScoreAdapter.itemCount}")
                    }
                    Log.e("liveScoreList", "$liveScoreList")
                    //Log.e("standingList", "${standingsAdapter.itemCount}")
                    //swipeRefreshLayout.isRefreshing = false
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MatchScheduleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                //swipeRefreshLayout.isRefreshing = false
            }
        })


        // match schedule data들 데리고 오기
        matchScheduleRv = binding.matchScheduleRecyclerview
        matchScheduleAdapter = MatchScheduleAdapter(matchScheduleList)
        matchScheduleRv.layoutManager = LinearLayoutManager(requireContext())
        matchScheduleRv.adapter = matchScheduleAdapter

        api.getMatchSchedule().enqueue(object: Callback<MatchScheduleResponse> {
            override fun onResponse(
                call: Call<MatchScheduleResponse>,
                response: Response<MatchScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e(ContentValues.TAG, "result: $result")
                    result?.let {
                        matchScheduleAdapter.updateData(result.response)
                        //standingsAdapter.notifyDataSetChanged()
                        Log.e("matchScheduleList", "${matchScheduleAdapter.itemCount}")
                    }
                    Log.e("matchScheduleList", "$matchScheduleList")
                    //Log.e("standingList", "${standingsAdapter.itemCount}")
                    //swipeRefreshLayout.isRefreshing = false
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MatchScheduleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                //swipeRefreshLayout.isRefreshing = false
            }
        })

        binding.livescoreRecyclerview.apply {
            val snap = PagerSnapHelper()
            snap.attachToRecyclerView(this)

            val scrollListener = ScrollListener(layoutManager as LinearLayoutManager)
            addOnScrollListener(scrollListener)
        }


        // standing data들 데리고 오기
        standingsRv = binding.standingsRecyclerview
        standingsAdapter = StandingsAdapter(standingsList)
        standingsRv.layoutManager = LinearLayoutManager(requireContext())
        standingsRv.adapter = standingsAdapter

        api.getStandings().enqueue(object: Callback<StandingsResponse> {
            override fun onResponse(
                call: Call<StandingsResponse>,
                response: Response<StandingsResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e(ContentValues.TAG, "result: $result")
                    result?.let {
                        standingsAdapter.updateData(result.response[0].league.standings[0])
                        //standingsAdapter.notifyDataSetChanged()
                        Log.e("standingList", "${standingsAdapter.itemCount}")
                    }
                    Log.e("standingList", "$standingsList")
                    //Log.e("standingList", "${standingsAdapter.itemCount}")
                    //swipeRefreshLayout.isRefreshing = false
                } else {
                    // HTTP 요청이 실패한 경우의 처리
                    Log.e(ContentValues.TAG, "HTTP 요청 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<StandingsResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "네트워크 오류: ${t.message}")
                //swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    companion object {}
}