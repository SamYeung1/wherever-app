package com.samyeung.wherever.fragment


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.samyeung.wherever.MainActivity

import com.samyeung.wherever.R
import com.samyeung.wherever.api.EmailVerificationServiceAdapter
import com.samyeung.wherever.cst.APIMapping
import com.samyeung.wherever.cst.Setting
import com.samyeung.wherever.fragment.main.HomeFragment
import com.samyeung.wherever.util.ErrorException
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.ValidationUtil
import com.samyeung.wherever.util.validator.EmptyValidator
import kotlinx.android.synthetic.main.fragment_email_verification.*

class EmailVerificationFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var emailVerificationService: EmailVerificationServiceAdapter
    private lateinit var sendButtonRunnable: Runnable
    private lateinit var sendButtonHandler: Handler
    private lateinit var btnSend:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        this.setUpService()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        this.btnSend = btn_send
        this.sendButtonHandler = Handler()
        this.sendButtonRunnable = Runnable {
            this.btnSend.visibility = View.VISIBLE
            this.sendButtonHandler.removeCallbacks(this.sendButtonRunnable)
        }
        sendButtonHandler.postDelayed(sendButtonRunnable, Setting.RESEND_EMAIL_SECOND)
        btn_send.setOnClickListener {
            it.visibility = View.INVISIBLE
            this.emailVerificationService.verifySend(true)
            sendButtonHandler.postDelayed(sendButtonRunnable, Setting.RESEND_EMAIL_SECOND)
        }
        btn_ok.setOnClickListener {
            ValidationUtil.validateForTextInput(
                this, arrayOf(
                    ValidationUtil.TextInputValidator(
                        layout_code, arrayOf(
                            EmptyValidator(context!!)
                        )
                    )
                )
            )
        }
    }

    private fun setUpService() {
        this.emailVerificationService = object : EmailVerificationServiceAdapter(context!!) {
            override fun onVerify() {
                stopLoading()
                if (activity is MainActivity) {
                    FragmentUtil.setFragment(null,activity!!,R.id.content,HomeFragment.newInstance(),
                        MainActivity.FRG_TAG_MAIN,true)
                }
            }

            override fun onVerifySend() {
                Toast.makeText(context,context.resources.getString(R.string.email_sent),Toast.LENGTH_LONG).show()
            }

            override fun onError(e: ErrorException) {
                stopLoading()
                if (e.retcode == APIMapping.CODE_EMAIL_VERIFICATION_ERROR) {
                    ValidationUtil.setError(layout_code, context.resources.getString(R.string.err_code))
                    et_code.text?.clear()
                }else{
                    Toast.makeText(context,e.resmsg,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun success() {
        startLoading()
        this.emailVerificationService.verify(et_code.text.toString().trim())
    }

    private fun startLoading() {
        btn_ok?.isEnabled = false
        LoadingDialog.show(childFragmentManager,"")
    }

    private fun stopLoading() {
        btn_ok?.isEnabled = true
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(this, LoadingDialog.FRA_TAG) as? LoadingDialog)?.dismissAllowingStateLoss()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmailVerificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            EmailVerificationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
