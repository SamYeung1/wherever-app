package com.samyeung.wherever.fragment.main


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.MainActivity

import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.PermissionUtil
import com.samyeung.wherever.fragment.page.MessagePageFragment


class PermissionFragment : BaseFragment(), MessagePageFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigationBar(ContextCompat.getColor(context!!, android.R.color.white))
        setUpStatusBarColor(ContextCompat.getColor(context!!, android.R.color.white))
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (PermissionUtil.checkPermissionForApplication(activity!!)) {
            val fragmentTransaction = childFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.content, MessagePageFragment.newInstance(
                    context!!.getString(R.string.title_permission),
                    context!!.getString(R.string.msg_permission),
                    context!!.getString(R.string.allow),
                    R.drawable.ic_location_on_gray_24dp
                ), MainActivity.FRG_TAG_MAIN
            )
            fragmentTransaction.commit()
        } else {
            setUpHome()
        }
    }

    private fun setUpHome() {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content,
            HomeFragment.newInstance(), MainActivity.FRG_TAG_MAIN)
        fragmentTransaction.commit()
    }

    override fun onButtonClicked() {
        PermissionUtil.showRequestForApplication(activity!!, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        FragmentUtil.getFragmentFromChildFragmentManagerByTag(this, MainActivity.FRG_TAG_MAIN)!!.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setUpHome()
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
         * @return A new instance of fragment PermissionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PermissionFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
