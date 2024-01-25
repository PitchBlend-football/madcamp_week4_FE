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
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.VideoView
import androidx.fragment.app.Fragment

class AnalyzeFragment : Fragment() {

    private lateinit var view: View
    private val hidePauseButtonDelay = 2000L // 2초

    private lateinit var videoView1: VideoView
    private lateinit var playPauseButton1: ImageButton
    private lateinit var sizeBigButton1: ImageButton
    private lateinit var thumbnail1: ImageView
    private var isPlaying1 = false
    private var lastClickTime1: Long = 0
    private var isFirst1 = true

    private lateinit var videoView2: VideoView
    private lateinit var playPauseButton2: ImageButton
    private lateinit var sizeBigButton2: ImageButton
    private lateinit var thumbnail2: ImageView
    private var isPlaying2 = false
    private var lastClickTime2: Long = 0
    private var isFirst2 = true

    private lateinit var videoView3: VideoView
    private lateinit var playPauseButton3: ImageButton
    private lateinit var sizeBigButton3: ImageButton
    private lateinit var thumbnail3: ImageView
    private var isPlaying3 = false
    private var lastClickTime3: Long = 0
    private var isFirst3 = true

    private var videoPath = ""

    private var videoViewList = ArrayList<VideoView>()
    private var videoPathList = ArrayList<String>()
    private var isPlayingList = ArrayList(listOf(false, false))
    private var isFirstList = ArrayList(listOf(true, true))


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_analyze, container, false)

        videoPath = "android.resource://${requireActivity().packageName}/"

        initiation()
        fullSize()


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FULLSCREEN && resultCode == Activity.RESULT_OK) {
            // 전체 화면에서 돌아왔을 때 전달받은 현재 재생 위치를 사용하여 재생
            val currentVideo = data?.getIntExtra("num", 0)
            if (currentVideo == 1) {
                val currentPosition = data?.getIntExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, 0) ?: 0
                videoView1.seekTo(currentPosition)
                isPlaying1 = data?.getBooleanExtra("isPlaying", false) ?: false
                if (isPlaying1) {
                    playPauseButton1.visibility = View.GONE
                    videoView1.start() // 비디오를 다시 시작
                }
            } else if (currentVideo == 2) {
                val currentPosition = data?.getIntExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, 0) ?: 0
                videoView2.seekTo(currentPosition)
                isPlaying2 = data?.getBooleanExtra("isPlaying", false) ?: false
                if (isPlaying2) {
                    playPauseButton2.visibility = View.GONE
                    videoView2.start() // 비디오를 다시 시작
                }
            } else if (currentVideo == 3) {
                val currentPosition = data?.getIntExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, 0) ?: 0
                videoView3.seekTo(currentPosition)
                isPlaying3 = data?.getBooleanExtra("isPlaying", false) ?: false
                if (isPlaying3) {
                    playPauseButton3.visibility = View.GONE
                    videoView3.start() // 비디오를 다시 시작
                }
            }
        }
    }

    private fun initiation() {

        // video1
        videoView1 = view.findViewById(R.id.videoView1)
        playPauseButton1 = view.findViewById(R.id.playPauseButton1)
        sizeBigButton1 = view.findViewById(R.id.fullscreen_button1)
        thumbnail1 = view.findViewById(R.id.che_man_thumbnail)

        //videoViewList.add(videoView1)

        videoView1.setOnCompletionListener {
            isFirst1 = true
            isPlaying1 = false
            thumbnail1.visibility = View.VISIBLE
            playPauseButton1.visibility = View.VISIBLE
        }

        videoView1.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime1 >= hidePauseButtonDelay) {
                togglePlayPauseState(1)
                updatePlayPauseButtonVisibility(1)
                lastClickTime1 = currentTime
            }
        }

        playPauseButton1.setOnClickListener {
            togglePlayPauseState(1)
            updatePlayPauseButtonVisibility(1)
        }

        videoView1.setVideoPath(videoPath+"${R.raw.facup_chelsea_mancity_out}")
        videoView1.setOnPreparedListener {
            updatePlayPauseButtonVisibility(1)
        }


        // video2
        videoView2 = view.findViewById(R.id.videoView2)
        playPauseButton2 = view.findViewById(R.id.playPauseButton2)
        sizeBigButton2 = view.findViewById(R.id.fullscreen_button2)
        thumbnail2 = view.findViewById(R.id.thumbnail2)

        //videoViewList.add(videoView2)

        videoView2.setOnCompletionListener {
            isFirst2 = true
            isPlaying2 = false
            thumbnail2.visibility = View.VISIBLE
            playPauseButton2.visibility = View.VISIBLE
        }

        videoView2.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime2 >= hidePauseButtonDelay) {
                togglePlayPauseState(2)
                updatePlayPauseButtonVisibility(2)
                lastClickTime2 = currentTime
            }
        }

        playPauseButton2.setOnClickListener {
            togglePlayPauseState(2)
            updatePlayPauseButtonVisibility(2)
        }

        videoView2.setVideoPath(videoPath+"${R.raw.che_liv_1_out}")
        videoView2.setOnPreparedListener {
            updatePlayPauseButtonVisibility(2)
        }



        // video3 고쳐~!~
        videoView3 = view.findViewById(R.id.videoView3)
        playPauseButton3 = view.findViewById(R.id.playPauseButton3)
        sizeBigButton3 = view.findViewById(R.id.fullscreen_button3)
        thumbnail3 = view.findViewById(R.id.thumbnail3)

        //videoViewList.add(videoView1)

        videoView3.setOnCompletionListener {
            isFirst3 = true
            isPlaying3 = false
            thumbnail3.visibility = View.VISIBLE
            playPauseButton3.visibility = View.VISIBLE
        }

        videoView3.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime3 >= hidePauseButtonDelay) {
                togglePlayPauseState(3)
                updatePlayPauseButtonVisibility(3)
                lastClickTime3 = currentTime
            }
        }

        playPauseButton3.setOnClickListener {
            togglePlayPauseState(3)
            updatePlayPauseButtonVisibility(3)
        }

        videoView3.setVideoPath(videoPath+"${R.raw.mci_ars_3}")
        videoView3.setOnPreparedListener {
            updatePlayPauseButtonVisibility(3)
        }



    }

    private fun fullSize() {
        sizeBigButton1.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra("num", 1)
            intent.putExtra(FullScreenActivity.EXTRA_VIDEO_PATH, videoPath+"${R.raw.facup_chelsea_mancity_out}")
            // 현재 재생 위치를 전달
            intent.putExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, videoView1.currentPosition)
            intent.putExtra("isPlaying", isPlaying1)
            startActivityForResult(intent, REQUEST_FULLSCREEN)
        }

        sizeBigButton2.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra("num", 2)
            intent.putExtra(FullScreenActivity.EXTRA_VIDEO_PATH, videoPath+"${R.raw.che_liv_1_out}")
            // 현재 재생 위치를 전달
            intent.putExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, videoView2.currentPosition)
            intent.putExtra("isPlaying", isPlaying2)
            startActivityForResult(intent, REQUEST_FULLSCREEN)
        }

        sizeBigButton3.setOnClickListener {
            val intent = Intent(requireContext(), FullScreenActivity::class.java)
            intent.putExtra("num", 3)
            intent.putExtra(FullScreenActivity.EXTRA_VIDEO_PATH, videoPath+"${R.raw.mci_ars_3}")
            // 현재 재생 위치를 전달
            intent.putExtra(FullScreenActivity.EXTRA_CURRENT_POSITION, videoView3.currentPosition)
            intent.putExtra("isPlaying", isPlaying3)
            startActivityForResult(intent, REQUEST_FULLSCREEN)
        }
    }

    private fun togglePlayPauseState(index: Int) {
        if (index == 1) {
            if (isPlaying1) {
                videoView1.pause()
            } else {
                if (isFirst1) {
                    thumbnail1.visibility = View.GONE
                    isFirst1 = false
                }
                videoView1.start()
            }
            isPlaying1 = !isPlaying1
        } else if (index == 2) {
            if (isPlaying2) {
                videoView2.pause()
            } else {
                if (isFirst2) {
                    thumbnail2.visibility = View.GONE
                    isFirst2 = false
                }
                videoView2.start()
            }
            isPlaying2 = !isPlaying2
        } else if (index == 3) {
            if (isPlaying3) {
                videoView3.pause()
            } else {
                if (isFirst3) {
                    thumbnail3.visibility = View.GONE
                    isFirst3 = false
                }
                videoView3.start()
            }
            isPlaying3 = !isPlaying3
        }
    }

    private fun updatePlayPauseButtonVisibility(index: Int) {
        if (index == 1) {
            playPauseButton1.visibility = if (isPlaying1) View.GONE else View.VISIBLE
        } else if (index == 2) {
            playPauseButton2.visibility = if (isPlaying2) View.GONE else View.VISIBLE
        } else if (index == 3) {
            playPauseButton3.visibility = if (isPlaying3) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val REQUEST_FULLSCREEN = 123
    }
}

