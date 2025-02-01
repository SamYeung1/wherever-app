package com.samyeung.wherever.fragment.page


import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng

import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.base.Optional
import com.samyeung.wherever.view.adapter.map.GoogleMapAdapter
import com.samyeung.wherever.view.adapter.map.GoogleMapClusterRender
import com.samyeung.wherever.model.Location
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.util.helper.*
import com.samyeung.wherever.model.Announcement
import com.samyeung.wherever.model.Home
import com.samyeung.wherever.view.adapter.TraceListAdapter
import com.samyeung.wherever.fragment.main.FilterFragment
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment
import com.samyeung.wherever.fragment.main.AnnouncementFragment
import com.samyeung.wherever.fragment.main.SearchTraceFragment
import io.reactivex.*
import kotlinx.android.synthetic.main.bottom_sheet_explore.*
import kotlinx.android.synthetic.main.fragment_explore.*
import java.text.SimpleDateFormat
import java.util.*

private const val FRG_TAG_FILTER = "filter"

abstract class BaseExploreFragment : BaseFragment(), OnMapReadyCallback, FilterFragment.Listener{
    private var canClickRefresh = true
    override fun onMapReady(p0: GoogleMap?) {
        this.googleMapAdapter = GoogleMapAdapter(context!!, p0!!).apply {
            this.onMarkerClick = {
                TraceImageViewFragment.start(activity!!, it.getImage()!!.id)
            }
        }
        LocationUtil.LocationHolder.get().observe(this, Observer {
            if (it != null) {
                (FragmentUtil.getFragmentFromChildFragmentManagerByTag(
                    this,
                    LoadingDialog.FRA_TAG
                ) as? LoadingDialog)?.dismissAllowingStateLoss()
            }
            this@BaseExploreFragment.doFindTrace(it!!, this.filterData)
            if (isFirst) {
                this.googleMapAdapter.move(it.lat, it.lng, 15f)
            }
            setUpCircleOnMap(it)
            isFirst = false
        })
    }

    private var isFirst = true
    private var distanceCircle: Circle? = null


