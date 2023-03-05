package com.example.kifmatome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    lateinit var db: AppDatabase
    lateinit var tabDao: TabDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // データベース
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "kif_matome.db"
        ).build()
        tabDao = db.tabDao()

        tabDao.getAllTabName().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            val tabList: MutableList<String> = mutableListOf<String>()
            tabList.addAll(it)

            /* TODO:
             * 「棋譜を追加」時に，データベースからレコードを取得しようとするが，データベースが空だとうまくいかない．
             * そのため，データベースが空だと勝手に"四間飛車"を登録するというその場しのぎの荒業 // タブの見本ができていいかも...
             * データベースが空でもうまくいくようにする．
             */
            if(it.size == 0){
                val tab = MyTab(0,"四間飛車", 0)
                Completable.fromAction({tabDao.insertAll(tab)}).subscribeOn(Schedulers.io()).subscribe()
            }

            // タブの移動をスワイプで行えるようにする
            var viewPager: ViewPager2 = findViewById(R.id.pager)
            val pagerAdapter = PageAdapter(this, tabList)
            viewPager.adapter = pagerAdapter

            // タブを作成する
            val tabLayout: TabLayout = findViewById(R.id.tab_layout)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             R.id.add_tab -> {
                 val editText = EditText(this)
                 editText.setHint("タブ名を入力してください")
                 AlertDialog.Builder(this)
                     .setTitle("タブを追加")
                     .setView(editText)
                     .setPositiveButton("OK", { dialog, which ->
                         Completable.fromAction({ tabDao.getLastTabOrder() }).subscribeOn(Schedulers.io()).subscribe()
//                         tabDao.getLastTabOrder().subscribeOn(Schedulers.io())./*observeOn(AndroidSchedulers.mainThread()).*/subscribe(
//                             {
//                                 val newTabOrder = it + 1
//                                 Log.i("debug", "ここまで" + newTabOrder)
//                                 val tabName = editText.text.toString()
//                                 val tab = MyTab(0, tabName, newTabOrder) // データベースに登録する時点では，idとorderは同じ
//                                   Completable.fromAction({ tabDao.insertAll(tab) })
//                                     .subscribeOn(Schedulers.io()).subscribe()
//                             })
                         var lastTabOrder: Int
                         tabDao.getLastTabOrder().subscribeOn(Schedulers.io()).subscribe({
                             val tab = MyTab(0, editText.text.toString(), it+1) // データベースに登録する時点では，idとorderは同じ
                             Completable.fromAction({ tabDao.insertAll(tab) }).subscribeOn(Schedulers.io()).subscribe()
                         })
                     })
                     .setNegativeButton("キャンセル", null)
                     .show()
             }
//             R.id.add_kif ->
//             R.id.delete_tab ->
//             R.id.delete_tab ->
//             R.id.sort_tab ->
//             R.id.sort_tab ->
         }
        return true
    }
}
