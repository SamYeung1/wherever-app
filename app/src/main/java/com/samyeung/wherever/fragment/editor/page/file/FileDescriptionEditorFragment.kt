package com.samyeung.wherever.fragment.editor.page.file


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.samyeung.wherever.api.TagServiceAdapter
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.util.helper.KeyboardUtil
import com.samyeung.wherever.model.Tag
import com.samyeung.wherever.view.adapter.ChipAutoCompleteTextAdapter
import com.samyeung.wherever.view.adapter.ChipListAdapter
import com.samyeung.wherever.fragment.editor.page.base.DescriptionEditorFragment
import com.samyeung.wherever.fragment.imageviewer.OriginalImageViewFragment
import com.samyeung.wherever.util.base.TypingHandler
import kotlinx.android.synthetic.main.fragment_description_editor.*

class FileDescriptionEditorFragment : DescriptionEditorFragment() {
    private var typingHandler = TypingHandler {h,value ->
        this.handleSearch(value)
        h.stop()
    }
    private lateinit var chipListAdapter: ChipListAdapter
    private lateinit var tagAutoCompleteTextAdapter: ArrayAdapter<String>
    private lateinit var tagService: TagServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.tagService = object :TagServiceAdapter(context!!){
            override fun onGetTags(tags: Array<Tag>) {
                this@FileDescriptionEditorFragment.tagAutoCompleteTextAdapter.clear()
                this@FileDescriptionEditorFragment.tagAutoCompleteTextAdapter.addAll(tags.map { return@map it.title })
            }
        }
    }
    override fun getDataForPrepareUpload(): Data = DescriptionEditorFragment.Data(
        getInputData().imagePath,
        et_title.text.toString(),
        et_description.text.toString(),
        chipListAdapter.getItemsOnlyText().toTypedArray(),
        getInputData().readType
    )

    override fun bindView(savedInstanceState: Bundle?) {
        super.bindView(savedInstanceState)
        et_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@FileDescriptionEditorFragment.typingHandler.start(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@FileDescriptionEditorFragment.typingHandler.stop()
            }
        })
        this.tagAutoCompleteTextAdapter =
            ChipAutoCompleteTextAdapter(context!!, et_tag) {
                this.chipListAdapter.add(ChipListAdapter.ChipEntity(it, it))
            }

        this.chipListAdapter = ChipListAdapter(context!!, list_tag)
        ImageUtil.loadImage(context!!, img_trace, getInputData().imagePath, ImageUtil.createCenterCropRequestOption())
        img_trace.setOnClickListener {
            OriginalImageViewFragment.start(
                activity!!,
                this@FileDescriptionEditorFragment.getInputData().imagePath
            )
        }
        btn_add_tag.setOnClickListener {
            if(et_tag.text.toString().trim().isNotEmpty()){
                et_tag.text.toString().trim().apply {
                    this@FileDescriptionEditorFragment.chipListAdapter.add(ChipListAdapter.ChipEntity(this, this))
                }
                et_tag.setText("")
                list_tag.requestFocus()
                KeyboardUtil.hideKeyboard(activity!!,view!!)
            }
        }
    }
    private fun handleSearch(text: String) {
        this@FileDescriptionEditorFragment.tagService.getTags(text)
    }

    companion object {
        @JvmStatic
        fun newInstance(data: Data) =
            FileDescriptionEditorFragment().apply {
                setUp(this, data)
            }
    }
}
