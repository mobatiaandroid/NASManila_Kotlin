package com.mobatia.nasmanila.recycler_view_manager

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class RecycleItemListener: RecyclerView.OnItemTouchListener {
    private val listener: RecyclerTouchListener? = null
    private var gestureDetector: GestureDetector? = null

    interface RecyclerTouchListener {
        fun onClickItem(v: View?, position: Int)
        fun onLongClickItem(v: View?, position: Int)
    }
    constructor(
        context: Context,
        recyclerView: RecyclerView,
        recyclerListener: RecyclerTouchListener
    ) {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
//                val v: View? = recyclerView.findChildViewUnder(e.x, e.y)
//                listener!!.onClickItem(v, recyclerView.getChildAdapterPosition(v!!))
                return true
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        return child != null && gestureDetector!!.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}