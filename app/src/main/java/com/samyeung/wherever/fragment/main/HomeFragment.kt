package com.samyeung.wherever.fragment.main


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import android.view.*

import com.samyeung.wherever.R
import com.samyeung.wherever.api.HomeServiceAdapter
import com.samyeung.wherever.activity.camera.CameraActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.LocationUtil
import com.samyeung.wherever.util.helper.MenuUtil
import com.samyeung.wherever.model.Home
import com.samyeung.wherever.fragment.page.ExploreFragment
import com.samyeung.wherever.fragment.page.MoreFragment
import com.samyeung.wherever.fragment.page.InboxFragment
import com.samyeung.wherever.fragment.page.social.SocialFragment
import io.nlopez.smartlocation.OnLocationUpdatedListener
import kotlinx.android.synthetic.main.fragment_home.*

const val SAVED_STATE_CONTAINER_KEY = "ContainerKey"
const val SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey"

class HomeFragment : BaseFragment(), OnLocationUpdatedListener {
    private var savedStateSparseArray = SparseArray<Fragment.SavedState>()
    private var currentSelectItemId = R.id.action_explore
    private lateinit var homeService: HomeServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY)!!
            currentSelectItemId = savedInstanceState
                .getInt(SAVED_STATE_CURRENT_TAB_KEY)
        }
        LocationUtil.start(context!!, this)
        setUpNavigationBar(ContextCompat.getColor(context!!, android.R.color.white))
        setUpStatusBarColor(ContextCompat.getColor(context!!, android.R.color.white))
        this.title = "Wherever"
        setHasOptionsMenu(true)
        setUpService()
        arguments?.let {
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSparseParcelableArray(SAVED_STATE_CONTAINER_KEY, savedStateSparseArray)
        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectItemId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomNavBar(savedInstanceState)
        bindVIew(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun setUpService(){
        this.homeService = object :HomeServiceAdapter(context!!){
            override fun onGetHome(result: Home) {
                MenuUtil.updateBadge(context, bnv, R.id.action_inbox, result.numberOfInbox)
                MenuUtil.updateBadge(context, bnv, R.id.action_social, result.numberOfRequest)
                Home.HomeLiveData.get().update(result)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        FragmentUtil.getFragmentFromChildFragmentManagerById(
            this@HomeFragment,
            R.id.fl_main_content
        )!!.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (requestCode == 100) {
            CameraActivity.handleData(activity!!, resultCode, data)
        } else if (!LocationUtil.handleOnActivityResult(context!!, requestCode, resultCode, this)) {

        }
    }

    private fun setUpBottomNavBar(savedInstanceState: Bundle?) {
        MenuUtil.setUpBadgeForBottomNavigation(context!!, bnv, R.id.action_inbox, 0)
        MenuUtil.setUpBadgeForBottomNavigation(context!!, bnv, R.id.action_social, 0)
        bnv.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when (it.itemId) {
                R.id.action_explore -> {

                    this.setFragment(ExploreFragment.newInstance(), R.id.action_explore, "explore")

                    true
                }
                R.id.action_social -> {
                    MenuUtil.updateBadge(context!!, bnv, R.id.action_social, 0)
                    this.setFragment(SocialFragment.newInstance(), R.id.action_social, "social")
                    true

                }
                R.id.action_inbox -> {
                    MenuUtil.updateBadge(context!!, bnv, R.id.action_inbox, 0)
                    this.setFragment(InboxFragment.newInstance(), R.id.action_inbox, "inbox")

                    true

                }
                R.id.action_more -> {

                    this.setFragment(MoreFragment.newInstance(), R.id.action_more, "more")

                    true

                }
                else -> {
                    false
                }
            }
        }
        bnv.selectedItemId = currentSelectItemId

    }


    private fun bindVIew(savedInstanceState: Bundle?) {
        fab_camera.setOnClickListener {
            CameraActivity.start(activity!!, 100)
        }
        this.homeService.getHome()
    }

    override fun onLocationUpdated(p0: Location?) {
        p0?.let {
            LocationUtil.LocationHolder.setUp(it.latitude, it.longitude)
        }

    }

    override fun onDestroy() {
        LocationUtil.onDestroy()
        super.onDestroy()
    }

    //Save instance for fragment
    private fun setFragment(fragment: BaseFragment, actionId: Int, key: String) {
        if (FragmentUtil.getFragmentFromChildFragmentManagerByTag(this, key) == null) {
            savedFragmentState(actionId)
            fragment.setInitialSavedState(savedStateSparseArray[actionId])
            FragmentUtil.setFragment(this, R.id.fl_main_content, fragment, key)

        }

    }

    private fun savedFragmentState(actionId: Int) {
        val currentFragment = FragmentUtil.getFragmentFromChildFragmentManagerById(this, R.id.fl_main_content)
        if (currentFragment != null) {
            savedStateSparseArray.put(
                currentSelectItemId,
                childFragmentManager.saveFragmentInstanceState(currentFragment)
            )
        }
        currentSelectItemId = actionId
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
