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
import com.samyeung.wherever.api.RegisterBody
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.filter.EmojiTextFilter
import com.samyeung.wherever.util.filter.SpecialCharacterTextFilter
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.KeyboardUtil
import com.samyeung.wherever.util.helper.ValidationUtil
import com.samyeung.wherever.util.validator.EmailValidator
import com.samyeung.wherever.util.validator.EmptyValidator
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.util.helper.RequiredTextInputLayoutUtil
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.layout_message_page_success.*

class SignUpFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var authService: AuthServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = resources.getString(R.string.sign_up)
        setUpService()
        setHasOptionsMenu(true)
        setUpNavigationBar(ContextCompat.getColor(context!!, android.R.color.white))
        arguments?.let {
        }
    }
    private fun setUpService() {
        this.authService = object : AuthServiceAdapter(context!!) {
            override fun onRegister() {
                stopLoading()
                layout_sign_up.visibility = View.INVISIBLE
                layout_done.visibility = View.VISIBLE
            }

            override fun onError(e: ErrorException) {
                stopLoading()
                if(e.retcode == APIMapping.CODE_EMAIL_USED){
                    ValidationUtil.setError(layout_email,context.resources.getString(R.string.err_email_is_exist))
                }else if(e.retcode == APIMapping.CODE_PASSWORD_FORMAT){
                    ValidationUtil.setError(layout_password,e.resmsg)
                }
            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(true)
        bindView(savedInstanceState)
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
    private fun bindView(savedInstanceState: Bundle?){
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_email)
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_password)
        btn_done.setOnClickListener {
            activity!!.finish()
        }
        et_displayName.filters = arrayOf(
            EmojiTextFilter(),
            SpecialCharacterTextFilter()
        )
        et_email.filters = arrayOf(EmojiTextFilter())
        et_password.filters = arrayOf(EmojiTextFilter())
        btn_sign_up.setOnClickListener {
            ValidationUtil.validateForTextInput(
                this, arrayOf(
                    ValidationUtil.TextInputValidator(
                        layout_email,
                        arrayOf(
                            EmptyValidator(context!!),
                            EmailValidator(context!!)
                        )
                    ),
                    ValidationUtil.TextInputValidator(
                        layout_password, arrayOf(
                            EmptyValidator(context!!)
                        )
                    ),
                    ValidationUtil.TextInputValidator(
                        layout_displayName, arrayOf(
                            EmptyValidator(context!!)
                        )
                    )
                )
            )
        }
    }
    override fun success() {
        startLoading()
        KeyboardUtil.hideKeyboard(activity!!,view!!)
        this.authService.register(RegisterBody(et_email.text.toString().trim(),et_password.text.toString(),et_displayName.text.toString().trim()))
    }
    private fun startLoading() {
        btn_sign_up.isEnabled = false
        LoadingDialog.show(childFragmentManager,"")
    }

    private fun stopLoading() {
        btn_sign_up.isEnabled = true
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(this,LoadingDialog.FRA_TAG) as? LoadingDialog)?.dismissAllowingStateLoss()
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
        fun start(activity:Activity){
            PortraitBaseActivity.start(activity, SignUpFragment())
        }
    }
}
