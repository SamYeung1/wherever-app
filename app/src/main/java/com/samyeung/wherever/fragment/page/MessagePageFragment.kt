package com.samyeung.wherever.fragment.page

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samyeung.wherever.R
import com.samyeung.wherever.fragment.BaseFragment

import kotlinx.android.synthetic.main.fragment_message_page.*

class MessagePageFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var message: String? = null
    private var icon:Drawable? = null
    private var btnTitle: String? = null
    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            message = it.getString(MESSAGE)
            title = it.getString(TITLE)
            btnTitle = it.getString(BTN_TITLE)
            icon = ContextCompat.getDrawable(context!!,it.getInt(ICON))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_default.setText(btnTitle)
        btn_default.setOnClickListener { listener!!.onButtonClicked() }
        tv_title.text = title
        tv_message.text = message
        img_primary_icon.setImageDrawable(icon)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            listener = parent as Listener
        } else {
            listener = context as Listener
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Listener {
        // TODO: Update argument type and name
        fun onButtonClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessagePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        private val MESSAGE = "message"
        private val TITLE = "title"
        private val BTN_TITLE = "btn_title"
        private val ICON = "icon"
        fun newInstance(title: String, message: String,btnTitle:String,@DrawableRes icon:Int) =
                MessagePageFragment().apply {
                    arguments = Bundle().apply {
                        putString(MESSAGE, message)
                        putString(TITLE, title)
                        putString(BTN_TITLE,btnTitle)
                        putInt(ICON,icon)
                    }
                }
    }
}
