package com.example.base.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.utils.dLog

abstract class BaseAdapter<VH : RecyclerView.ViewHolder, I>(
    private val items: ArrayList<I>
) : RecyclerView.Adapter<VH>() {

    override fun getItemCount(): Int {
        return items.size
    }

    fun moveItemToTop(position: Int) {
        val secondItem = items[position]
        items[position] = items[0]
        items[0] = secondItem
        "moveItemToTop position = $position \n items[position] = ${items[position]} \n items[0] = ${items[0]}".dLog()
        notifyItemMoved(position, 0)
    }

    fun addItems(list: List<I>) {
        items.addAll(list)
    }


    fun clearItems() {
        items.clear()
    }

    fun getItem(position: Int) = items[position]

    fun hasItems() = items.size > 0

}