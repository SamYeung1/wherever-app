package com.samyeung.wherever.fragment.page.social


import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.fragment.main.SearchFriendFragment
import kotlinx.android.synthetic.main.fragment_social.*


class SocialFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = context!!.resources.getString(R.string.social)
        this.setUpService()
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.setUpToolbar(false)
        this.bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        pager_social.adapter = PagerAdapter(context!!, this)
        tab_layout.setupWithViewPager(pager_social)
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                handleFabButton(p0!!.position)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                handleFabButton(p0!!.position)
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                handleFabButton(p0!!.position)
            }

        })
        this.handleFabButton(pager_social.currentItem)
    }

    private fun setUpService() {

    }

    private fun handleFabButton(position: Int) {
        when (position) {
            0 -> {
                fab_search.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_search_white_24dp))
                fab_search.setOnClickListener {
                    SearchFriendFragment.start(activity!!)
                }
                fab_search.show()
            }
            else -> {
                fab_search.hide()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SocialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SocialFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private class PagerAdapter(private val context: Context, parentFragment: Fragment) :
        FragmentStatePagerAdapter(parentFragment.childFragmentManager) {
        private val fragments = arrayListOf(FriendListFragment.newInstance(), RequestListFragment.newInstance())

        override fun getItem(p0: Int): BaseFragment {
            return fragments[p0]
        }

        override fun getCount(): Int {
            return this.fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return arrayOf(
                context.resources.getString(R.string.friend),
                context.resources.getString(R.string.request)
            )[position]
        }
    }

}
