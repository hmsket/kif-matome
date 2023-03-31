package com.example.kifmatome

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.nio.charset.Charset

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
             R.id.add_kif -> addKif()
        //     R.id.delete_tab ->
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

    fun addKif(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            val inputStream = uri?.let { contentResolver.openInputStream(it) }
            if (inputStream != null) {
                try {
                    val lines = inputStream.bufferedReader(Charset.forName("SJIS")).use { it.readLines() }
                    val parser = Parser()
                    val gameInfo: GameInfo = parser.parse(lines)
                }catch(e:Exception){
                    // 読み込みエラー
                    AlertDialog.Builder(this)
                        .setTitle("エラー")
                        .setMessage("このファイルは読み込むことができませんでした")
                        .setPositiveButton("OK", { dialog, which ->
                        })
                        .show()
                }
            }
        }
    }
}
