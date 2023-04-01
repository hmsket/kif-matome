package com.example.kifmatome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(fa: FragmentActivity, tabList: MutableList<String>): FragmentStateAdapter(fa){
    private val tabList: MutableList<String> = tabList
    private val fa = fa

    override fun createFragment(position: Int): Fragment {
        return TabFragment(fa, position)
    }

    override fun getItemCount(): Int {
        return this.tabList.size
    }
}