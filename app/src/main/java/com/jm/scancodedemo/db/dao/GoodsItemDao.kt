package com.jm.scancodedemo.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jm.scancodedemo.db.Goods

@Dao
interface GoodsItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: Goods)

    @Query("DELETE FROM goods_table")
    fun deleteAll()

    @Query("SELECT * FROM goods_table")
    fun getAllList(): LiveData<List<Goods>>

    @Query("SELECT * FROM goods_table WHERE quantity >0")
    fun getShopCartList(): LiveData<List<Goods>>

    @Query("UPDATE goods_table SET quantity =quantity+1 WHERE id = :id;")
    fun addQuantity(id: String): Int

    @Query("UPDATE goods_table SET quantity =quantity-1 WHERE id = :id;")
    fun reduceQuantity(id: String): Int

    @Query("UPDATE goods_table SET quantity = 0")
    fun emptyShopCart(): Int

    @Query("select count(id) from goods_table;")
    fun getTableCount(): Long

}