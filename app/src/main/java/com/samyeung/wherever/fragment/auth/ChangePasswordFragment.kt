package com.samyeung.wherever.fragment.auth


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.api.AuthServiceAdapter
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.KeyboardUtil
import com.samyeung.wherever.util.helper.ValidationUtil
import com.samyeung.wherever.util.validator.EmptyValidator
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.util.helper.RequiredTextInputLayoutUtil
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.layout_message_page_success.*

class ChangePasswordFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var authService: AuthServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        title = context!!.resources.getString(R.string.pref_title_change_password)
        arguments?.let {

        }
        this.setUpService()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolbar(true)
        this.bindView(savedInstanceState)
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

    private fun setUpService() {
        this.authService = object : AuthServiceAdapter(context!!) {
            override fun onChangePassword() {
                stopLoading()
                layout_change_password.visibility = View.INVISIBLE
                layout_done.visibility = View.VISIBLE
            }

            override fun onError(e: ErrorException) {
                if (e.retcode == APIMapping.CODE_AUTH_ERROR) {
                    ValidationUtil.setError(layout_password_current,context.resources.getString(R.string.err_password))
                }else if(e.retcode == APIMapping.CODE_PASSWORD_FORMAT){
                    ValidationUtil.setError(layout_password_new,e.resmsg)
                }
                stopLoading()
            }
        }
    }

    private fun bindView(savedInstanceState: Bundle?) {
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_password_current)
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_password_new)
        btn_done.setOnClickListener {
            activity!!.finish()
        }
        btn_ok.setOnClickListener {
            ValidationUtil.validateForTextInput(
                this, arrayOf(
                    ValidationUtil.TextInputValidator(
                        layout_password_current,
                        arrayOf(
                            EmptyValidator(context!!)
                        )
                    ),
                    ValidationUtil.TextInputValidator(
                        layout_password_new,
                        arrayOf(
                            EmptyValidator(context!!)
                        )
                    )
                )
            )
        }
    }

    override fun success() {
        KeyboardUtil.hideKeyboard(activity!!,view!!)
        this.authService.changePassword(et_password_current.text.toString(), et_password_new.text.toString())
        startLoading()
    }

    private fun startLoading() {
        btn_ok.isEnabled = false
        LoadingDialog.show(childFragmentManager, "")
    }

    private fun stopLoading() {
        btn_ok.isEnabled = true
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(
            this,
            LoadingDialog.FRA_TAG
        ) as? LoadingDialog)?.dismissAllowingStateLoss()
    }

    companion object {
        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, ChangePasswordFragment())
        }

        @JvmStatic
        fun newInstance() =
            ChangePasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
