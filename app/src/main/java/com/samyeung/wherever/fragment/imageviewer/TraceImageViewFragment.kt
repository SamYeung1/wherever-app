package com.samyeung.wherever.fragment.imageviewer


import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.view.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

import com.samyeung.wherever.api.ReactionServiceAdapter
import com.samyeung.wherever.api.ImageTraceServiceAdapter
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.activity.TraceActivity
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.util.helper.DateManager
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.model.Trace
import kotlinx.android.synthetic.main.bottom_sheet_image_viewer.*
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.toolbar_title.*
import com.samyeung.wherever.R
import com.samyeung.wherever.api.FavouriteServiceAdapter
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.view.adapter.map.GoogleMapAdapterForView
import com.samyeung.wherever.view.adapter.map.GoogleMapClusterRender
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.view.adapter.ChipListAdapter
import com.samyeung.wherever.fragment.main.*


class TraceImageViewFragment : ImageViewFragment(), OnMapReadyCallback {
    private lateinit var chipListAdapter: ChipListAdapter
    private lateinit var googleMapAdapter: GoogleMapAdapterForView
    override fun onMapReady(p0: GoogleMap?) {
        this.googleMapAdapter = GoogleMapAdapterForView(context!!, p0!!)
        this.setUpMarker()

    }

    private lateinit var traceId: String
    private var addLoveListener = {
        fab_love.setImageResource(R.drawable.ic_love_white_24dp)
        this.reactionServiceAdapter!!.insertReaction(this@TraceImageViewFragment.cachedTraceInfoData!!.id)
        this.cachedTraceInfoData!!.reactionTotal = this.cachedTraceInfoData!!.reactionTotal?.plus(1)
        this.cachedTraceInfoData!!.isReacted = true
        this.bindDetail(this.cachedTraceInfoData!!)
    }
    private var removeLoveListener = {
        fab_love.setImageResource(R.drawable.ic_favorite_border_write_24dp)
        this.reactionServiceAdapter!!.deleteReaction(this@TraceImageViewFragment.cachedTraceInfoData!!.id)
        this.cachedTraceInfoData!!.reactionTotal = this.cachedTraceInfoData!!.reactionTotal?.minus(1)
        this.cachedTraceInfoData!!.isReacted = false
        this.bindDetail(this.cachedTraceInfoData!!)
    }
    private var imageTraceServiceAdapter: ImageTraceServiceAdapter? = null
    private var reactionServiceAdapter: ReactionServiceAdapter? = null
    private var favouriteServiceAdapter: FavouriteServiceAdapter? = null
    private var cachedTraceInfoData: Trace? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.intent?.data?.let {
            this.traceId = it.getQueryParameter(URL_TRACE_ID_DATA)!!
        }
        if (savedInstanceState != null) {
            this.cachedTraceInfoData = savedInstanceState.getParcelable(TRACE_DATA)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        this.cachedTraceInfoData?.let {
            menu!!.apply {
                this.findItem(R.id.action_report).isVisible = !it.isMe
                this.findItem(R.id.action_favourite).title = if (it.isFavourite!!)
                    context!!.resources.getString(R.string.remove_favourite)
                else
                    context!!.resources.getString(R.string.add_favourite)
                this.findItem(R.id.action_favourite).icon = if (it.isFavourite!!)
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_star_border_white_24dp
                    ) else
                    ContextCompat.getDrawable(context!!, R.drawable.ic_star_white_24dp)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_favourite -> {
                cachedTraceInfoData?.let {
                    if (it.isFavourite!!) {
                        this.favouriteServiceAdapter!!.removeFavourite(this.traceId)
                        FavouriteFragment.FavouriteLiveData.get().removeTrace(this.cachedTraceInfoData!!)
                        it.isFavourite = false
                    } else {
                        this.favouriteServiceAdapter!!.addFavourite(this.traceId)
                        FavouriteFragment.FavouriteLiveData.get().addTrace(this.cachedTraceInfoData!!)
                        it.isFavourite = true
                    }
                }
                activity!!.invalidateOptionsMenu()
                true
            }
            R.id.action_report ->{
                ReportFragment.start(activity!!,ReportFragment.Data(ReportFragment.Data.TRACE,this.traceId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACE_DATA, this.cachedTraceInfoData)

    }

    private fun setUpMap() {
        val childFragmentManager =
            FragmentUtil.getFragmentFromChildFragmentManagerById(this, R.id.map) as? SupportMapFragment
        childFragmentManager!!.getMapAsync(this)

    }

    override fun handleDetailPage(view: CardView, savedInstanceState: Bundle?) {
        detailPageLoading()
        setUpMap()
        this.favouriteServiceAdapter = FavouriteServiceAdapter(context!!)
        this.reactionServiceAdapter = ReactionServiceAdapter(context!!)
        this.imageTraceServiceAdapter = object : ImageTraceServiceAdapter(context!!) {
            override fun onGetTrace(trace: Trace) {
                this@TraceImageViewFragment.cachedTraceInfoData = trace
                bindDetail(trace)
                this@TraceImageViewFragment.activity!!.invalidateOptionsMenu()
                stopDetailPageLoading()
            }

            override fun onError(e: ErrorException) {
                super.onError(e)
                if (e.retcode == APIMapping.CODE_ERROR_NOT_FOUND) {
                    AlertUtils.createOnlyOK(
                        context,
                        context.resources.getString(R.string.error),
                        context.resources.getString(R.string.error_deleted_trace_by_user),
                        {
                            activity!!.finish()
                        }, false
                    ).show()
                }

            }
        }

        if (cachedTraceInfoData == null) {
            this.imageTraceServiceAdapter!!.getTrace(this.traceId)
        } else {
            this.bindDetail(cachedTraceInfoData!!)
            stopDetailPageLoading()
        }
    }

    private fun bindDetail(trace: Trace) {
        this.setUpMarker()
        this.chipListAdapter = ChipListAdapter(context!!, list_tag, false)
        if (trace.tags.isNotEmpty()) {
            this.chipListAdapter.addAll(trace.tags.map { return@map ChipListAdapter.ChipEntity(it, it) }.toTypedArray())
        } else {
            item_view_tag.visibility = View.GONE
        }
        action_bar_title.text = trace.title
        tv_love?.setOnClickListener {
            ReactionFragment.start(activity!!, trace.id)
        }
        tv_love?.text = trace.reactionTotal.toString()
        tv_user?.text = trace.user.displayName
        tv_description?.text = trace.description
        item_view_date?.title =
            DateManager.createTime(context!!, trace.tokenDate)
        fab_love.setOnClickListener {
            toggleReaction()
        }
        user_profile.setOnClickListener {
            ProfileFragment.start(activity!!, trace.user.id)
        }
        item_view_comment?.value =
            "${trace.commentTotal.toString()} ${context!!.resources.getString(R.string.comments)}"
        item_view_comment?.setOnClickListener {
            CommentFragment.start(activity!!, trace.id)
        }

        if (trace.isReacted!!) {
            fab_love.setImageResource(R.drawable.ic_favorite_border_write_24dp)
        } else {
            fab_love.setImageResource(R.drawable.ic_love_white_24dp)
        }
        ImageUtil.loadImage(
            context!!,
            view!!.findViewById(R.id.img_primary_icon),
            trace.user.icon,
            ImageUtil.createCircleCropRequestOption().fallback(R.drawable.ic_person_icon_default)
        )

        ImageUtil.loadImage(
            context!!,
            view!!.findViewById(R.id.iv_image),
            trace.banners[0],
            RequestOptions().override(1000).diskCacheStrategy(
                DiskCacheStrategy.DATA
            )
        )
    }

    private fun setUpMarker() {
        this.cachedTraceInfoData?.let { trace ->
            this.googleMapAdapter.addMarker(
                "0", GoogleMapClusterRender.Marker(
                    "", null, "",
                    Location(trace.latitude, trace.longitude)
                )
            )
            this.googleMapAdapter.move(trace.latitude, trace.longitude, 18f)
        }

    }

    private fun toggleReaction() {
        if (cachedTraceInfoData!!.isReacted!!) {
            removeLoveListener()
        } else {
            addLoveListener()
        }
    }

    override fun onDestroy() {
        this.imageTraceServiceAdapter?.onDestroy()
        this.favouriteServiceAdapter?.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val TRACE_DATA = "TRACE_DATA"
        private const val URL_TRACE_ID_DATA = "trace_id"
        fun start(activity: Activity, traceID: String) {
            TraceActivity.start(activity, traceID)
        }
    }
}
