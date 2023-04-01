package com.example.kifmatome

import android.database.sqlite.SQLiteDatabase
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(fa: FragmentActivity, db: SQLiteDatabase, tabList: MutableList<String>): FragmentStateAdapter(fa){
    private val tabList: MutableList<String> = tabList
    private val fa = fa
    private val db = db

    override fun createFragment(position: Int): Fragment {
        return TabFragment(fa, db, position)
    }

    override fun getItemCount(): Int {
        return this.tabList.size
    }
}