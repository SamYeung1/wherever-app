package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class NoScrollViewPager : ViewPager {
    var isPagingEnabled = false
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event)
    }
    class FadeTransition: ViewPager.PageTransformer{
        override fun transformPage(page: View, position: Float) {
            page.translationX = page.width * -position
            if(position <= -1.0f || position >=1.0f){
                page.alpha = 0.0f
                page.visibility = View.GONE
            }else if(position == 0.0f){
                page.alpha = 1.0f
                page.visibility = View.VISIBLE
            } else{
                page.alpha = 1.0F - Math.abs(position)
                page.visibility = View.VISIBLE
            }
        }

    }
}