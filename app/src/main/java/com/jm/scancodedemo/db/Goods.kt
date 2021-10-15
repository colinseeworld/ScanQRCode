package com.jm.scancodedemo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "goods_table")
data class Goods(
    @PrimaryKey(autoGenerate = false) var id: String,
    var qrUrl: String,
    var thumbnail: String,
    var name: String,
    var price: String,
    var quantity: Int=0,
){
    fun totalBigDecimal():BigDecimal{
        return price.replace("$","").toBigDecimal().multiply(BigDecimal(quantity))
    }
}
