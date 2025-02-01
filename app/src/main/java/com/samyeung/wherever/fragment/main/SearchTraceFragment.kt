package com.samyeung.wherever.fragment.main


import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.samyeung.wherever.R
import com.samyeung.wherever.api.TagServiceAdapter
import com.samyeung.wherever.fragment.BaseBottomSheetDialogFragment
import com.samyeung.wherever.model.Tag
import com.samyeung.wherever.view.adapter.ChipAutoCompleteTextAdapter
import com.samyeung.wherever.view.adapter.ChipListAdapter
import com.samyeung.wherever.fragment.page.BaseExploreFragment
import com.samyeung.wherever.util.base.TypingHandler
import kotlinx.android.synthetic.main.fragment_search_trace.*


class SearchTraceFragment : BaseBottomSheetDialogFragment() {
    private var typingHandler = TypingHandler {h,value ->
        this.handleSearch(value)
        h.stop()
    }
    private val tagListObserver:RecyclerView.AdapterDataObserver = object :
        RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if(this@SearchTraceFragment.chipListAdapter.getItemsOnlyText().isNotEmpty()){
                showReset()
            }else{
                hideReset()
            }

        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            if(this@SearchTraceFragment.chipListAdapter.getItemsOnlyText().isNotEmpty()){
                showReset()
            }else{
                hideReset()
            }
        }

    }
    private lateinit var originalData:BaseExploreFragment.Data
    private lateinit var data: BaseExploreFragment.Data
    private var mListener: FilterFragment.Listener? = null
    private lateinit var chipListAdapter: ChipListAdapter
    private lateinit var tagAutoCompleteTextAdapter: ArrayAdapter<String>
    private lateinit var tagService: TagServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.data = it.getParcelable(ARG_DATA)!!
            this.originalData = this.data.copy()
        }
        this.setUpService()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_trace, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.bindView(savedInstanceState)
    }

    private fun bindView(savedInstanceState: Bundle?) {
        if(this.data.equals(BaseExploreFragment.Data())){
            hideReset()
        }
        btn_reset.setOnClickListener {
            this.data = BaseExploreFragment.Data()
            this@SearchTraceFragment.updateView(this.data)
            this@SearchTraceFragment.mListener!!.onApply(this.data,false)
            hideReset()
            dismiss()
        }
        et_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@SearchTraceFragment.typingHandler.start(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@SearchTraceFragment.typingHandler.stop()
            }
        })
        this.tagAutoCompleteTextAdapter =
            ChipAutoCompleteTextAdapter(context!!, et_tag) {
                this.chipListAdapter.add(ChipListAdapter.ChipEntity(it, it))
            }
        this.chipListAdapter = ChipListAdapter(context!!, list_tag)
        this.chipListAdapter.addAll(this.data.tags.map { return@map ChipListAdapter.ChipEntity(it.capitalize(), it) }.toTypedArray())
        this.chipListAdapter.registerAdapterDataObserver(this.tagListObserver)
        btn_add_tag.setOnClickListener {
            if(et_tag.text.toString().trim().isNotEmpty()){
                et_tag.text.toString().trim().apply {
                    this@SearchTraceFragment.chipListAdapter.add(ChipListAdapter.ChipEntity(this.capitalize(), this))
                }
                et_tag.setText("")
            }
        }
        btn_ok.setOnClickListener {
            this.data.tags = (list_tag.adapter as ChipListAdapter).getItemsOnlyText().toTypedArray()
            this@SearchTraceFragment.mListener?.onApply(this.data,false)
            dismiss()
        }

    }
    private fun handleSearch(text: String) {
        this@SearchTraceFragment.tagService.getTags(text)
    }
    private fun setUpService(){
        this.tagService = object :TagServiceAdapter(context!!){
            override fun onGetTags(tags: Array<Tag>) {
                this@SearchTraceFragment.tagAutoCompleteTextAdapter.clear()
                this@SearchTraceFragment.tagAutoCompleteTextAdapter.addAll(tags.map { return@map it.title })
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as FilterFragment.Listener
        } else {
            mListener = context as FilterFragment.Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onDestroy() {
        this.chipListAdapter.unregisterAdapterDataObserver(this.tagListObserver)
        super.onDestroy()
    }

    private fun updateView(data: BaseExploreFragment.Data){
        (list_tag.adapter as ChipListAdapter).clear()
    }
    private fun showReset(){
        btn_reset.visibility = View.VISIBLE
    }
    private fun hideReset(){
        btn_reset.visibility = View.INVISIBLE
    }
    companion object {
        private const val ARG_DATA = "arg_data"
        @JvmStatic
        private fun newInstance(data: BaseExploreFragment.Data): SearchTraceFragment =
            SearchTraceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }

        fun show(fragmentManager: FragmentManager, data: BaseExploreFragment.Data, tag: String = "filter") {
            newInstance(data)
                .show(fragmentManager, tag)
        }
    }
}
