package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.api.CommentServiceAdapter
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.model.*
import com.samyeung.wherever.view.adapter.CommentBoxView
import com.samyeung.wherever.view.adapter.CommentListAdapter
import kotlinx.android.synthetic.main.fragment_comment.*
import com.samyeung.wherever.view.custom.ContextMenuRecyclerView
import android.view.ContextMenu.ContextMenuInfo
import com.samyeung.wherever.fragment.TextEditorDialogFragment
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*


class CommentFragment : BaseFragment(), TextEditorDialogFragment.Listener {

    private lateinit var commentService: CommentServiceAdapter
    private lateinit var commentListAdapter: CommentListAdapter
    private var traceId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setUpService()
        this.title = resources.getString(R.string.comments)
        arguments?.let {
            this.traceId = it.getString(ARG_TRACE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(true)
        this.commentService.getComments(this.traceId!!, 1, 10)
        bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        srl_content.isRefreshing = true
        srl_content.setOnRefreshListener {
            srl_content.isRefreshing = true
            this@CommentFragment.commentService.getComments(this.traceId!!, 1, 10)
        }

        this.commentListAdapter =
            CommentListAdapter(context!!, list_comment, layout_no_item)
        registerForContextMenu(list_comment)
        this.commentListAdapter.onItemUserIconClick = {
            ProfileFragment.start(activity!!, it.user.id)
        }
        this.commentListAdapter.initLoadMore { page ->
            srl_content.isRefreshing = true
            this@CommentFragment.commentService.getComments(this.traceId!!, page, 10)
        }
        cbv_comment.setSendButtonClick(object : CommentBoxView.SendButtonClick {
            override fun onSendButtonClick(view: View, message: String) {
                this@CommentFragment.commentService.insertComment(this@CommentFragment.traceId!!, message)
            }
        })
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo)
        if (menuInfo is ContextMenuRecyclerView.RecyclerContextMenuInfo) {
            val comment = this@CommentFragment.commentListAdapter.getItems()[menuInfo.position]
            activity!!.menuInflater.inflate(R.menu.context_menu_comment, menu)
            menu.findItem(R.id.delete).isVisible = comment.is_me
            menu.findItem(R.id.edit).isVisible = comment.is_me
            menu.findItem(R.id.report).isVisible = !comment.is_me
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val infoMenu = item!!.menuInfo
        if (infoMenu is ContextMenuRecyclerView.RecyclerContextMenuInfo) {
            val commentList = this@CommentFragment.commentListAdapter.getItems()
            val comment = commentList[commentList.indexOf(commentList.find { it.id == (infoMenu.data as? Comment)!!.id })]
            when (item.itemId) {
                R.id.edit -> {
                    TextEditorDialogFragment.show(
                        childFragmentManager,
                        context!!.getString(R.string.edit),
                        true,
                        comment.message,
                        true,
                        "${MESSAGE_DIALOG_COMMENT}_${comment.id}",
                        false
                    )
                    return true
                }
                R.id.delete -> {
                    this@CommentFragment.commentService.deleteComment(comment.id)
                    this@CommentFragment.commentListAdapter.pendingRemove(comment.id)
                    return true
                }
                R.id.report -> {
                        ReportFragment.start(activity!!,ReportFragment.Data(ReportFragment.Data.COMMENT,comment.id))
                    return true
                }
                else -> {
                    return false
                }
            }
        } else {
            return false
        }
    }

    private fun setUpService() {
        this.commentService = object : CommentServiceAdapter(context!!) {
            override fun onGetComments(comments: PagingModel<Array<Comment>>) {
                loading_indicator?.visibility = View.GONE
                this@CommentFragment.commentListAdapter.addAll(comments.data)
                srl_content.isRefreshing = false
            }

            override fun onInsertComment(comment: Comment) {
                this@CommentFragment.commentListAdapter.addToTop(comment)
                this@CommentFragment.commentListAdapter.moveToTop()
            }

            override fun onUpdateComment(comment: Comment) {
                this@CommentFragment.commentListAdapter.update(comment)
            }

            override fun onDeleteComment(comment: Comment) {
                this@CommentFragment.commentListAdapter.remove(comment.id)
            }

        }
    }

    override fun onDestroy() {
        this.commentService.onDestroy()
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

    override fun onDone(mTag: String, changedText: String) {
        val tag = mTag.split("_")
        if (tag[0] == MESSAGE_DIALOG_COMMENT) {
            this.commentService.updateComment(tag[1], changedText)
            this.commentListAdapter.pendingUpdate(tag[1])

        }
    }

    override fun onCancel(mTag: String, changedText: String) {

    }

    companion object {
        const val ARG_TRACE_ID = "TRACE_ID"
        const val MESSAGE_DIALOG_COMMENT = "COMMENT"
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
            CommentFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity, traceId: String) {
            val bundle = Bundle()
            bundle.putString(ARG_TRACE_ID, traceId)
            PortraitBaseActivity.start(activity, CommentFragment(), bundle)
        }
    }
}
