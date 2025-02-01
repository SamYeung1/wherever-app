package com.samyeung.wherever.fragment.imageviewer


import android.graphics.Color
import android.os.*
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.CardView
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import kotlinx.android.synthetic.main.bottom_sheet_image_viewer.*
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.tool_bar.*

const val AUTO_HIDE = false
const val AUTO_HIDE_DELAY_MILLIS = 3000
const val UI_ANIMATION_DELAY = 300


abstract class ImageViewFragment : BaseFragment() {
    //Full Screen
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        fullscreen_content_controls.visibility = View.VISIBLE

    }
    private var mVisible: Boolean = false
    private var mToolbarVisible: Boolean = true //For only toolbar
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        fullscreen_content_controls?.visibility = View.INVISIBLE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setUpToolbar(true, isDark = true)
        setUpNavigationBar(Color.BLACK)
        toolbar.visibility = if (mToolbarVisible) View.VISIBLE else View.INVISIBLE
        if (savedInstanceState == null) {
            delayedHide(1000)
        }
        this.bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        iv_image.setOnClickListener { toggle() }
        layout_bottom_content?.let {
            val layoutParam = it.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParam.behavior as BottomSheetBehavior
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {

                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_EXPANDED) {
                        toolbar.visibility = View.INVISIBLE
                        this@ImageViewFragment.mToolbarVisible = false
                    } else {
                        toolbar.visibility = View.VISIBLE
                        this@ImageViewFragment.mToolbarVisible = true
                    }
                }

            })
            handleDetailPage(it, savedInstanceState)
        }


    }

    protected fun detailPageLoading() {
        loading_indicator.visibility = View.VISIBLE
        layout_content.visibility = View.INVISIBLE

    }

    protected fun stopDetailPageLoading() {
        loading_indicator.visibility = View.INVISIBLE
        layout_content.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_image_viewer, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.finish()
                true
            }
            else -> {
                false
            }
        }
    }

    abstract fun handleDetailPage(view: CardView, savedInstanceState: Bundle?)

}
