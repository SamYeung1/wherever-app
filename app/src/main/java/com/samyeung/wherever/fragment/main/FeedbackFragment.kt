package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast

import com.samyeung.wherever.R
import com.samyeung.wherever.api.FeedbackServiceAdapter
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import com.samyeung.wherever.util.helper.ValidationUtil
import com.samyeung.wherever.util.validator.EmptyValidator
import com.samyeung.wherever.util.validator.LengthValidator
import kotlinx.android.synthetic.main.fragment_feedback.*


class FeedbackFragment : BaseFragment(), ValidationUtil.Success {
    private lateinit var data: Data
    private lateinit var feedbackService: FeedbackServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        title = context!!.resources.getString(R.string.feedback)
        arguments?.let {
            this.data = it.getParcelable(ARG_DATA)!!
        }
        this.setUpService()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(true)
        (activity as? AppCompatActivity)!!.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_gray_24dp)
        this.onBindView(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_feedback_menu, menu)
    }

    private fun setUpService() {
        this.feedbackService = object :FeedbackServiceAdapter(context!!){
            override fun onFeedback() {
                Toast.makeText(context,context.resources.getString(R.string.feedback_sent),Toast.LENGTH_LONG).show()
                activity!!.finish()
            }
        }
    }

    private fun onBindView(savedInstanceState: Bundle?) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.finish()
                true
            }
            R.id.action_ok -> {
                ValidationUtil.validateForTextInput(
                    this,
                    arrayOf(
                        ValidationUtil.TextInputValidator(
                            layout_txt,
                            arrayOf(EmptyValidator(context!!), LengthValidator(context!!, 50))
                        )
                    )
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun success() {
        this.data.message = et_txt.text.toString()
        this.feedbackService.sendFeedback(this.data.message)
        startLoading()
    }

    private fun startLoading() {
        LoadingDialog.show(childFragmentManager,"")
    }

    private fun stopLoading() {
        (FragmentUtil.getFragmentFromChildFragmentManagerByTag(this, LoadingDialog.FRA_TAG) as? LoadingDialog)?.dismissAllowingStateLoss()
    }
    companion object {
        private const val ARG_DATA = "arg_data"
        @JvmStatic
        fun newInstance(data: Data) =
            FeedbackFragment().apply {
                arguments = Bundle().apply {
                    this.putParcelable(ARG_DATA, data)
                }
            }

        fun start(activity: Activity) {
            val bundle = Bundle()
            bundle.putParcelable(ARG_DATA, Data(""))
            PortraitBaseActivity.start(activity, FeedbackFragment(), bundle)
        }
    }

    data class Data(var message: String) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }
}
