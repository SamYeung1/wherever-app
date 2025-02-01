package com.samyeung.wherever.fragment.editor.page.http


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

class HttpDescriptionEditorFragment : DescriptionEditorFragment() {
    private var typingHandler = TypingHandler {h,value ->
        this.handleSearch(value)
        h.stop()
    }
    private lateinit var chipListAdapter: ChipListAdapter
    private lateinit var tagAutoCompleteTextAdapter: ArrayAdapter<String>
    private lateinit var tagService:TagServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.tagService = object :TagServiceAdapter(context!!){
            override fun onGetTags(tags: Array<Tag>) {
                this@HttpDescriptionEditorFragment.tagAutoCompleteTextAdapter.clear()
                this@HttpDescriptionEditorFragment.tagAutoCompleteTextAdapter.addAll(tags.map { return@map it.title })
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
        showLoading()
        et_tag.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                this@HttpDescriptionEditorFragment.typingHandler.start(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@HttpDescriptionEditorFragment.typingHandler.stop()

            }
        })
        this.tagAutoCompleteTextAdapter =
            ChipAutoCompleteTextAdapter(context!!, et_tag) {
                this.chipListAdapter.add(ChipListAdapter.ChipEntity(it, it))
            }
        this.chipListAdapter = ChipListAdapter(context!!, list_tag)
        val tags = this.getInputData().tags.map { return@map ChipListAdapter.ChipEntity(it, it) }
        this.chipListAdapter.addAll(tags.toTypedArray())
        ImageUtil.loadImage(
            context!!,
            img_trace,
            getInputData().imagePath,
            ImageUtil.createCenterCropRequestOption().override(1000),
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    img_trace.setOnClickListener {
                        OriginalImageViewFragment.start(
                            activity!!,
                            this@HttpDescriptionEditorFragment.getInputData().imagePath
                        )
                    }
                    hideLoading()
                    img_trace.setImageDrawable(resource)
                    return true
                }

            })
        et_title.setText(this.getInputData().title)
        et_description.setText(this.getInputData().description)
        btn_add_tag.setOnClickListener {
            if (et_tag.text.toString().trim().isNotEmpty()) {
                et_tag.text.toString().trim().apply {
                    this@HttpDescriptionEditorFragment.chipListAdapter.add(ChipListAdapter.ChipEntity(this.capitalize(), this))
                }
                et_tag.setText("")
                list_tag.requestFocus()
                KeyboardUtil.hideKeyboard(activity!!, view!!)
            }
        }

    }
    private fun handleSearch(text: String) {
        this@HttpDescriptionEditorFragment.tagService.getTags(text)
    }
    override fun onDestroy() {
        this.tagService.onDestroy()
        super.onDestroy()
    }
    companion object {
        @JvmStatic
        fun newInstance(data: Data) =
            HttpDescriptionEditorFragment().apply {
                setUp(this, data)
            }
    }
}
