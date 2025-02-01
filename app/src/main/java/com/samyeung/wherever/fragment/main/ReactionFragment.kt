package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.ReactionServiceAdapter
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.PagingModel
import com.samyeung.wherever.model.Reaction
import com.samyeung.wherever.view.adapter.UserReactionListAdapter
import kotlinx.android.synthetic.main.fragment_reaction.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*


class ReactionFragment : BaseFragment() {
    private lateinit var reactionService: ReactionServiceAdapter
    private lateinit var reactionListAdapter: UserReactionListAdapter
    private var traceId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setUpService()
        this.title = resources.getString(R.string.reactions)
        arguments?.let {
            this.traceId = it.getString(ARG_TRACE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(true)
        this.reactionService.getReactions(this.traceId!!, 1, 10)
        bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        srl_content.isRefreshing = true
        srl_content.setOnRefreshListener {
            srl_content.isRefreshing = true
            this@ReactionFragment.reactionService.getReactions(this.traceId!!, 1, 10)
        }
        this.reactionListAdapter =
            UserReactionListAdapter(context!!, list_reaction, layout_no_item)
        this.reactionListAdapter.onItemClick = {
            ProfileFragment.start(activity!!, it.user.id)
        }
        this.reactionListAdapter.initLoadMore { page ->
            srl_content.isRefreshing = true
            this@ReactionFragment.reactionService.getReactions(this.traceId!!, page, 10)
        }
    }

    private fun setUpService() {
        this.reactionService = object : ReactionServiceAdapter(context!!) {
            override fun onGetReactions(reactions: PagingModel<Array<Reaction>>) {
                loading_indicator?.visibility = View.GONE
                this@ReactionFragment.reactionListAdapter.addAll(reactions.data)
                srl_content.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        this.reactionService.onDestroy()
        super.onDestroy()
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
        const val ARG_TRACE_ID = "TRACE_ID"
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
            ReactionFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity, traceId: String) {
            val bundle = Bundle()
            bundle.putString(ARG_TRACE_ID, traceId)
            PortraitBaseActivity.start(activity, ReactionFragment(), bundle)
        }
    }
}
