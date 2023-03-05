package com.example.kifmatome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(fa: FragmentActivity, tabList: MutableList<String>): FragmentStateAdapter(fa){
    private val tabList: MutableList<String> = tabList

    override fun createFragment(position: Int): Fragment {
        return TabFragment()
    }

    override fun getItemCount(): Int {
        return this.tabList.size
    }
}