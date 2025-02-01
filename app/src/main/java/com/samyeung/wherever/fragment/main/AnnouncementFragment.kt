package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.Home
import com.samyeung.wherever.view.adapter.AnnouncementListAdapter
import kotlinx.android.synthetic.main.fragment_announcement.*

/**
 * A simple [Fragment] subclass.
 *
 */
class AnnouncementFragment : BaseFragment() {
    private lateinit var announcementListAdapter: AnnouncementListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.setUpService()
        title = context!!.resources.getString(R.string.news)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindVIew(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            Home.HomeLiveData.get().observe(this, Observer {
                it?.announcements?.let { announcements ->
                    this@AnnouncementFragment.announcementListAdapter.addAll(announcements)
                }
            })
        }
    }

    private fun setUpService() {

    }

    private fun bindVIew(savedInstanceState: Bundle?) {
        this.announcementListAdapter =
            AnnouncementListAdapter(context!!, list_announcement)
        this.announcementListAdapter.onItemClick = {
            AnnouncementDetailFragment.start(activity!!,it)
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
        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, AnnouncementFragment())
        }
    }
}
