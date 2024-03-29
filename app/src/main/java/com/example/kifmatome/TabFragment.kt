package com.example.kifmatome

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class TabFragment(val fa: FragmentActivity, val db: SQLiteDatabase, val position: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_kif_listview, container, false)

        val listview = root.findViewById<ListView>(R.id.kif_listview)

        // リストデータの作成
        val dataList = arrayListOf<Kif>()

        var sql = "SELECT _id FROM tab ORDER BY tab_order ASC LIMIT 1 OFFSET " + position
        var cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val tabId = cursor.getInt(0)

        sql = "SELECT * FROM file WHERE tab_id = " + tabId + " ORDER BY file_order ASC"
        cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val tilteIdx = cursor.getColumnIndex("file_title")
        val tournamentIdx = cursor.getColumnIndex("file_tournament")
        val dateIdx = cursor.getColumnIndex("file_date")
        val senteIdx = cursor.getColumnIndex("file_sente")
        val goteIdx = cursor.getColumnIndex("file_gote")
        for (i in 0 until cursor.count){
            dataList.add(Kif().apply {
                title = cursor.getString(tilteIdx)
                tournament = cursor.getString(tournamentIdx)
                date = cursor.getString(dateIdx)
                sente = cursor.getString(senteIdx)
                gote = cursor.getString(goteIdx)
            })
            cursor.moveToNext()
        }
        cursor.close()

        val adapter = MyAdapter(fa, dataList, tabId, db)
        listview.adapter = adapter

        return root
    }
}