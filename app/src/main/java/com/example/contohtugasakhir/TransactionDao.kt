package com.example.contohtugasakhir

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Query("select * from transactions order by id desc")
    fun getAll(): List<Transaction>

    @Insert
    fun insert(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE label LIKE :label")
    fun findTransaction(label: String): List<Transaction>
}