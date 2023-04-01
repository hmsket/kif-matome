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
        pagerAdapter = PageAdapter(this, db, tabList)
        viewPager.adapter = pagerAdapter

        // タブを作成し，表示する
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }

    fun getMaxFileOrder(tab_id: Int): Int{
        val sql = "SELECT MAX(file_order) FROM file WHERE tab_id = " + tab_id
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val maxFileOrder = cursor.getInt(0)
        cursor.close()
        return maxFileOrder
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

                    val view = this.layoutInflater.inflate(R.layout.add_kif_form, null)

                    view.findViewById<EditText>(R.id.add_kif_tournament).setText(gameInfo.gameName)
                    view.findViewById<EditText>(R.id.add_kif_date).setText(gameInfo.date)
                    view.findViewById<EditText>(R.id.add_kif_sente).setText(gameInfo.senteName)
                    view.findViewById<EditText>(R.id.add_kif_gote).setText(gameInfo.goteName)

                    AlertDialog.Builder(this)
                        .setTitle("棋譜を追加")
                        .setView(view)
                        .setPositiveButton("OK", { dialog, which ->
                            val pos = tabLayout.selectedTabPosition
                            // tab_orderを昇順に並べて，pos番目の_idを取得
                            val sql = "SELECT _id FROM tab ORDER BY tab_order ASC LIMIT 1 OFFSET " + pos
                            val cursor = db.rawQuery(sql, null)
                            cursor.moveToFirst()
                            val tabId = cursor.getInt(0)
                            cursor.close()

                            val fileId = getMaxFileOrder(tabId)+1
                            val filePic = ""
                            val fileTitle = view.findViewById<EditText>(R.id.add_kif_title).text.toString()
                            val filePath = uri.toString()
                            val fileTournament = view.findViewById<EditText>(R.id.add_kif_tournament).text.toString()
                            val fileDate = view.findViewById<EditText>(R.id.add_kif_date).text.toString()
                            val fileSente = view.findViewById<EditText>(R.id.add_kif_sente).text.toString()
                            val fileGote = view.findViewById<EditText>(R.id.add_kif_gote).text.toString()
                            val fileOrder = fileId

                            // DBに登録
                            val values = ContentValues()
                            values.put("tab_id", tabId)
                            values.put("file_id", fileId)
                            values.put("file_pic", filePic)
                            values.put("file_title", fileTitle)
                            values.put("file_path", filePath)
                            values.put("file_tournament", fileTournament)
                            values.put("file_date", fileDate)
                            values.put("file_sente", fileSente)
                            values.put("file_gote", fileGote)
                            values.put("file_order", fileOrder)
                            db.insert("file", null, values)
                        })
                        .setNegativeButton("キャンセル", null)
                        .show()
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
