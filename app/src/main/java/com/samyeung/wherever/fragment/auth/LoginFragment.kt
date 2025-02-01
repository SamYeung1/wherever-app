package com.samyeung.wherever.fragment.auth


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.MainActivity
import com.samyeung.wherever.activity.PreferenceActivity

import com.samyeung.wherever.R
import com.samyeung.wherever.api.AuthServiceAdapter
import com.samyeung.wherever.util.*
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.KeyboardUtil
import com.samyeung.wherever.util.helper.ValidationUtil
import com.samyeung.wherever.util.validator.*
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.model.Token
import com.samyeung.wherever.fragment.main.PermissionFragment
import com.samyeung.wherever.util.helper.RequiredTextInputLayoutUtil
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.loading.*

class LoginFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var authService: AuthServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpService()
        setUpNavigationBar(ContextCompat.getColor(context!!, android.R.color.white))
        setUpStatusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        arguments?.let {
        }
    }

    private fun setUpService() {
        this.authService = object : AuthServiceAdapter(context!!) {
            override fun onLogin(result: Token, lastLoginTime: Long) {
                if(LoginManager.setUp(context, result)){
                    val fragmentTransaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.content,
                        PermissionFragment.newInstance(), MainActivity.FRG_TAG_MAIN)
                    fragmentTransaction.commit()
                }
            }

            override fun onError(e: ErrorException) {
                stopLoading()
                if (e.retcode == APIMapping.CODE_AUTH_ERROR) {
                    ValidationUtil.setError(layout_password,context.resources.getString(R.string.err_password))
                }

            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        FragmentUtil.getFragmentFromFragmentManagerByTag(
            activity!!,
            MainActivity.FRG_TAG_MAIN
        )!!.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_email)
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_password)
        btn_setting.setOnClickListener {
            PreferenceActivity.start(context!!)
        }
        btn_sign_up.setOnClickListener {
            SignUpFragment.start(activity!!)
        }
        tv_forgot_password.setOnClickListener {
            ForgotPasswordFragment.start(activity!!)
        }
        btn_login.setOnClickListener {
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
                    )
                )
            )
        }
    }

    override fun success() {
        startLoading()
        KeyboardUtil.hideKeyboard(activity!!, view!!)
        this.authService.login(et_email.text.toString(), et_password.text.toString())
    }

    override fun onDestroy() {
        this.authService.onDestroy()
        super.onDestroy()
    }

    private fun startLoading() {
        layout_login?.visibility = View.INVISIBLE
        loading_indicator.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        layout_login?.visibility = View.VISIBLE
        loading_indicator.visibility = View.INVISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
