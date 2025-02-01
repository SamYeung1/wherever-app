package com.samyeung.wherever.fragment.main


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.samyeung.wherever.R

import com.samyeung.wherever.fragment.BaseBottomSheetDialogFragment
import com.samyeung.wherever.util.helper.DateManager
import com.samyeung.wherever.fragment.page.BaseExploreFragment.Data
import kotlinx.android.synthetic.main.fragment_filter.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


class FilterFragment : BaseBottomSheetDialogFragment() {
    private lateinit var simpleFormat: DateFormat
    private val calendar = Calendar.getInstance()
    private val fromListener = DatePickerDialog.OnDateSetListener { picker, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        if (this.data!!.startDate > this.data!!.endDate) {
            this.data!!.startDate = this.data!!.endDate
        } else {
            this.data!!.startDate = calendar.timeInMillis
        }
        dp_start_date.text = simpleFormat.format(Date(this.data!!.startDate))
        showReset()
    }
    private val toListener = DatePickerDialog.OnDateSetListener { picker, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        if (this.data!!.startDate > this.data!!.endDate) {
            this.data!!.endDate = this.data!!.startDate
        } else {
            this.data!!.endDate = calendar.timeInMillis
        }

        dp_end_date.text = simpleFormat.format(Date(this.data!!.endDate))
        showReset()
    }

    private var mListener: Listener? = null
    private var data: Data? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.simpleFormat = DateManager.createSimpleFormat(DateManager.DATE_BY_DEVICE, context!!)
        setHasOptionsMenu(true)
        this.arguments?.let {
            this.data = (it.getParcelable(ARG_DATA) as Data).copy()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        if (this.data!!.equals(Data())) {
            hideReset()
        }
        btn_reset.setOnClickListener {
            this.data = Data()
            this@FilterFragment.updateView(this.data!!)
            this@FilterFragment.mListener!!.onApply(this.data!!, false)
            hideReset()
            dismiss()
        }
        this.data?.let {
            tv_distance.text = "${it.distance}m"
            sb_distance.progress = (it.distance / 1000) / 5
            sb_distance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    it.distance = max((progress * 1000) * 5, 2000)
                    tv_distance.text = "${it.distance}m"
                    showReset()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })
        }

        btn_ok.setOnClickListener {
            this.mListener!!.onApply(this.data!!, this.isExceed())
            dismiss()

        }
        dp_start_date.text = this.simpleFormat.format(Date(this.data!!.startDate))
        dp_end_date.text = this.simpleFormat.format(Date(this.data!!.endDate))
        dp_start_date.setOnClickListener {
            openFromDatePicker()
        }
        dp_end_date.setOnClickListener {
            openToDatePicker()
        }
    }

    private fun openFromDatePicker() {
        calendar.timeInMillis = this.data!!.startDate
        val picker = DatePickerDialog(
            context!!,
            fromListener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MARCH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        picker.datePicker.maxDate = this.data!!.endDate
        picker.show()
    }

    private fun openToDatePicker() {
        calendar.timeInMillis = this.data!!.endDate
        val picker = DatePickerDialog(
            context!!,
            toListener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MARCH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        picker.datePicker.maxDate = DateManager.toDay
        picker.datePicker.minDate = this.data!!.startDate
        picker.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun updateView(data: Data) {
        sb_distance.progress = ((data.distance / 1000) / 2) - 1
        dp_start_date.text = this.simpleFormat.format(Date(this.data!!.startDate))
        dp_end_date.text = this.simpleFormat.format(Date(this.data!!.endDate))

    }

    private fun showReset() {
        btn_reset.visibility = View.VISIBLE
    }

    private fun hideReset() {
        btn_reset.visibility = View.INVISIBLE
    }

    companion object {
        private const val YEAR_LIMIT = 2
        private const val ARG_DATA = "arg_data"
        private fun newInstance(data: Data): FilterFragment =
            FilterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }

        fun show(fragmentManager: FragmentManager, data: Data, tag: String = "filter") {
            newInstance(data)
                .show(fragmentManager, tag)
        }
    }

    interface Listener {
        fun onApply(data: Data, isExceed: Boolean)
    }

    private fun isExceed(): Boolean {
        val simpleDateFormat = SimpleDateFormat("YYYY")
        return (simpleDateFormat.format(Date(this.data!!.endDate)).toInt() - simpleDateFormat.format(Date(this.data!!.startDate)).toInt()) >= YEAR_LIMIT
    }
}
