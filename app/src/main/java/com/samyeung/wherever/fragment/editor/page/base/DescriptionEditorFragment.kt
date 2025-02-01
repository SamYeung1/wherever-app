package com.samyeung.wherever.fragment.editor.page.base


import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.samyeung.wherever.R
import com.samyeung.wherever.util.helper.RequiredTextInputLayoutUtil
import kotlinx.android.synthetic.main.fragment_description_editor.*
import kotlinx.android.synthetic.main.loading.*

abstract class DescriptionEditorFragment : BaseEditorFragment(){
    protected lateinit var data: Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            arguments?.let {
                this.data = it.getParcelable(KEY_DATA)!!
            }
        } else {
            savedInstanceState.let {
                this.data = it.getParcelable(KEY_DATA)!!
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_DATA, data)
    }

    fun getInputData(): Data = this.data
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_description_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.bindView(savedInstanceState)
    }

    protected open fun bindView(savedInstanceState: Bundle?) {
        RequiredTextInputLayoutUtil.setUpMarkRequired(context!!,layout_title)
    }

    protected fun showLoading() {
        loading_indicator?.visibility = View.VISIBLE
    }

    protected fun hideLoading() {
        loading_indicator?.visibility = View.GONE
    }

    override fun canUpload():Boolean{
        return et_title.text.toString().trim().isNotEmpty()
    }
    override fun showError(){
        layout_title.error = context!!.resources.getString(R.string.err_input_empty)
        layout_title.requestFocus()
    }
    data class Data(
        val imagePath: String,
        val title: String = "",
        val description: String = "",
        val tags: Array<String> = arrayOf(),
        var readType: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArray(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(imagePath)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeStringArray(tags)
            parcel.writeString(readType)
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

    companion object {
        const val KEY_DATA = "data"
        fun setUp(fragment: DescriptionEditorFragment, data: Data) {
            fragment.arguments = Bundle().apply {
                this.putParcelable(KEY_DATA, data)
            }
        }
    }
}
