package com.example.kifmatome

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab")
data class MyTab(
    @PrimaryKey(autoGenerate = true) val tid: Int,
    @ColumnInfo(name = "tab_name") val tabName: String?,
    @ColumnInfo(name = "tab_order") val tabOrder: Int
)
