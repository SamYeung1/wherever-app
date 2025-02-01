package com.samyeung.wherever.view.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class ChipAutoCompleteTextAdapter(
    context: Context,
    private val autoCompleteTextView: AutoCompleteTextView,
    private val onItemClick: (String) -> Unit
) :
    ArrayAdapter<String>(context, android.R.layout.select_dialog_item, arrayListOf()) {
    init {
        this.autoCompleteTextView.setAdapter(this)
        this.autoCompleteTextView.threshold = 1
        this.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            this.onItemClick(this.getItem(position)!!)
            this.autoCompleteTextView.text.clear()
        }
    }
}