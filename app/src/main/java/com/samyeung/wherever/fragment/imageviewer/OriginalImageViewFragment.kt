package com.samyeung.wherever.fragment.imageviewer


import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.samyeung.wherever.R
import com.samyeung.wherever.activity.BaseActivity
import com.samyeung.wherever.util.helper.ImageUtil
import kotlinx.android.synthetic.main.fragment_image_view.*



class OriginalImageViewFragment : ImageViewFragment() {
    private var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            this.url = arguments?.getString(URL)
        } else {
            this.url = savedInstanceState.getString(URL)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu!!.findItem(R.id.action_favourite).isVisible = false
        menu.findItem(R.id.action_report).isVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImageUtil.loadImage(
            context!!, view.findViewById(R.id.iv_image), this.url, RequestOptions().override(1000).diskCacheStrategy(
                DiskCacheStrategy.DATA
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(URL, this.url)

    }

    override fun handleDetailPage(view: CardView, savedInstanceState: Bundle?) {
        view.visibility = View.GONE
        fab_love.hide()
    }

    companion object {
        private const val URL = "URL"
        fun start(activity: Activity, url: String) {
            val bundle = Bundle()
            bundle.putString(URL, url)
            BaseActivity.start(activity, OriginalImageViewFragment(), bundle)
        }
    }
}
