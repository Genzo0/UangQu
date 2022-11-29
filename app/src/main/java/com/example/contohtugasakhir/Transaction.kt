package com.example.contohtugasakhir

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val label:String,
    val amount: Long,
    val description: String,
    val date: String
    ) : java.io.Serializable{
}