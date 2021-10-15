package com.jm.scancodedemo.ui.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.jm.scancodedemo.db.Goods
import com.jm.scancodedemo.db.dao.GoodsItemDao

class GoodsItemRepository(private val dao: GoodsItemDao) {
    val allShopCartGoods: LiveData<List<Goods>> = dao.getShopCartList()

    @WorkerThread
    fun insert(movie: Goods) {
        dao.insert(movie)
    }

    @WorkerThread
    fun deleteAll() {
        dao.deleteAll()
    }

    @WorkerThread
    fun addQuantity(id: String) {
        dao.addQuantity(id)
    }
    @WorkerThread
    fun reduceQuantity(id: String) {
        dao.reduceQuantity(id)
    }

    @WorkerThread
    fun emptyShopCart() {
        dao.emptyShopCart()
    }
    @WorkerThread
    fun tableCount():Long {
        return dao.getTableCount()
    }
}