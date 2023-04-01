package com.example.kifmatome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class TabFragment(val fa: FragmentActivity, val position: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ここでDBからデータを取得し、listviewを作成する。そのviewを返す。
        // tab_order = position であるような tab_id を取得し、
        // その tab_id を用いてテーブル file からデータを取得し、棋譜リストを作成する

        val root = inflater.inflate(R.layout.fragment_kif_listview, container, false)

        val listview = root.findViewById<ListView>(R.id.kif_listview)

        // リストデータの作成
        val dataList = arrayListOf<Kif>()
        for (i in 0..10){
            dataList.add(Kif().apply {
                title = "${i}番目のタイトル"
                tournament =  "棋戦：${i}番目のテキスト"
                date =  "日付：${i}番目のテキスト"
                sente =  "先手：${i}番目のテキスト"
                gote =  "後手：${i}番目のテキスト"
            })
        }

        val adapter = MyAdapter(fa, dataList)
        listview.adapter = adapter

        return root
    }
}