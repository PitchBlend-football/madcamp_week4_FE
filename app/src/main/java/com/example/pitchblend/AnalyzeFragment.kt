package com.example.pitchblend

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AnalyzeFragment : Fragment() {
    private lateinit var videoView: VideoView
    private lateinit var playPauseButton: ImageButton
    private var isPlaying = false
    private var lastClickTime: Long = 0

    private val hidePauseButtonDelay = 2000L // 2초

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analyze, container, false)

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
            it.setOnVideoSizeChangedListener { _, width, height ->
                updateVideoViewSize(width, height)
            }
            updateVideoViewSize(it.videoWidth, it.videoHeight)
            updatePlayPauseButtonVisibility()
        }

        return view
    }

    private fun togglePlayPauseState() {
        if (isPlaying) {
            videoView.pause()
        } else {
            videoView.start()
        }
        isPlaying = !isPlaying
    }

    private fun updateVideoViewSize(videoWidth: Int, videoHeight: Int) {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        // 화면 크기에 따라 비디오 화면 비율을 조정
        val adjustedWidth: Int
        val adjustedHeight: Int
        if (videoWidth / screenWidth.toDouble() > videoHeight / screenHeight.toDouble()) {
            // 화면 가로 비율이 비디오 가로 비율보다 큰 경우
            adjustedWidth = screenWidth
            adjustedHeight = (videoHeight / (videoWidth / screenWidth.toDouble())).toInt()
        } else {
            // 화면 세로 비율이 비디오 세로 비율보다 큰 경우
            adjustedWidth = (videoWidth / (videoHeight / screenHeight.toDouble())).toInt()
            adjustedHeight = screenHeight
        }

        val layoutParams = videoView.layoutParams
        layoutParams.width = adjustedWidth
        layoutParams.height = adjustedHeight
        videoView.layoutParams = layoutParams
    }

    private fun updatePlayPauseButtonVisibility() {
        playPauseButton.visibility = if (isPlaying) View.GONE else View.VISIBLE
    }
}



