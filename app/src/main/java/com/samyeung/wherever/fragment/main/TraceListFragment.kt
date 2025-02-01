package com.samyeung.wherever.fragment.main

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseBottomSheetDialogFragment
import com.samyeung.wherever.model.Trace
import kotlinx.android.synthetic.main.dialog_image_list.*
import kotlinx.android.synthetic.main.dialog_image_list.view.*
import com.samyeung.wherever.view.adapter.TraceListAdapter
import com.samyeung.wherever.fragment.imageviewer.TraceImageViewFragment


class TraceListFragment : BaseBottomSheetDialogFragment() {
    private var images: ArrayList<Trace>? = null
    private var imageListAdapter: TraceListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.images = it.getParcelableArrayList<Trace>(IMAGES)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_image_list, container, false)
        return view
    }
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = Dialog(context!!)
//        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
//        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setContentView(R.layout.fragment_image_list_dialog)
//        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
//        return dialog
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        content.setOnClickListener {
            this@TraceListFragment.dismiss()
        }
        this.imageListAdapter = TraceListAdapter(context!!, view.list_image)
        this.images?.forEach {
            this.imageListAdapter!!.add(it)
        }
        this.imageListAdapter!!.onItemClick = {
            TraceImageViewFragment.start(activity!!, it.id)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TraceListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        val IMAGES = "images"

        private fun newInstance(images: ArrayList<Trace>) =
            TraceListFragment().apply {
                arguments = Bundle().apply {
                    this.putParcelableArrayList(IMAGES, images)
                }
            }

        fun show(fragmentManager: FragmentManager, images: ArrayList<Trace>, tag: String = "dialog") {
            newInstance(images)
                .show(fragmentManager, tag)
        }
    }
}