package com.samyeung.wherever.fragment.page


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.UserInboxServiceAdapter
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.UserInbox
import com.samyeung.wherever.view.adapter.UserInboxListAdapter
import kotlinx.android.synthetic.main.fragment_inbox.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*

class InboxFragment : BaseFragment() {
    private lateinit var userInboxService: UserInboxServiceAdapter
    private lateinit var userInboxListAdapter: UserInboxListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setUpService()
        this.title = resources.getString(R.string.inbox)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(false)
        this.userInboxService.getInboxes(1, 10)
        bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        srl_content.isRefreshing = true
        srl_content.setOnRefreshListener {
            srl_content.isRefreshing = true
            this@InboxFragment.userInboxService.getInboxes(1, 10)
        }
        this.userInboxListAdapter =
            UserInboxListAdapter(context!!, list_inbox, layout_no_item)
        this.userInboxListAdapter.initLoadMore { page ->
            srl_content.isRefreshing = true
            this@InboxFragment.userInboxService.getInboxes(page, 10)
        }
    }

    private fun setUpService() {
        this.userInboxService = object : UserInboxServiceAdapter(context!!) {
            override fun onGetInboxes(inboxes: Array<UserInbox>) {
                loading_indicator?.visibility = View.GONE
                this@InboxFragment.userInboxListAdapter.addAll(inboxes)
                this@InboxFragment.userInboxService.updateInboxReadAll()
                srl_content.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        this.userInboxService.onDestroy()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InboxFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            InboxFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
