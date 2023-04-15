package com.example.kifmatome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SortTabListViewAdapter(val dataSet: MutableList<String>):
    RecyclerView.Adapter<SortTabListViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sortTabNameView = view.findViewById<TextView>(R.id.sort_tab_name_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.sort_tab_listview_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.sortTabNameView.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size
}