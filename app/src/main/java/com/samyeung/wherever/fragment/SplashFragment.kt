package com.samyeung.wherever.fragment


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.*
import com.samyeung.wherever.util.helper.AlertUtils
import com.samyeung.wherever.model.AppVersion

class SplashFragment : BaseFragment() {
    private var callback: Callback? = null
    private lateinit var appVersionService: AppVersionServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpStatusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        setUpNavigationBar(ContextCompat.getColor(context!!, R.color.colorPrimary))
        setUpService()
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindVIew(savedInstanceState)
    }

    private fun bindVIew(savedInstanceState: Bundle?) {
        this.appVersionService.checkVersion()
    }

    private fun setUpService() {
        this.appVersionService = object : AppVersionServiceAdapter(context!!) {
            override fun onCheckVersion(result: AppVersion) {
                if (!result.isLatestVersion && result.isForceUpdate) {
                    AlertUtils.createOnlyOK(
                        context,
                        context!!.resources.getString(R.string.app_update),
                        context!!.resources.getString(R.string.msg_version_update),
                        {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                            activity!!.startActivity(intent)
                            activity!!.finish()
                        }).show()
                    return
                }
                this@SplashFragment.callback!!.onSuccess()
            }
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            callback = parent as Callback
        } else {
            callback = context as Callback
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SplashFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onBackPressed(): Boolean {
        return false
    }


    override fun onDestroy() {
        this.appVersionService.onDestroy()
        super.onDestroy()
    }

    interface Callback {
        fun onSuccess()
        fun onError()
    }
}
