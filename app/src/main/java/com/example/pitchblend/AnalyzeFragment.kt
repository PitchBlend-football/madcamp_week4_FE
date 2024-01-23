package com.example.pitchblend

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.VideoView
import androidx.fragment.app.Fragment

class AnalyzeFragment : Fragment() {
    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private var isPlaying = false
    private var lastClickTime: Long = 0
    private lateinit var sizeBigButton: ImageButton
    private lateinit var view: View

    private val hidePauseButtonDelay = 2000L // 2초

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_analyze, container, false)

        videoView = view.findViewById(R.id.videoView)
        playPauseButton = view.findViewById(R.id.playPauseButton)

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

        val videoPath = "android.resource://${requireActivity().packageName}/${R.raw.playchelman}"
        videoView.setVideoPath(videoPath)
        videoView.setOnPreparedListener {
            updatePlayPauseButtonVisibility()
        }

        sizeBigButton = view.findViewById(R.id.fullscreen_button)
        sizeBigButton.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra(FullScreenActivity.EXTRA_VIDEO_PATH, videoPath)
            // 현재 재생 위치를 전달
            intent.putExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, videoView.currentPosition)
            intent.putExtra("isPlaying", isPlaying)
            startActivityForResult(intent, REQUEST_FULLSCREEN)

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FULLSCREEN && resultCode == Activity.RESULT_OK) {
            // 전체 화면에서 돌아왔을 때 전달받은 현재 재생 위치를 사용하여 재생
            val currentPosition = data?.getIntExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, 0) ?: 0
            videoView.seekTo(currentPosition)
            isPlaying = data?.getBooleanExtra("isPlaying", false) ?: false
            if (isPlaying) {
                playPauseButton.visibility = View.GONE
                videoView.start() // 비디오를 다시 시작
            }
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
        const val REQUEST_FULLSCREEN = 123
    }
}

