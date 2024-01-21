package com.example.pitchblend

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollListener(
    private val layoutManager: LinearLayoutManager
): RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        setFocus(layoutManager)
    }

    private fun setFocus(manager: LinearLayoutManager) {
        manager.let {
            val firstPos = it.findFirstVisibleItemPosition()
            val lastPos = it.findLastVisibleItemPosition()
            val completePos = it.findFirstCompletelyVisibleItemPosition()

            Log.d("ScrollListener", "first: $firstPos")
            Log.d("ScrollListener", "last: $lastPos")
            Log.d("ScrollListener", "complete: $completePos")

            val firstView = it.findViewByPosition(firstPos)
            val lastView = it.findViewByPosition(lastPos)
            val completeView = it.findViewByPosition(completePos)

            if (completePos != -1) {
                zoomOutView(firstView)
                zoomOutView(lastView)
                zoomInView(completeView)
            }
        }
    }

    private fun zoomInView(inView: View?) {
        inView?.run {
            val anim = AnimationUtils.loadAnimation(context, R.anim.anim_focus_in)
            startAnimation(anim)
        }
    }

    private fun zoomOutView(outView: View?) {
        outView?.run {
            val anim = AnimationUtils.loadAnimation(context, R.anim.anim_focus_out)
            startAnimation(anim)
        }
    }
}