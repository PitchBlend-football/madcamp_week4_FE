package com.example.pitchblend

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pitchblend.databinding.ActivityMomBinding
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Mom : AppCompatActivity() {

    private lateinit var accessToken: String
    private val api = RetrofitInterface.create()

    private var nowMom = 0 // nowMom == 1이면 첫번째 player을 투표한 것
    private var momIdList = ArrayList<Int>()
    private var momNameList = ArrayList<String>()

    private lateinit var binding: ActivityMomBinding

    private lateinit var momBackBtn: ImageButton

    private lateinit var mom1Name: TextView
    private lateinit var mom1LikeBtn: ImageButton
    private lateinit var mom1DislikeBtn: ImageButton
    private lateinit var mom1LikePercent:TextView
    private lateinit var mom1DislikePercent:TextView
    private lateinit var mom1LikePercentGraph: CardView
    private lateinit var mom1DislikePercentGraph: CardView
    private lateinit var mom1Comment: EditText
    private lateinit var mom1Logo: ImageView
    private lateinit var mom1CommentRv: RecyclerView
    private lateinit var mom1CommentAdapter: MomCommentAdapter
    private var mom1CommentList = ArrayList<MomCommentRequest>()

    private lateinit var mom2Name: TextView
    private lateinit var mom2LikeBtn: ImageButton
    private lateinit var mom2DislikeBtn: ImageButton
    private lateinit var mom2LikePercent:TextView
    private lateinit var mom2DislikePercent:TextView
    private lateinit var mom2LikePercentGraph: CardView
    private lateinit var mom2DislikePercentGraph: CardView
    private lateinit var mom2Comment: EditText
    private lateinit var mom2Logo: ImageView
    private lateinit var mom2CommentRv: RecyclerView
    private lateinit var mom2CommentAdapter: MomCommentAdapter
    private var mom2CommentList = ArrayList<MomCommentRequest>()

    private lateinit var mom3Name: TextView
    private lateinit var mom3LikeBtn: ImageButton
    private lateinit var mom3DislikeBtn: ImageButton
    private lateinit var mom3LikePercent:TextView
    private lateinit var mom3DislikePercent:TextView
    private lateinit var mom3LikePercentGraph: CardView
    private lateinit var mom3DislikePercentGraph: CardView
    private lateinit var mom3Comment: EditText
    private lateinit var mom3Logo: ImageView
    private lateinit var mom3CommentRv: RecyclerView
    private lateinit var mom3CommentAdapter: MomCommentAdapter
    private var mom3CommentList = ArrayList<MomCommentRequest>()

    private var is_like = true
    private var mom1Length = 0
    private var mom2Length = 0
    private var mom3Length = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref= getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        accessToken = sharedPref.getString("accessToken", "")!!
        Log.e("accessToken", "accessToken: $accessToken")

        initiation()

        GlobalScope.launch(Dispatchers.Main) {
            getTeamMom()
            // 결과를 사용하는 로직
            Log.e("Mom", "$momIdList")
            Log.e("Mom", "$momNameList")
            mom1Name.text = momNameList[0]
            mom2Name.text = momNameList[1]
            mom3Name.text = momNameList[2]

            GlobalScope.launch(Dispatchers.Main) {
                getMomVoteStat(1)
                val mom1LayoutParam = mom1LikePercentGraph.layoutParams
                val mom1LikePercentGraphLength = dpToPx300(mom1Length / 100f)
                mom1LayoutParam.width = mom1LikePercentGraphLength
                mom1LikePercentGraph.layoutParams = mom1LayoutParam
            }
            GlobalScope.launch(Dispatchers.Main) {
                getMomVoteStat(2)
                val mom2LayoutParam = mom2LikePercentGraph.layoutParams
                val mom2LikePercentGraphLength = dpToPx300(mom2Length / 100f)
                mom2LayoutParam.width = mom2LikePercentGraphLength
                mom2LikePercentGraph.layoutParams = mom2LayoutParam
            }
            GlobalScope.launch(Dispatchers.Main) {
                getMomVoteStat(3)
                val mom3LayoutParam = mom3LikePercentGraph.layoutParams
                val mom3LikePercentGraphLength = dpToPx300(mom3Length / 100f)
                mom3LayoutParam.width = mom3LikePercentGraphLength
                mom3LikePercentGraph.layoutParams = mom3LayoutParam
            }

            mom1LikeBtn.setOnClickListener {
                is_like = true
                postVote(1)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(1)
                    val mom1LayoutParam = mom1LikePercentGraph.layoutParams
                    val mom1LikePercentGraphLength = dpToPx300(mom1Length / 100f)
                    mom1LayoutParam.width = mom1LikePercentGraphLength
                    mom1LikePercentGraph.layoutParams = mom1LayoutParam
                }
            }
            mom1DislikeBtn.setOnClickListener {
                is_like = false
                postVote(1)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(1)
                    val mom1LayoutParam = mom1LikePercentGraph.layoutParams
                    val mom1LikePercentGraphLength = dpToPx300(mom1Length / 100f)
                    mom1LayoutParam.width = mom1LikePercentGraphLength
                    mom1LikePercentGraph.layoutParams = mom1LayoutParam
                }
            }

            mom2LikeBtn.setOnClickListener {
                is_like = true
                postVote(2)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(2)
                    val mom2LayoutParam = mom2LikePercentGraph.layoutParams
                    val mom2LikePercentGraphLength = dpToPx300(mom2Length / 100f)
                    mom2LayoutParam.width = mom2LikePercentGraphLength
                    mom2LikePercentGraph.layoutParams = mom2LayoutParam
                }
            }
            mom2DislikeBtn.setOnClickListener {
                is_like = false
                postVote(2)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(2)
                    val mom2LayoutParam = mom2LikePercentGraph.layoutParams
                    val mom2LikePercentGraphLength = dpToPx300(mom2Length / 100f)
                    mom2LayoutParam.width = mom2LikePercentGraphLength
                    mom2LikePercentGraph.layoutParams = mom2LayoutParam
                }
            }

            mom3LikeBtn.setOnClickListener {
                is_like = true
                postVote(3)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(3)
                    val mom3LayoutParam = mom3LikePercentGraph.layoutParams
                    val mom3LikePercentGraphLength = dpToPx300(mom3Length / 100f)
                    mom3LayoutParam.width = mom3LikePercentGraphLength
                    mom3LikePercentGraph.layoutParams = mom3LayoutParam
                }
            }
            mom3DislikeBtn.setOnClickListener {
                is_like = false
                postVote(3)
                GlobalScope.launch(Dispatchers.Main) {
                    getMomVoteStat(3)
                    val mom3LayoutParam = mom3LikePercentGraph.layoutParams
                    val mom3LikePercentGraphLength = dpToPx300(mom3Length / 100f)
                    mom3LayoutParam.width = mom3LikePercentGraphLength
                    mom3LikePercentGraph.layoutParams = mom3LayoutParam
                }
            }

        }

    }

    private fun initiation() {
        momBackBtn = binding.momBackBtn

        momBackBtn.setOnClickListener {
            finish()
        }

        mom1Name = binding.mom1Name
        mom1LikeBtn = binding.mom1LikeBtn
        mom1DislikeBtn = binding.mom1DislikeBtn
        mom1LikePercent = binding.mom1LikePercent
        mom1DislikePercent = binding.mom1DislikePercent
        mom1LikePercentGraph = binding.mom1LikePercentGraph
        mom1DislikePercentGraph = binding.mom1DislikePercentGraph
        mom1Comment = binding.mom1Comment
        mom1Logo = binding.mom1Logo
        mom1CommentRv = binding.mom1CommentRv
        mom1CommentAdapter = MomCommentAdapter(mom1CommentList)
        mom1CommentRv.layoutManager = LinearLayoutManager(this)
        mom1CommentRv.adapter = mom1CommentAdapter

        mom2Name = binding.mom2Name
        mom2LikeBtn = binding.mom2LikeBtn
        mom2DislikeBtn = binding.mom2DislikeBtn
        mom2LikePercent = binding.mom2LikePercent
        mom2DislikePercent = binding.mom2DislikePercent
        mom2LikePercentGraph = binding.mom2LikePercentGraph
        mom2DislikePercentGraph = binding.mom2DislikePercentGraph
        mom2Comment = binding.mom2Comment
        mom2Logo = binding.mom2Logo
        mom2CommentRv = binding.mom2CommentRv
        mom2CommentAdapter = MomCommentAdapter(mom2CommentList)
        mom2CommentRv.layoutManager = LinearLayoutManager(this)
        mom2CommentRv.adapter = mom2CommentAdapter

        mom3Name = binding.mom3Name
        mom3LikeBtn = binding.mom3LikeBtn
        mom3DislikeBtn = binding.mom3DislikeBtn
        mom3LikePercent = binding.mom3LikePercent
        mom3DislikePercent = binding.mom3DislikePercent
        mom3LikePercentGraph = binding.mom3LikePercentGraph
        mom3DislikePercentGraph = binding.mom3DislikePercentGraph
        mom3Comment = binding.mom3Comment
        mom3Logo = binding.mom3Logo
        mom3CommentRv = binding.mom3CommentRv
        mom3CommentAdapter = MomCommentAdapter(mom3CommentList)
        mom3CommentRv.layoutManager = LinearLayoutManager(this)
        mom3CommentRv.adapter = mom3CommentAdapter
    }



    private suspend fun getTeamMom(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getTeamMom("Bearer $accessToken")
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                Log.e("Mom", "$result")
                for (mom in result) {
                    momIdList.add(mom.get("id").asInt)
                    momNameList.add(mom.get("name").asString)
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

    private fun postVote(mom: Int) {
        //mom == 1이면 첫번째, 2면 2번째, 3이면 3번째

        //postVote(momIdList[mom-1], true, "Really Good")

        var comment = ""

        when (mom) {
            1 -> { comment = mom1Comment.text.toString() }
            2 -> { comment = mom2Comment.text.toString() }
            3 -> { comment = mom3Comment.text.toString() }
        }
//            PostVoteRequest(
//                momIdList[mom-1],
//                is_like,
//                comment
//            )

        val call = api.postVote(
            "Bearer $accessToken",
//            momIdList[mom-1],
//            is_like,
//            comment
            JsonObject().apply {
                addProperty("player", momIdList[mom-1])
                addProperty("is_like", is_like)
                addProperty("comment", comment)
            }
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e("Lets go", "$result")
                    mom1Comment.text.clear()
                    mom2Comment.text.clear()
                    mom3Comment.text.clear()
                    Log.e("Lets go", "success!! good!!")
                } else {
                    Log.e("Lets go", "what's wrong...")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("mad..nn", "so sad plz")
            }
        })
    }

    private suspend fun getMomVoteStat(mom: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        try {
            val result = withContext(dispatcher) {
                val call = api.getMomVoteStat("Bearer $accessToken", momIdList[mom-1])
                val response = call.execute()
                response.body()
            }
            if (result != null) {
                Log.e("Mom", "vote stat: $result")
                when (mom) {
                    1 -> {
                        mom1LikePercent.text = "${result.votePercentage.likes.toFloat().toInt()}%"
                        mom1DislikePercent.text = "${result.votePercentage.dislikes.toInt()}%"
                        mom1Length = result.votePercentage.likes.toInt()

                        mom1CommentAdapter.updateData(result.comments)
                        Log.e("standingList", "${mom1CommentAdapter.itemCount}")
                    }
                    2 -> {
                        mom2LikePercent.text = "${result.votePercentage.likes.toInt()}%"
                        mom2DislikePercent.text = "${result.votePercentage.dislikes.toInt()}%"
                        mom2Length = result.votePercentage.likes.toInt()

                        mom2CommentAdapter.updateData(result.comments)
                        Log.e("standingList", "${mom1CommentAdapter.itemCount}")
                    }
                    3 -> {
                        mom3LikePercent.text = "${result.votePercentage.likes.toInt()}%"
                        mom3DislikePercent.text = "${result.votePercentage.dislikes.toInt()}%"
                        mom3Length = result.votePercentage.likes.toInt()

                        mom3CommentAdapter.updateData(result.comments)
                        Log.e("standingList", "${mom1CommentAdapter.itemCount}")
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


    private fun dpToPx300(float: Float): Int {
        val desiredHeightInDp = 300 * float
        val pixels = (desiredHeightInDp * resources.displayMetrics.density).toInt()
        return pixels
    }

}