package com.example.kifmatome

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TabDao {
    @Query("SELECT * FROM tab")
    fun getAll(): Flowable<List<MyTab>>

    @Query("SELECT tab_name FROM tab ORDER BY tab_order ASC")
    fun getAllTabName(): Flowable<List<String>>

    @Query("SELECT max(tab_order) FROM tab")
    fun getLastTabOrder(): Flowable<Int>

    @Insert
    fun insertAll(vararg tabs: MyTab)

    @Delete
    fun delete(tab: MyTab)
}