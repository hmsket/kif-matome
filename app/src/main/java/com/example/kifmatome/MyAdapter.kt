package com.example.kifmatome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(context: Context, list: ArrayList<Kif>) : ArrayAdapter<Kif>(context, 0, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view  = convertView
        if (view == null) {
            view  = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        }

        // 一行分のデータを取得
        val data = getItem(position)

        // 一行分のレイアウトに取得したdataの情報をセットする
//        view?.findViewById<ImageView>(R.id.kif_icon).setImageBitmap()
        view?.findViewById<TextView>(R.id.kif_title)?.setText(data?.title)
        view?.findViewById<TextView>(R.id.kif_tournament)?.setText(data?.tournament)
        view?.findViewById<TextView>(R.id.kif_date)?.setText(data?.date)
        view?.findViewById<TextView>(R.id.kif_sente)?.setText(data?.sente)
        view?.findViewById<TextView>(R.id.kif_gote)?.setText(data?.gote)

        return view!!
    }
}