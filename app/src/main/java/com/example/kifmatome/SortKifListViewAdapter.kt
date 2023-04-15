package com.example.kifmatome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SortKifListViewAdapter(val dataSet: MutableList<Kif>):
    RecyclerView.Adapter<SortKifListViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val sortKifIconView = view.findViewById<ImageView>(R.id.kif_icon)
        val sortKifTitleView = view.findViewById<TextView>(R.id.kif_title)
        val sortKifTournamentView = view.findViewById<TextView>(R.id.kif_tournament)
        val sortKifDateView = view.findViewById<TextView>(R.id.kif_date)
        val sortKifSenteView = view.findViewById<TextView>(R.id.kif_sente)
        val sortKifGoteView = view.findViewById<TextView>(R.id.kif_gote)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.listview_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
//        viewHolder.sortKifIconView.setImageBitmap(bmp)
        viewHolder.sortKifTitleView.text = dataSet[position].title
        viewHolder.sortKifTournamentView.text = dataSet[position].tournament
        viewHolder.sortKifDateView.text = dataSet[position].date
        viewHolder.sortKifSenteView.text = dataSet[position].sente
        viewHolder.sortKifGoteView.text = dataSet[position].gote
    }

    override fun getItemCount() = dataSet.size
}