    private var traceRecycleViewAdapter: TraceListAdapter? = null
    private var filterData: Data? = null
    private lateinit var googleMapAdapter: GoogleMapAdapter
    private lateinit var home: Home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.filterData = Data()
        this.title = resources.getString(R.string.app_name)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoadingDialog.show(
            childFragmentManager,
            context!!.resources.getString(R.string.finding_your_location) + " ..."
        )
        setUpToolbar(false)
        setUpService()
        bindView(savedInstanceState)
        setUpMap()

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        ExploreTraceLiveData.get().observe(this, Observer {
            MenuUtil.updateBadge(menu!!, R.id.refresh, it!!.numberOfLatestTrace)
        })
        Home.HomeLiveData.get().observe(this, Observer {
            this@BaseExploreFragment.home = it!!
            if (it.announcements.isNotEmpty()) {
                menu!!.findItem(R.id.news).isVisible = true
                if(Announcement.hasLatestAnnouncement(context!!,it.announcements)){
                    MenuUtil.updateBadge(menu, R.id.news, it.announcements.size)
                }else{
                    MenuUtil.updateBadge(menu, R.id.news, 0)
                }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_explore_menu, menu)
        menu?.let { m ->
            MenuUtil.setUpBadge(m, R.id.refresh, 0) {
                onOptionsItemSelected(m.findItem(R.id.refresh))
            }
            MenuUtil.setUpBadge(m, R.id.news, 0) {
                onOptionsItemSelected(m.findItem(R.id.news))
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.filter -> {
                FilterFragment.show(childFragmentManager, this.filterData!!, FRG_TAG_FILTER)
                true
            }
            R.id.refresh -> {
                ExploreTraceLiveData.get().setNumberOfLatestTrace(0)
                if (this@BaseExploreFragment.canClickRefresh) {
                    LocationUtil.LocationHolder.get().value?.let {
                        this@BaseExploreFragment.doFindTrace(it, this.filterData)
                    }
                }

                this@BaseExploreFragment.canClickRefresh = false
                true
            }
            R.id.news -> {
                AnnouncementFragment.start(activity!!)
                Announcement.updateLatestAnnouncement(context!!, this.home.announcements)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    abstract fun doFindTrace(location: Location, filterData: BaseExploreFragment.Data?)

    private fun setUpMap() {
        val childFragmentManager =
            FragmentUtil.getFragmentFromChildFragmentManagerById(this, R.id.map) as? SupportMapFragment
        childFragmentManager!!.getMapAsync(this)
    }
    private fun setUpGuide(){
    }
    abstract fun setUpService()

    protected fun handleMarker(context: Context, traces: Array<Trace>) {
        tv_item_display.visibility = if (traces.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        this@BaseExploreFragment.googleMapAdapter.clearMarker()
        this@BaseExploreFragment.traceRecycleViewAdapter!!.clear()
        this@BaseExploreFragment.traceRecycleViewAdapter!!.addAll(traces)
        this@BaseExploreFragment.canClickRefresh = true
        traces.forEach { marker ->
            createMarkerTask(context, marker).subscribe { t1: List<Optional<Bitmap>>?, t2: Throwable? ->
                this@BaseExploreFragment.googleMapAdapter.addMarker(
                    marker.id,
                    GoogleMapClusterRender.Marker(
                        marker.title,
                        t1!![0].get(),
                        marker.description,
                        Location(marker.latitude, marker.longitude),
                        marker,
                        if (t1[1].isEmpty) null else t1[1].get()
                    )
                )
            }
        }
    }

    private fun createMarkerTask(context: Context, trace: Trace): Single<List<Optional<Bitmap>>> {
        val task1 = Single.create { emitter: SingleEmitter<Optional<Bitmap>> ->
            Glide.with(context).asBitmap().load(trace.thumbnail)
                .apply(ImageUtil.createCircleCropRequestOption().override(150))
                .into(object : Target<Bitmap> {
                    override fun onLoadStarted(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        emitter.onSuccess(Optional(null))
                    }

                    override fun getSize(cb: SizeReadyCallback) {

                    }

                    override fun getRequest(): Request? {
                        return null
                    }

                    override fun onStop() {

                    }

                    override fun setRequest(request: Request?) {

                    }

                    override fun removeCallback(cb: SizeReadyCallback) {

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onStart() {

                    }

                    override fun onDestroy() {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        emitter.onSuccess(Optional(resource))
                    }
                })
        }
        val task2 = Single.create { emitter: SingleEmitter<Optional<Bitmap>> ->
            Glide.with(context).asBitmap().load(trace.user.icon)
                .apply(ImageUtil.createCircleCropRequestOption().override(50))
                .into(object : Target<Bitmap> {
                    override fun onLoadStarted(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        emitter.onSuccess(Optional(null))
                    }

                    override fun getSize(cb: SizeReadyCallback) {

                    }

                    override fun getRequest(): Request? {
                        return null
                    }

                    override fun onStop() {

                    }

                    override fun setRequest(request: Request?) {

                    }

                    override fun removeCallback(cb: SizeReadyCallback) {

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onStart() {

                    }

                    override fun onDestroy() {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        emitter.onSuccess(Optional(resource))
                    }
                })
        }
        return Single.concat(task1, task2).toList()
    }

    private fun bindView(savedInstanceState: Bundle?) {
        fab_track.setOnClickListener {
            LocationUtil.LocationHolder.get().value?.let {
                this@BaseExploreFragment.googleMapAdapter.move(it.lat, it.lng, 15f, true)
            }

        }
        this.traceRecycleViewAdapter = TraceListAdapter(context!!, list_trace).apply {
            this.onItemClick = {
                this@BaseExploreFragment.googleMapAdapter.move(it.latitude, it.longitude, 20f)
                this@BaseExploreFragment.hideBottomSheet()
            }
        }
        fab_search.setOnClickListener {
            SearchTraceFragment.show(childFragmentManager, this.filterData!!)
        }
    }

    private fun setUpCircleOnMap(location: Location) {
        if (this.distanceCircle == null) {
            this.distanceCircle = this.googleMapAdapter.drawCircle(
                location.lat, location.lng, this.filterData!!.distance.toDouble(),
                Color.argb(80, 94, 190, 232), Color.TRANSPARENT
            )
        } else {
            this.distanceCircle?.center = LatLng(location.lat, location.lng)
        }
    }

    private fun hideBottomSheet() {
        val layoutParam = bottom_sheet_more.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParam.behavior as BottomSheetBehavior
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onApply(data: Data, isExceed: Boolean) {
        if (isExceed) {
            AlertUtils.createOnlyOK(
                context!!,
                context!!.resources.getString(R.string.warning),
                context!!.resources.getString(R.string.warm_exceed_year_max),
                {
                    it.dismiss()
                }, true
            ).show()
        }
        this.filterData = data
        this.distanceCircle?.radius = this.filterData!!.distance.toDouble()
        LocationUtil.LocationHolder.setUp(this.distanceCircle!!.center.latitude, this.distanceCircle!!.center.longitude)

    }

    data class Data(
        var distance: Int = 2000,
        var startDate: Long = -1,
        var endDate: Long = -1,
        var tags: Array<String> = arrayOf()
    ) : Parcelable {
        private val calendar = Calendar.getInstance()
        init {
            if (startDate == -1L) {
                calendar.add(Calendar.YEAR, -1)
                this.startDate = calendar.timeInMillis
            }
            if (endDate == -1L) {
                calendar.add(Calendar.YEAR, 1)
                this.endDate = calendar.timeInMillis
            }
        }

        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.createStringArray()!!
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(distance)
            parcel.writeLong(startDate)
            parcel.writeLong(endDate)
            parcel.writeArray(tags)
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun equals(other: Any?): Boolean {
            val simpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Data
            if (distance != other.distance) return false
            if (simpleDateFormat.format(Date(startDate)) != simpleDateFormat.format(Date(other.startDate))) return false
            if (simpleDateFormat.format(Date(endDate)) != simpleDateFormat.format(Date(other.endDate))) return false
            if (!tags.contentEquals(other.tags)) return false

            return true
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }
}
