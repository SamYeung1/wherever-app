package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import com.samyeung.wherever.R
import com.samyeung.wherever.api.RequestServiceAdapter
import com.samyeung.wherever.api.UserServiceAdapter
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.activity.UserProfileActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.ButtonUtil
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.model.Request
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.UserProfile
import com.samyeung.wherever.view.adapter.TraceListAdapter
import com.samyeung.wherever.fragment.imageviewer.OriginalImageViewFragment
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.loading.*


class ProfileFragment : BaseFragment() {
    private lateinit var userID: String
    private var cachedUserProfileData: UserProfile? = null
    private var cachedLatestTraces: Array<Trace>? = null
    private var userService: UserServiceAdapter? = null
    private var requestService: RequestServiceAdapter? = null
    private lateinit var traceListAdapter: TraceListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setUpService()
        title = ""
        activity!!.intent.data?.let {
            this.userID = it.getQueryParameter(URL_USER_ID_DATA)!!
        }
        if (savedInstanceState != null) {
            this.cachedUserProfileData = savedInstanceState.getParcelable(PROFILE_DATA)
            this.cachedLatestTraces = savedInstanceState.getParcelableArray(LATEST_TRACES) as Array<Trace>?
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PROFILE_DATA, this.cachedUserProfileData)
        outState.putParcelableArray(LATEST_TRACES,this.cachedLatestTraces)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu!!.findItem(R.id.action_report).isVisible = false
        if(this.cachedUserProfileData !=null){
           this.cachedUserProfileData!!.isMe?.let {
               menu.findItem(R.id.action_report).isVisible = !it
           }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_profile_menu,menu)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startLoading()
        setUpToolbar(true)
        setUpStatusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        if (this.cachedUserProfileData == null) {
            this@ProfileFragment.userService!!.getUserProfile(this.userID)
        } else {
            bindView(this.cachedUserProfileData!!)
        }
        if (this.cachedLatestTraces == null) {
            this.userService!!.getTraces(1, 5, this.userID)
        } else {
            bindViewLatestTraces(this.cachedLatestTraces!!)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.finish()
                true
            }
            R.id.action_report ->{
                ReportFragment.start(activity!!,ReportFragment.Data(ReportFragment.Data.USER,this.userID))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setUpService() {
        this.userService = object : UserServiceAdapter(context!!) {
            override fun onGetProfile(userProfile: UserProfile) {
                this@ProfileFragment.cachedUserProfileData = userProfile
                this@ProfileFragment.bindView(userProfile)
            }

            override fun onGetTraces(traces: Array<Trace>) {
                this@ProfileFragment.cachedLatestTraces = traces
                this@ProfileFragment.bindViewLatestTraces(traces)
            }

            override fun onError(e: ErrorException) {
                if (e.retcode == APIMapping.CODE_PERMISSION_ERROR) {
                    card_trace.visibility = View.GONE
                }
            }
        }
        this.requestService = object : RequestServiceAdapter(context!!) {
            override fun onSendFriendRequest() {
                btn_friend.isEnabled = true
            }

            override fun onCancelFriendRequest() {
                btn_friend.isEnabled = true
            }

            override fun onAcceptFriendRequest(request: Request?) {
                btn_friend.isEnabled = true
            }
        }

    }
    private fun bindViewLatestTraces(latestTraces:Array<Trace>){
        card_trace.visibility = View.VISIBLE
        tv_no_item.visibility = if(latestTraces.isEmpty()) View.VISIBLE else View.INVISIBLE
        this@ProfileFragment.traceListAdapter.addAll(latestTraces)
    }
    private fun bindView(profile: UserProfile) {
        this.traceListAdapter = TraceListAdapter(context!!, list_trace)
        this.traceListAdapter.onItemClick = {
            TraceImageViewFragment.start(activity!!, it.id)
        }
        if (profile.isMe!!) {
            btn_friend.visibility = View.GONE
        } else {
            ButtonUtil.setUpFriendRelationshipButton(
                context!!,
                btn_friend,
                this.cachedUserProfileData!!,
                this.requestService!!
            )
        }
        tv_user_id.text = "@" + profile.accountID
        tv_name.text = profile.displayName
        tv_aboutme.text =
            if (profile.aboutMe != null) profile.aboutMe else context!!.resources.getString(R.string.no_description)
        toolbar_title.text = profile.displayName
        ImageUtil.loadImage(
            context!!, img_primary_icon, profile.icon,
            ImageUtil.createCircleCropRequestOption()
                .fallback(
                    R.drawable.ic_person_icon_default
                )
        )
        ImageUtil.loadImageWithPalette(
            context!!, img_profile_image, profile.profileIcon,
            ImageUtil.createCenterCropRequestOption()
                .fallback(R.drawable.bg_profile_icon_default)
        ) { palette ->
            palette?.let {
                val color = palette.getMutedColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                val colorDrawable = ColorDrawable()
                colorDrawable.color = color
                setUpStatusBarColor(color)
                collapsing_toolbar.setStatusBarScrimColor(color)
                collapsing_toolbar.setContentScrimColor(color)

            }
            img_profile_image.setOnClickListener {
                profile.profileIcon?.let {
                    OriginalImageViewFragment.start(activity!!, it)
                }

            }
            img_primary_icon.setOnClickListener {
                profile.icon?.let {
                    OriginalImageViewFragment.start(activity!!, it)
                }
            }
            stopLoading()
            activity!!.invalidateOptionsMenu()
        }
    }

    private fun startLoading() {
        content?.visibility = View.INVISIBLE
        loading_indicator.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        content?.visibility = View.VISIBLE
        loading_indicator.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        this.userService?.onDestroy()
        this.requestService?.onDestroy()
        super.onDestroy()
    }

    companion object {
        const val LATEST_TRACES = "LATEST_TRACES"
        const val PROFILE_DATA = "PROFILE_DATA"
        const val PROFILE_EXTRA_INFO_DATA = "PROFILE_EXTRA_INFO_DATA"
        const val URL_USER_ID_DATA = "user_id"
        @JvmStatic
        private fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity, userID: String = "me") {
            UserProfileActivity.start(activity, userID)
        }
    }
}
