package com.samyeung.wherever.fragment.page.social

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.api.FriendServiceAdapter
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.model.UserProfile
import com.samyeung.wherever.view.custom.ContextMenuRecyclerView
import com.samyeung.wherever.view.adapter.FriendListAdapter
import com.samyeung.wherever.fragment.main.ProfileFragment
import kotlinx.android.synthetic.main.fragment_friend_list.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*

class FriendListFragment : BaseFragment() {
    private lateinit var friendListAdapter: FriendListAdapter
    private lateinit var friendService: FriendServiceAdapter

    companion object {
        fun newInstance() = FriendListFragment()
        fun getViewModel(activity: FragmentActivity): FriendListViewModel {
            return ViewModelProviders.of(activity).get(FriendListViewModel::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = context!!.resources.getString(R.string.friend)
        this.setUpService()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.run {
            getViewModel(this).users.observe(this@FriendListFragment, Observer {
                this@FriendListFragment.friendListAdapter.addAll(it!!.toTypedArray())

            })
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.bindView(savedInstanceState)
    }

    private fun setUpService() {
        this.friendService = object : FriendServiceAdapter(context!!) {
            override fun onGetFriends(friends: Array<UserProfile>) {
                loading_indicator?.visibility = View.GONE
                getViewModel(activity!!).users.value = friends.toMutableList()
                srl_content.isRefreshing = false
            }
        }
        this.friendService.getFriends(1)

    }

    private fun bindView(savedInstanceState: Bundle?) {
        registerForContextMenu(list_friend)
        srl_content.isRefreshing = true
        srl_content.setOnRefreshListener {
            srl_content.isRefreshing = true
            getViewModel(activity!!).users.value?.clear()
            this@FriendListFragment.friendService.getFriends(1)
        }
        this.friendListAdapter =
            FriendListAdapter(context!!, list_friend, layout_no_item)
        this.friendListAdapter.onItemClick = {
            ProfileFragment.start(activity!!, it.id)
        }
        this.friendListAdapter.initLoadMore {
            if (!srl_content.isRefreshing) {
                this.friendService.getFriends(it)
            }
            srl_content.isRefreshing = true
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (menuInfo is ContextMenuRecyclerView.RecyclerContextMenuInfo) {
            activity!!.menuInflater.inflate(R.menu.context_menu_friend, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val menuInfo = item!!.menuInfo
        if (menuInfo is ContextMenuRecyclerView.RecyclerContextMenuInfo) {
            return when (item.itemId) {
                R.id.remove -> {
                    val user = this.friendListAdapter.getItems()[menuInfo.position]
                    AlertUtils.create(context!!, "", context!!.resources.getString(R.string.warm_remove_friend).replace("$",user.displayName), {

                    }, {

                        this.friendListAdapter.remove(user.id)
                        this.friendService.deleteFriend(user.id)
                    }).show()
                    true
                }
                else -> {
                    false
                }
            }
        } else {
            return false
        }
    }

    override fun onDestroy() {
        getViewModel(activity!!).onDestroy()
        this.friendService.onDestroy()
        super.onDestroy()
    }

}
