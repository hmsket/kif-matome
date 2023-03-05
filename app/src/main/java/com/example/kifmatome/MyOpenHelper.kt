package com.example.kifmatome

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            SQL_CREATE_TAB
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(
            SQL_DELETE_TAB
        )
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "kif_matome.db"
        const val SQL_CREATE_TAB = "CREATE TABLE tab (_id INTEGER PRIMARY KEY, tab_name TEXT, tab_order INTEGER)"
        const val SQL_DELETE_TAB = "DROP TABLE IF EXISTS tab"
    }
}
