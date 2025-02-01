package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import android.support.v7.widget.SearchView
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.api.UserServiceAdapter
import com.samyeung.wherever.model.UserProfile
import com.samyeung.wherever.view.adapter.FriendListAdapter
import kotlinx.android.synthetic.main.fragment_search_friend.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*
import android.os.Handler
import android.util.Log
import com.samyeung.wherever.util.base.TypingHandler


class SearchFriendFragment : BaseFragment() {
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var userService: UserServiceAdapter
    private lateinit var searchView: SearchView
    private var typingHandler = TypingHandler {h,value ->
        this.handleSearch(value)
        h.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.setUpService()
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindView(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_search_friend_menu, menu)
        searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.isIconified = false
        searchView.setOnCloseListener {
            activity!!.finish()
            true
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                typingHandler.start(p0!!)
                return true
            }
        })
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

    private fun bindView(savedInstanceState: Bundle?) {
        this@SearchFriendFragment.userService.findUserProfiles("", 1)
        this.friendListAdapter =
            FriendListAdapter(context!!, list_friend, layout_no_item)
        this.friendListAdapter.initLoadMore {
            this@SearchFriendFragment.userService.findUserProfiles(this.typingHandler.value, it)
        }
        this.friendListAdapter.onItemClick = {
            ProfileFragment.start(activity!!, it.id)
        }

    }

    private fun setUpService() {
        this.userService = object : UserServiceAdapter(context!!) {
            override fun onFindProfiles(userProfiles: Array<UserProfile>) {
                this@SearchFriendFragment.friendListAdapter.addAll(userProfiles)
                stopLoading()
            }
        }
    }

    private fun handleSearch(text: String) {
        this@SearchFriendFragment.friendListAdapter.clear()
        this.userService.findUserProfiles(text, 1)
        startLoading()
    }
    private fun startLoading() {
        loading_indicator.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        loading_indicator.visibility = View.INVISIBLE
    }
    override fun onDestroy() {
        this.userService.onDestroy()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFriendFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, SearchFriendFragment())
        }
    }
}
