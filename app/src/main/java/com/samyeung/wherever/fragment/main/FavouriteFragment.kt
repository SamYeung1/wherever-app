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
import com.samyeung.wherever.api.FavouriteServiceAdapter
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.view.custom.menu.MenuListDialogFragment
import com.samyeung.wherever.model.PagingModel
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.view.adapter.TraceListAdapter
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.layout_no_item.*
import kotlinx.android.synthetic.main.loading.*


class FavouriteFragment : BaseFragment(), MenuListDialogFragment.Listener {
    private lateinit var traceListAdapter: TraceListAdapter
    private lateinit var favouriteService: FavouriteServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.setUpService()
        title = context!!.resources.getString(R.string.favourite)
        arguments?.let {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run {
            FavouriteLiveData.get().observe(this@FavouriteFragment, Observer {
                it?.apply {
                    when (this.action) {
                        FavouriteLiveData.ACTION_ADD -> {
                            this@FavouriteFragment.traceListAdapter.add(this.trace)
                        }
                        FavouriteLiveData.ACTION_REMOVE -> {
                            this@FavouriteFragment.traceListAdapter.remove(this.trace.id)
                        }
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setUpToolbar(true)
        this.bindVIew(savedInstanceState)
    }

    private fun setUpService() {
        this.favouriteService = object : FavouriteServiceAdapter(context!!) {
            override fun onGetFavourites(favourites: PagingModel<Array<Trace>>) {
                loading_indicator?.visibility = View.GONE
                this@FavouriteFragment.traceListAdapter.addAll(favourites.data)
            }


            override fun onRemoveFavourite(favourite: Trace?) {
                this@FavouriteFragment.traceListAdapter.remove(favourite!!.id)
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
                        context!!.resources.getString(R.string.remove),
                        "",
                        R.drawable.ic_delete_gray_24dp,
                        R.color.colorDelete
                    )
                ), TAG_MENU_TRACE, data = bundle
            )
        }
        this.favouriteService.getFavourites(1, 20)
        this.traceListAdapter.initLoadMore {
            this.favouriteService.getFavourites(it, 20)
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
                    data!!.getParcelable<Trace>("trace")!!.let {
                        this.traceListAdapter.pendingRemove(it.id)
                        this.favouriteService.removeFavourite(it.id)
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG_MENU_TRACE = "tag_menu_trace"
        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, FavouriteFragment())
        }
    }

    class FavouriteLiveData : MutableLiveData<FavouriteLiveData.Model>() {
        fun addTrace(trace: Trace) {
            this.value = Model(trace, ACTION_ADD)
        }

        fun removeTrace(trace: Trace) {
            this.value = Model(trace, ACTION_REMOVE)
        }

        companion object {
            const val ACTION_ADD = "add"
            const val ACTION_REMOVE = "remove"
            private var sInstance: FavouriteLiveData? = null

            @MainThread
            @JvmStatic
            fun get(): FavouriteLiveData {
                if (sInstance == null) {
                    sInstance = FavouriteLiveData()
                }
                return sInstance!!
            }
        }

        data class Model(val trace: Trace, val action: String)
    }
}
