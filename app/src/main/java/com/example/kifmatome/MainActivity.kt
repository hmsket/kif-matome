package com.example.kifmatome

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    lateinit var pagerAdapter: PageAdapter
    lateinit var tabLayout: TabLayout

    lateinit var helper: MyOpenHelper
    lateinit var db: SQLiteDatabase

    lateinit var tabList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helper = MyOpenHelper(applicationContext)
        db = helper.readableDatabase

        viewPager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tab_layout)

        setTab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             R.id.add_tab -> addTab()
        //     R.id.add_kif ->
             R.id.delete_tab -> deleteTab()
        //     R.id.delete_tab ->
        //     R.id.sort_tab ->
        //   R.id.sort_tab ->
         }
        return true
    }

    fun readTabFromDB(): MutableList<String> {
        val tabList = mutableListOf<String>()

        val cursor = db.query(
            "tab",
            arrayOf("tab_name"),
            null,
            null,
            null,
            null,
            "tab_order"
        )
        cursor.moveToFirst()
        for (i in 0 until cursor.count){
            val tabName = cursor.getString(0)
            tabList.add(tabName)
            cursor.moveToNext()
        }
        cursor.close()
        return tabList
    }

    fun getMaxTabOrder(): Int{
        val cursor = db.query(
            "tab",
            arrayOf("MAX(tab_order)"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val maxTabOrder = cursor.getInt(0)
        cursor.close()
        return maxTabOrder
    }

    fun addTab(){
        val editText = EditText(this)
        editText.setHint("タブ名を入力してください")
        AlertDialog.Builder(this)
            .setTitle("タブを追加")
            .setView(editText)
            .setPositiveButton("OK", { dialog, which ->
                val tabName = editText.text.toString()
                val newTabOrder = getMaxTabOrder() + 1

                val values = ContentValues()
                values.put("tab_name", tabName)
                values.put("tab_order", newTabOrder)
                db.insert("tab", null, values)

                // 追加したタブもスワイプで移動できるようにする
                setTab()
            })
            .setNegativeButton("キャンセル", null)
            .show()
    }

    fun deleteTab(){
        tabList = readTabFromDB()

        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, tabList)
        val tabListView = ListView(this)
        tabListView.adapter = adapter
        tabListView.setOnItemClickListener { adapterView, view, i, l ->
            AlertDialog.Builder(this)
                .setTitle("確認")
                .setMessage("「" + tabList[i] + "」を削除します。\nこの操作は取り消せません。")
                .setPositiveButton("OK", { dialog, which ->
                    deleteTabFromDB(i)
                })
                .setNegativeButton("キャンセル", null)
                .show()
        }

        AlertDialog.Builder(this)
            .setTitle("タブを削除")
            .setView(tabListView)
            .setPositiveButton("閉じる", null)
            .show()
    }

    fun deleteTabFromDB(position: Int){
        val sql: String = "DELETE FROM tab ORDER BY tab_order ASC LIMIT 1 OFFSET " + position.toString() + ";"
        db.execSQL(sql)
    }

    fun setTab(){
        tabList = readTabFromDB()

        // タブの移動をスワイプで行えるようにする
        pagerAdapter = PageAdapter(this, tabList)
        viewPager.adapter = pagerAdapter

        // タブを作成し，表示する
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }
}
