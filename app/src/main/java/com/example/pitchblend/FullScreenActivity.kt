package com.example.pitchblend

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.VideoView

class FullScreenActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private var savedCurrentPosition: Int = 0
    private lateinit var playPauseButton: ImageButton
    private var isPlaying = false
    private var lastClickTime: Long = 0

    private val hidePauseButtonDelay = 2000L // 2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)



        // 화면 회전 및 네비게이션 바 숨기기 설정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                )

        // VideoView 초기화
        videoView = findViewById(R.id.fullScreenVideoView)
        playPauseButton = findViewById(R.id.playPauseButton)

        // 이전 Activity에서 전달된 비디오 경로와 현재 재생 위치를 가져옴
        val videoPath = intent.getStringExtra(EXTRA_VIDEO_PATH)
        savedCurrentPosition = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)
        isPlaying = intent.getBooleanExtra("isPlaying", false)

        videoView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= hidePauseButtonDelay) {
                togglePlayPauseState()
                updatePlayPauseButtonVisibility()
                lastClickTime = currentTime
            }
        }

        playPauseButton.setOnClickListener {
            togglePlayPauseState()
            updatePlayPauseButtonVisibility()
        }

        // 비디오 재생
        videoView.setVideoPath(videoPath)

        // 현재 재생 위치로 이동
        videoView.seekTo(savedCurrentPosition)

        if (isPlaying) {
            playPauseButton.visibility = View.GONE
            videoView.start()
        }

        findViewById<ImageButton>(R.id.return_screen).setOnClickListener {
            // 비디오의 현재 재생 위치를 Intent에 추가하여 전송
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_CURRENT_POSITION, videoView.currentPosition)
            resultIntent.putExtra("isPlaying", isPlaying)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
    private fun togglePlayPauseState() {
        if (isPlaying) {
            videoView.pause()
        } else {
            videoView.start()
        }
        isPlaying = !isPlaying
    }

    private fun updatePlayPauseButtonVisibility() {
        playPauseButton.visibility = if (isPlaying) View.GONE else View.VISIBLE
    }

    companion object {
        const val EXTRA_VIDEO_PATH = "extra_video_path"
        const val EXTRA_CURRENT_POSITION = "extra_current_position"
    }
}