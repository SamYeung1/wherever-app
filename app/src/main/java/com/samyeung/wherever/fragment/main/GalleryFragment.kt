package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.annotation.MainThread
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.ImageTraceServiceAdapter
import com.samyeung.wherever.api.UserServiceAdapter
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.view.custom.menu.MenuListDialogFragment
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.view.adapter.TraceListAdapter
import com.samyeung.wherever.fragment.editor.HttpEditorFragment
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*


class GalleryFragment : BaseFragment(), MenuListDialogFragment.Listener {
    private lateinit var traceListAdapter: TraceListAdapter
    private lateinit var userService: UserServiceAdapter
    private lateinit var imageTraceService: ImageTraceServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.setUpService()
        title = context!!.resources.getString(R.string.gallery)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            GalleryLiveData.get().observe(this@GalleryFragment, Observer {
                this@GalleryFragment.traceListAdapter.update(it!!.updatedTrace)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindVIew(savedInstanceState)
    }

    private fun setUpService() {
        this.userService = object : UserServiceAdapter(context!!) {
            override fun onGetTraces(traces: Array<Trace>) {
                loading_indicator?.visibility = View.GONE
                this@GalleryFragment.traceListAdapter.addAll(traces)
            }
        }
        this.imageTraceService = object : ImageTraceServiceAdapter(context!!) {
            override fun onDeleteTrace(trace: Trace) {
                this@GalleryFragment.traceListAdapter.remove(trace.id)
            }
        }
    }

    private fun bindVIew(savedInstanceState: Bundle?) {
        this.traceListAdapter =
            TraceListAdapter(context!!, list_trace, false, layout_no_item)
        this.traceListAdapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("trace", it)
            MenuListDialogFragment.show(
                childFragmentManager, arrayListOf(
                    com.samyeung.wherever.view.custom.menu.MenuItem(
                        context!!.resources.getString(R.string.view),
                        "",
                        R.drawable.ic_pageview_gray_24dp
                    ),
                    com.samyeung.wherever.view.custom.menu.MenuItem(
                        context!!.resources.getString(R.string.edit),
                        "",
                        R.drawable.ic_edit_gray_24dp
                    ),
                    com.samyeung.wherever.view.custom.menu.MenuItem(
                        context!!.resources.getString(R.string.delete),
                        "",
                        R.drawable.ic_delete_gray_24dp,
                        R.color.colorDelete
                    )
                ), TAG_MENU_TRACE, data = bundle
            )
        }
        this.userService.getTraces(1, 20)
        this.traceListAdapter.initLoadMore {
            this.userService.getTraces(it, 20)
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

    override fun onMenuClicked(mTag: String, position: Int, data: Bundle?) {
        if (mTag == TAG_MENU_TRACE) {
            when (position) {
                0 -> {
                    TraceImageViewFragment.start(activity!!, data!!.getParcelable<Trace>("trace").id)
                }
                1 -> {
                    HttpEditorFragment.start(activity!!, data!!.getParcelable("trace"))
                }
                2 -> {
                    data!!.getParcelable<Trace>("trace")!!.let { trace ->
                        AlertUtils.create(context!!, "", context!!.resources.getString(R.string.warm_remove_trace), {

                        }, {
                            this.traceListAdapter.pendingRemove(trace.id)
                            this.imageTraceService.deleteTrace(trace.id)
                        }).show()

                    }

                }
            }
        }
    }


    companion object {
        private const val TAG_MENU_TRACE = "tag_menu_trace"
        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, GalleryFragment())
        }
    }

    class GalleryLiveData : MutableLiveData<GalleryLiveData.Model>() {
        fun setUpdatedTrace(trace: Trace) {
            this.value = Model(trace)
        }

        companion object {
            private var sInstance: GalleryLiveData? = null

            @MainThread
            @JvmStatic
            fun get(): GalleryLiveData {
                if (sInstance == null) {
                    sInstance = GalleryLiveData()
                }
                return sInstance!!
            }
        }

        data class Model(val updatedTrace: Trace)
    }
}
