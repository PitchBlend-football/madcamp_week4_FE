package com.example.pitchblend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.pitchblend.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var liveScoreAdapter: LiveScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.beforeBtn.setOnClickListener {
            val intent = Intent(requireContext(), DetailMatchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        binding.afterBtn.setOnClickListener {
            val intent = Intent(requireContext(), AfterMatchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        initRecyclerView() // LiveScore RecyclerView 예쁘게

        return binding.root
    }

    private fun initRecyclerView() {
        binding.livescoreRecyclerview.apply {
            liveScoreAdapter = LiveScoreAdapter(arrayListOf("호랑이", "사자", "곰", "고양이", "개", "기린")) {
                Toast.makeText(this@HomeFragment.requireContext(), it, Toast.LENGTH_SHORT).show()
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            adapter = liveScoreAdapter

            val snap = PagerSnapHelper()
            snap.attachToRecyclerView(this)

            val scrollListener = ScrollListener(layoutManager as LinearLayoutManager)
            addOnScrollListener(scrollListener)
        }
    }

    companion object {}
}