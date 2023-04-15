package com.example.kifmatome

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(context: Context, list: ArrayList<Kif>, tabId: Int, db: SQLiteDatabase) : ArrayAdapter<Kif>(context, 0, list) {

    val tabId = tabId
    val db = db

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

        view?.setOnClickListener{
            var sql = "SELECT file_id FROM file WHERE tab_id = " + tabId + " ORDER BY file_order ASC LIMIT 1 OFFSET " + position
            var cursor = db.rawQuery(sql, null)
            cursor.moveToFirst()
            val fileId = cursor.getInt(0)
            sql = "SELECT file_path FROM file WHERE tab_id = " + tabId + " AND file_id = " + fileId
            cursor = db.rawQuery(sql, null)
            cursor.moveToFirst()
            val filePath = cursor.getString(0)
            cursor.close()

            // 棋譜再生画面への遷移
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("filePath", filePath);
            context.startActivity(intent)
        }

        return view!!
    }
}