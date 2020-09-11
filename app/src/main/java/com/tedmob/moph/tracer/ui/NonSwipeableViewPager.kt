package com.tedmob.moph.tracer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


open class NonSwipeableViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean = false
}