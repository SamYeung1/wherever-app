package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.model.Announcement
import kotlinx.android.synthetic.main.fragment_announcement_detail.*


class AnnouncementDetailFragment : BaseFragment() {
    private lateinit var announcement: Announcement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            this.announcement = it.getParcelable(ANNOUNCEMENT)!!
            title = ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindVIew(savedInstanceState)
    }

    private fun bindVIew(savedInstanceState: Bundle?) {
        this.announcement.let {
            if (it.banner != null) {
                img_banner.visibility = View.VISIBLE
                ImageUtil.loadImage(
                    context!!,
                    img_banner,
                    it.banner,
                    ImageUtil.createCenterCropRequestOption().override(500)
                )
            } else {
                img_banner.visibility = View.GONE
            }
            tv_title.text = it.title
            tv_description.text = it.content
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        private const val ANNOUNCEMENT = "announcement"
        fun start(activity: Activity, announcement: Announcement) {
            val bundle = Bundle()
            bundle.putParcelable(ANNOUNCEMENT, announcement)
            PortraitBaseActivity.start(activity, AnnouncementDetailFragment(), bundle)
        }
    }

}
