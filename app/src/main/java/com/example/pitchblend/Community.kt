package com.example.pitchblend

// Community.kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.squareup.picasso.Picasso

class Community : AppCompatActivity() {

    private val channelId = "AlarmChannel"
    private val notificationId1 = 1
    private val notificationId2 = 2
    private val notificationId3 = 3

    private var intentTeamLogo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community)

        // intent해서 가지고 온 team 이름, 로고, 위치 설정
        Picasso.get().load(intent.getStringExtra("teamLogo")).into(findViewById<ImageView>(R.id.logo))
        intentTeamLogo = intent.getStringExtra("teamLogo")!!
        findViewById<TextView>(R.id.teamName).text = intent.getStringExtra("teamName")
        findViewById<TextView>(R.id.teamLocation).text = intent.getStringExtra("teamStadium")

        // back_btn 클릭 시 MyProfileFragment로 이동
        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            // MyProfileFragment로 이동하는 Intent 생성
            val intent = Intent(this, MyProfileFragment::class.java)
            // 기존의 Community 액티비티 종료
            finish()
        }

        // momButton 클릭 시 Mom.kt로 전환
        val momButton = findViewById<ImageButton>(R.id.momButton)
        momButton.setOnClickListener {
            // Mom.kt로 이동하는 Intent 생성
            val intent = Intent(this, Mom::class.java)
            intent.putExtra("teamLogo", intentTeamLogo)
            startActivity(intent)
        }

    }



    // TextView와 ImageView를 업데이트하는 함수
    private fun updateView(isChecked: Boolean, textViewId: Int, imageViewId: Int) {
        val textView = findViewById<TextView>(textViewId)
        val alarmSunImageView = findViewById<ImageView>(imageViewId)

        // 색상 및 이미지 업데이트
        textView.setTextColor(if (isChecked) Color.BLACK else Color.WHITE)
        alarmSunImageView.setImageResource(if (isChecked) R.drawable.alarmsun else R.drawable.alarmnight)
    }

    // 토글 버튼 클릭 이벤트 핸들러
    fun onToggleClicked(view: View) {
        val isChecked = (view as ToggleButton).isChecked

        // 각 토글 버튼에 대한 notificationId를 지정하여 알림 표시
        when (view.id) {
            R.id.toggleButton -> {
                showNotification(notificationId1, isChecked)
                updateView(isChecked, R.id.textView, R.id.alarmsun)
            }
            R.id.toggleButton2 -> {
                showNotification(notificationId2, isChecked)
                updateView(isChecked, R.id.textView2, R.id.alarmsun2)
            }
            R.id.toggleButton3 -> {
                showNotification(notificationId3, isChecked)
                updateView(isChecked, R.id.textView3, R.id.alarmsun3)
            }
        }
    }

    // 알림 표시 함수
    private fun showNotification(notificationId: Int, isChecked: Boolean) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel 생성 (Android 8.0 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel for Alarm Notifications"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            notificationManager.createNotificationChannel(channel)
        }

        // Notification 빌더 설정
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.premierleague_logo_2)  // 알림창 아이콘 (앱 로고)
            .setContentTitle("알람 예약")
            .setContentText("알람이 예약되었습니다")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)  // 사용자가 탭하면 자동으로 알림 삭제

        // 알림 터치 시 실행될 액티비티 설정
        val resultIntent = Intent(this, Community::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(resultPendingIntent)

        // 알림 표시
        notificationManager.notify(notificationId, builder.build())
    }
}
