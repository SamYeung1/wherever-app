package com.samyeung.wherever.view.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.samyeung.wherever.R
import kotlinx.android.synthetic.main.layout_list_chip.view.*


class ChipListAdapter(
    val context: Context,
    val recyclerView: RecyclerView,
    val canEdit:Boolean = true
) : RecyclerView.Adapter<ChipListAdapter.ViewHolder>() {
    init {
        val layoutManager = ChipsLayoutManager.newBuilder(context)
            .setScrollingEnabled(true)
        recyclerView.layoutManager = layoutManager.build()
        recyclerView.adapter = this
    }

    private val list = mutableMapOf<String, ChipEntity>()
    var onItemClick: ((ChipEntity) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ChipEntity) {
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
            itemView.chip.text = item.text
            if(canEdit){
                itemView.btn_close.visibility = View.VISIBLE
                itemView.btn_close.setOnClickListener {
                    this@ChipListAdapter.remove(item.id)
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.layout_list_chip))


    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(list.values.toList()[position])


    fun add(chip: ChipEntity) {
        chip.id = chip.id.toLowerCase()
        if(!list.containsKey(chip.id)){
            chip.text = chip.text.capitalize()
            list.put(chip.id, chip)
            notifyItemInserted(list.size - 1)
        }

    }

    fun update(chip: ChipEntity) {
        chip.text = chip.text.capitalize()
        list.put(chip.id, chip)
        notifyDataSetChanged()
    }

    fun addAll(chips: Array<ChipEntity>) {
        list.putAll(chips.map { return@map Pair(it.id, it) })
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        val i = list.values.toList().indexOf(list.remove(id))
        notifyItemRemoved(i)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
    fun getItemsOnlyText(): List<String> {
        return this.list.values.map { return@map it.text }
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

    data class ChipEntity(var id: String, var text: String)
}