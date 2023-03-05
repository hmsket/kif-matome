package com.example.kifmatome

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MyTab::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tabDao(): TabDao
}