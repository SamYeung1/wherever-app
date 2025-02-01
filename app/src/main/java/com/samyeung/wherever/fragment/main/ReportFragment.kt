package com.samyeung.wherever.fragment.main


import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast

import com.samyeung.wherever.R
import com.samyeung.wherever.api.ReportServiceAdapter
import com.samyeung.wherever.util.LoadingDialog
import com.samyeung.wherever.activity.PortraitBaseActivity
import com.samyeung.wherever.fragment.BaseFragment
import com.samyeung.wherever.util.helper.FragmentUtil
import kotlinx.android.synthetic.main.fragment_report.*


class ReportFragment : BaseFragment() {
    private lateinit var data: Data
    private lateinit var reportService: ReportServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        title = context!!.resources.getString(R.string.report)
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
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar(true)
        (activity as? AppCompatActivity)!!.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_gray_24dp)
        this.onBindView(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.fragment_report_menu, menu)
    }
    private fun setUpService() {
        this.reportService = object :ReportServiceAdapter(context!!){
            override fun onReportRequest() {
                Toast.makeText(context,context.resources.getString(R.string.report_sent),Toast.LENGTH_LONG).show()
                activity!!.finish()
            }
        }
    }
    private fun onBindView(savedInstanceState: Bundle?) {
        this.data.message = "Fake content"
        rdgrp_report.check(R.id.type_1)
        rdgrp_report.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.type_1 -> {
                    this.data.message = "Fake content"
                }
                R.id.type_2 -> {
                    this.data.message = "Sexually explicit Content"
                }
                R.id.type_3 -> {
                    this.data.message = "Other"
                }
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.finish()
                true
            }
            R.id.action_ok -> {
                this.reportService.sendReport(this.data.sourceId,this.data.type,this.data.message)
                startLoading()
                item.isEnabled = false
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
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
            ReportFragment().apply {
                arguments = Bundle().apply {
                    this.putParcelable(ARG_DATA, data)
                }
            }

        fun start(activity: Activity, data: Data) {
            val bundle = Bundle()
            bundle.putParcelable(ARG_DATA, data)
            PortraitBaseActivity.start(activity, ReportFragment(), bundle)
        }
    }

    data class Data(val type: String, val sourceId: String, var message: String = "") : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(type)
            parcel.writeString(sourceId)
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            const val TRACE = "TRACE"
            const val USER = "USER"
            const val COMMENT = "COMMENT"
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }
}
