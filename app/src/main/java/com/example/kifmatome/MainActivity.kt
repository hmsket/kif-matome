package com.example.kifmatome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // when (item.itemId) {
        //     R.id.add_tab -> 処理
        //     R.id.add_kif ->
        //     R.id.delete_tab ->
        //     R.id.delete_tab ->
        //     R.id.sort_tab ->
        //   R.id.sort_tab ->
        // }
        return true
    }
}
