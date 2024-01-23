package com.example.pitchblend

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RetrofitInterface {


    @GET("info/get_fixtures/")
    fun getOneDayMatchInfo(@Query("date") date: String, @Query("season") season: String): Call<FixtureResponse>

    @GET("info/get_predictions/{matchId}/")
    fun getMatchPredictions(@Path("matchId") matchId: Int): Call<JsonObject>


    @GET("info/get_standings/")
    fun getStandings(): Call<StandingsResponse>

    @GET("info/get_match/")
    fun getMatchSchedule(): Call<MatchScheduleResponse>

    @GET("info/get_allmatches/")
    fun getLastMatches(): Call<MatchScheduleResponse>

    @GET("info/get_events/{matchId}/")
    fun getScorers(@Path("matchId") matchId: Int): Call<ScorersResponse>

    @GET("info/fixture_statistics/{matchId}/")
    fun getMatchResult(@Path("matchId") matchId: Int): Call<MatchResultResponse>

    @GET("news/user_team_info/")
    fun getTeamInfo(@Header("authorization") authorization: String): Call<TeamInfo>

    @GET("info/get_players/")
    fun getTopScorer(@Query("team") team: Int): Call<TopScorersResponse>

    @FormUrlEncoded
    @POST("accounts/register/")
    fun postSignUp(
        @Field("username") username: String,
        @Field("phone_number") phonenumber: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("nickname") nickname: String,
        @Field("selected_team") selectedteam: Int
    ) : Call<JsonObject>





//    예시들
//
//    @GET("/user/{userId}/check")
//    fun getUserCheck(@Path("userId") userId: String): Call<JsonObject>
//
//    @FormUrlEncoded
//    @POST("/{userId}/clothes/add")
//    fun postAddClothes(
//        @Path("userId") userId: String,
//        @Field("category") category: String,
//        @Field("styles") styles: List<String>,
//        @Field("like") like: String,
//        @Field("trash") trash: String,
//        @Field("wish") wish: String,
//        @Field("imageUrl") imageUrl: String,
//        @Field("link") link: String?,
//    ): Call<JsonObject>
//
//    @Multipart
//    @POST("/images/upload")
//    fun uploadImage(@Part image: MultipartBody.Part): Call<JsonObject>
//
//    @PATCH("/{userId}/clothes/{clothesId}/changeLike")
//    fun changeLike(@Path("userId") userId: String, @Path("clothesId") clothesId: String): Call<JsonObject>
//
//    @DELETE("/{userId}/clothes/{clothesId}/remove")
//    fun deleteCloth(@Path("userId") userId: String, @Path("clothesId") clothesId: String): Call<JsonObject>
//
//    @GET("/{userId}/openai/generateOOTD")
//    fun dalle(@Path("userId") userId: String, @Query("stylePick") stylePick: String): Call<JsonObject>



    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.

        private const val BASE_URL = "http://ec2-43-202-210-226.ap-northeast-2.compute.amazonaws.com/"


        fun create(): RetrofitInterface {
            val gson : Gson =   GsonBuilder().setLenient().create();

            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS) // 연결 timeout
                .readTimeout(60, TimeUnit.SECONDS)    // 읽기 timeout
                .writeTimeout(60, TimeUnit.SECONDS)   // 쓰기 timeout
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitInterface::class.java)
        }
    }
}