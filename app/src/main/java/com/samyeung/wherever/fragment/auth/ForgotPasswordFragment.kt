package com.samyeung.wherever.fragment.auth


import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
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
import com.samyeung.wherever.util.validator.EmailValidator
import com.samyeung.wherever.util.validator.EmptyValidator
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.util.helper.RequiredTextInputLayoutUtil
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.layout_message_page_success.*

class ForgotPasswordFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var authService: AuthServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = context!!.resources.getString(R.string.forgot_password)
        setHasOptionsMenu(true)
        setUpNavigationBar(ContextCompat.getColor(context!!, android.R.color.white))
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolbar(true)
        setUpService()
        bindView(savedInstanceState)
    }

    private fun setUpService() {
        this.authService = object :AuthServiceAdapter(context!!){
            override fun onForgotPassword() {
                stopLoading()
                layout_forgot_password.visibility = View.INVISIBLE
                layout_done.visibility = View.VISIBLE
            }

            override fun onError(e: ErrorException) {
                stopLoading()
                if(e.retcode == APIMapping.CODE_ERROR_NOT_FOUND){
                    ValidationUtil.setError(layout_email,context.resources.getString(R.string.err_email_is_not_exist))
                }
            }
        }
    }

    private fun bindView(savedInstanceState: Bundle?) {
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_email)
        btn_done.setOnClickListener {
            activity!!.finish()
        }
        btn_send.setOnClickListener {
            ValidationUtil.validateForTextInput(
                this, arrayOf(
                    ValidationUtil.TextInputValidator(
                        layout_email,
                        arrayOf(
                            EmptyValidator(context!!),
                            EmailValidator(context!!)
                        )
                    )
                )
            )
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
    companion object {

        @JvmStatic
        private fun newInstance() =
            ForgotPasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }

        fun start(activity: Activity) {
            PortraitBaseActivity.start(activity, ForgotPasswordFragment())
        }
    }

    override fun success() {
        KeyboardUtil.hideKeyboard(activity!!,view!!)
        startLoading()
        this.authService.forgotPassword(et_email.text.toString().trim())
    }
    private fun startLoading() {
        btn_send.isEnabled = false
        LoadingDialog.show(childFragmentManager,"")
    }

    private fun stopLoading() {
        btn_send.isEnabled = true
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(this,LoadingDialog.FRA_TAG) as? LoadingDialog)?.dismissAllowingStateLoss()
    }
}
