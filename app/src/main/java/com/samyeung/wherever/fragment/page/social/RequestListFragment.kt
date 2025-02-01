package com.samyeung.wherever.fragment.page.social

import android.os.Bundle
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.api.RequestServiceAdapter
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.Request
import com.samyeung.wherever.view.adapter.RequestListAdapter
import com.samyeung.wherever.fragment.main.ProfileFragment
import kotlinx.android.synthetic.main.fragment_request_list.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*

class RequestListFragment : BaseFragment() {
    private lateinit var requestListAdapter: RequestListAdapter
    private lateinit var requestService: RequestServiceAdapter

    companion object {
        fun newInstance() = RequestListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = context!!.resources.getString(R.string.request)
        this.setUpService()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.bindView(savedInstanceState)
    }

    private fun setUpService() {
        this.requestService = object : RequestServiceAdapter(context!!) {
            override fun onGetRequests(requests: Array<Request>) {
                loading_indicator?.visibility = View.GONE
                this@RequestListFragment.requestListAdapter.addAll(requests)
                srl_content.isRefreshing = false
            }
        }
        this.requestService.getRequests(1)


    }

    private fun bindView(savedInstanceState: Bundle?) {
        srl_content.isRefreshing = true
        srl_content.setOnRefreshListener {
            srl_content.isRefreshing = true
            this@RequestListFragment.requestService.getRequests(1)
        }
        this.requestListAdapter =
            RequestListAdapter(context!!, list_request, layout_no_item)
        this.requestListAdapter.onItemClick = {
            if (it.type == Request.TYPE_FRIEND) {
                ProfileFragment.start(activity!!, it.user.id)
            }

        }
        this.requestListAdapter.onAcceptClick = {
            if (it.type == Request.TYPE_FRIEND) {
                this@RequestListFragment.requestService.acceptFriendRequest(it.id)
                FriendListFragment.getViewModel(activity!!).users.value = arrayListOf(it.user)
            }

        }
        this.requestListAdapter.onCancelClick = {
            if (it.type == Request.TYPE_FRIEND) {
                this@RequestListFragment.requestService.denyFriendRequest(it.id)
            }

        }
        this.requestListAdapter.initLoadMore {
            if (!srl_content.isRefreshing) {
                this.requestService.getRequests(it)
            }
            srl_content.isRefreshing = true
        }
    }

    override fun onDestroy() {
        this.requestService.onDestroy()
        super.onDestroy()
    }

}
