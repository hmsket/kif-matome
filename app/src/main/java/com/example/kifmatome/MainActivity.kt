package com.example.kifmatome

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
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
             R.id.delete_tab -> deleteTab()
             R.id.delete_kif -> deleteKif()
             R.id.edit_tab -> editTab()
             R.id.edit_kif -> editKif()
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

            // URIへのアクセス権限を永続化する
            // 再起動すると権限が消えるらしい？
            val contentResolver = applicationContext.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            }

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

                            // 棋譜を追加後，ListViewを更新する
                            viewPager.adapter = pagerAdapter
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

    fun getTabIdFromDB(pos: Int): Int{
        val sql = "SELECT _id FROM tab ORDER BY tab_order ASC LIMIT 1 OFFSET " + pos
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val tabId = cursor.getInt(0)
        cursor.close()
        return tabId
    }

    fun deleteTabFromDB(deleteTabId: Int){
        val sql = "DELETE FROM tab WHERE _id = " + deleteTabId
        db.execSQL(sql)
    }

    fun deleteFileIfTabIdFromDB(deleteTabId: Int){
        val sql = "DELETE FROM file WHERE tab_id = " + deleteTabId
        db.execSQL(sql)
    }

    fun deleteTab(){
        val deleteTabList = readTabFromDB()
        val deleteAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deleteTabList)
        val deleteListView = ListView(this)
        deleteListView.adapter = deleteAdapter

        deleteListView.setOnItemClickListener { adapterView, view, i, l ->
            val deleteTabName = deleteAdapter.getItem(i)
            AlertDialog.Builder(this)
                .setTitle("「" + deleteTabName + "」を削除")
                .setMessage("注意：削除したら元に戻せません")
                .setPositiveButton("削除する", { dialog, which ->
                    val deleteTabId = getTabIdFromDB(i)
                    deleteTabFromDB(deleteTabId)
                    deleteFileIfTabIdFromDB(deleteTabId)
                    deleteAdapter.remove(deleteTabName)
                    deleteAdapter.notifyDataSetChanged()
                })
                .setNegativeButton("キャンセル", null)
                .show()
        }

        AlertDialog.Builder(this)
            .setView(deleteListView)
            .setCancelable(false)
            .setPositiveButton("OK", { dialog, which ->
                setTab()
            })
            .show()
    }

    fun getKifIdFromDB(tabId: Int, pos: Int): Int{
        val sql = "SELECT file_id FROM file WHERE tab_id = " + tabId + " ORDER BY file_order ASC LIMIT 1 OFFSET " + pos
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        val kifId = cursor.getInt(0)
        cursor.close()
        return kifId
    }

    fun updateKifListview(listview: ListView, tabId: Int){
        val dataList = arrayListOf<Kif>()
        val sql = "SELECT * FROM file WHERE tab_id = " + tabId + " ORDER BY file_order ASC"
        val cursor = db.rawQuery(sql, null)
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

        val listviewAdapter = MyAdapter(this, dataList, tabId, db)
        listview.adapter = listviewAdapter
    }

    fun deleteKifFromDB(deleteTabId: Int, deleteKifId: Int){
        val sql = "DELETE FROM file WHERE tab_id = " + deleteTabId + " AND file_id = " + deleteKifId
        db.execSQL(sql)
    }

    fun deleteKif(){
        val deleteKifDialogView = this.layoutInflater.inflate(R.layout.delete_kif_form, null)

        val tabList = readTabFromDB()

        val spinnerView = deleteKifDialogView.findViewById<Spinner>(R.id.delete_kif_spinner)
        val spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, tabList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerView.adapter = spinnerAdapter

        val deleteListView = deleteKifDialogView.findViewById<ListView>(R.id.delete_kif_listview)

        var tabId = getTabIdFromDB(0)
        updateKifListview(deleteListView, tabId)

        deleteListView.setOnItemClickListener { adapterView, view, i, l ->
            AlertDialog.Builder(this)
                .setMessage("注意：削除したら元に戻せません")
                .setPositiveButton("削除する", { dialog, which ->
                    val deleteTabId = tabId
                    val deleteKifId = getKifIdFromDB(deleteTabId, i)
                    deleteKifFromDB(deleteTabId, deleteKifId)
                    updateKifListview(deleteListView, tabId)
                })
                .setNegativeButton("キャンセル", null)
                .show()
        }

        spinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                tabId = getTabIdFromDB(pos)
                updateKifListview(deleteListView, tabId)
            }
            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }
        }

        AlertDialog.Builder(this)
            .setTitle("棋譜を削除")
            .setView(deleteKifDialogView)
            .setCancelable(false)
            .setPositiveButton("OK", { dialog, which ->
                // 棋譜ListViewを更新する
                viewPager.adapter = pagerAdapter
            })
            .show()
    }

    fun editTabFromDB(tabName: String, editTabId: Int){
        val sql = "UPDATE tab SET tab_name = '" + tabName + "' WHERE _id = " + editTabId
        db.execSQL(sql)
    }

    fun editTab(){
        var editTabList = readTabFromDB()
        var editAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, editTabList)
        val editListView = ListView(this)
        editListView.adapter = editAdapter

        editListView.setOnItemClickListener { adapterView, view, i, l ->
            val editTabName = editAdapter.getItem(i)
            val editText = EditText(this)
            editText.setText(editTabName)
            AlertDialog.Builder(this)
                .setTitle("編集")
                .setView(editText)
                .setPositiveButton("OK", { dialog, which ->
                    val newTabName = editText.text.toString()
                    val editTabId = getTabIdFromDB(i)
                    editTabFromDB(newTabName, editTabId)
                    editTabList = readTabFromDB()
                    editAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, editTabList)
                    editListView.adapter = editAdapter
                })
                .setNegativeButton("キャンセル", null)
                .show()
        }

        AlertDialog.Builder(this)
            .setView(editListView)
            .setCancelable(false)
            .setPositiveButton("OK", { dialog, which ->
                setTab()
            })
            .show()
    }

    fun getKifInfoFromDB(tabId: Int, kifId: Int): Kif{
        val kifInfo = Kif()

        val sql = "SELECT * FROM file WHERE tab_id = " + tabId + " AND file_id = " + kifId
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()

        val tilteIdx = cursor.getColumnIndex("file_title")
        val tournamentIdx = cursor.getColumnIndex("file_tournament")
        val dateIdx = cursor.getColumnIndex("file_date")
        val senteIdx = cursor.getColumnIndex("file_sente")
        val goteIdx = cursor.getColumnIndex("file_gote")

        kifInfo.title = cursor.getString(tilteIdx)
        kifInfo.tournament = cursor.getString(tournamentIdx)
        kifInfo.date = cursor.getString(dateIdx)
        kifInfo.sente = cursor.getString(senteIdx)
        kifInfo.gote = cursor.getString(goteIdx)

        cursor.close()

        return kifInfo
    }

    fun editKif(){
        // R.layout.delete_kif_formを再利用する
        val editKifDialogView = this.layoutInflater.inflate(R.layout.delete_kif_form, null)

        val tabList = readTabFromDB()

        val spinnerView = editKifDialogView.findViewById<Spinner>(R.id.delete_kif_spinner)
        val spinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, tabList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerView.adapter = spinnerAdapter

        val editListView = editKifDialogView.findViewById<ListView>(R.id.delete_kif_listview)

        var tabId = getTabIdFromDB(0)
        updateKifListview(editListView, tabId)

        editListView.setOnItemClickListener { adapterView, _, i, l ->
            // DBから棋譜情報を取得
            val editTabId = tabId
            val editKifId = getKifIdFromDB(editTabId, i)
            val kifInfo = getKifInfoFromDB(editTabId, editKifId)

            // R.layout.add_kif_formを再利用する
            val view = this.layoutInflater.inflate(R.layout.add_kif_form, null)

            view.findViewById<EditText>(R.id.add_kif_title).setText(kifInfo.title)
            view.findViewById<EditText>(R.id.add_kif_tournament).setText(kifInfo.tournament)
            view.findViewById<EditText>(R.id.add_kif_date).setText(kifInfo.date)
            view.findViewById<EditText>(R.id.add_kif_sente).setText(kifInfo.sente)
            view.findViewById<EditText>(R.id.add_kif_gote).setText(kifInfo.gote)

            AlertDialog.Builder(this)
                .setTitle("棋譜情報を編集")
                .setView(view)
                .setPositiveButton("OK", { dialog, which ->
                    val fileTitle = view.findViewById<EditText>(R.id.add_kif_title).text.toString()
                    val fileTournament = view.findViewById<EditText>(R.id.add_kif_tournament).text.toString()
                    val fileDate = view.findViewById<EditText>(R.id.add_kif_date).text.toString()
                    val fileSente = view.findViewById<EditText>(R.id.add_kif_sente).text.toString()
                    val fileGote = view.findViewById<EditText>(R.id.add_kif_gote).text.toString()

                    val values = ContentValues()
                    values.put("file_title", fileTitle)
                    values.put("file_tournament", fileTournament)
                    values.put("file_date", fileDate)
                    values.put("file_sente", fileSente)
                    values.put("file_gote", fileGote)
                    db.update("file", values, "tab_id = " + editTabId + " AND file_id = " + editKifId, null)

                    updateKifListview(editListView, tabId)
                })
                .setNegativeButton("キャンセル", null)
                .show()
        }

        spinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                tabId = getTabIdFromDB(pos)
                updateKifListview(editListView, tabId)
            }
            override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

            }
        }

        AlertDialog.Builder(this)
            .setTitle("棋譜を削除")
            .setView(editKifDialogView)
            .setCancelable(false)
            .setPositiveButton("OK", { dialog, which ->
                // 棋譜ListViewを更新する
                viewPager.adapter = pagerAdapter
            })
            .show()
    }
}
