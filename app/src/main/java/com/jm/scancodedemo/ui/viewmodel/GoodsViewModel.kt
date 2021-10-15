package com.jm.scancodedemo.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jm.scancodedemo.db.AppDatabase
import com.jm.scancodedemo.db.Goods
import com.jm.scancodedemo.ui.repository.GoodsItemRepository
import com.jm.scancodedemo.utlis.JsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.coroutines.CoroutineContext

class GoodsViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    var goodsListLiveData: MediatorLiveData<List<Goods>> = MediatorLiveData()

    private val repository: GoodsItemRepository =
        GoodsItemRepository(AppDatabase.getDatabase(application).goodsItemDao())

    var totalPriceStrLiveData: MediatorLiveData<String> = MediatorLiveData()
    var totalPrice = BigDecimal.valueOf(0)
    var totalGoodsAmountLiveData: MediatorLiveData<Int> = MediatorLiveData()
    val mContext: Context = application

    init {
        goodsListLiveData.addSource(repository.allShopCartGoods) {
            totalPrice = BigDecimal.valueOf(0)
            if (it.isEmpty()) {
                totalGoodsAmountLiveData.value = 0
                totalPriceStrLiveData.value = "Empty Cart"
            } else {
                var quantityTotal = 0
                it?.forEach {
                    totalPrice = totalPrice.add(it.totalBigDecimal())
                    quantityTotal += it.quantity
                }
                totalGoodsAmountLiveData.value = quantityTotal
                totalPriceStrLiveData.value = "${totalPrice.setScale(2, RoundingMode.HALF_UP)}"
            }
            goodsListLiveData.value = it
        }
    }

    private fun insert(movie: Goods) = scope.launch(Dispatchers.IO) {
        repository.insert(movie)
    }

    private fun deleteAll() = scope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun addQuantity(id: String) = scope.launch(Dispatchers.IO) {
        repository.addQuantity(id)
    }

    fun reduceQuantity(id: String) = scope.launch(Dispatchers.IO) {
        repository.reduceQuantity(id)
    }

    fun emptyShopCart() = scope.launch(Dispatchers.IO) {
        repository.emptyShopCart()
    }


    fun initData() = scope.launch(Dispatchers.IO) {
        if (repository.tableCount() == 0L) {
            getGoodsItemList().let {
                it.forEach {
                    insert(it)
                }
            }
        }
    }

    private fun getGoodsItemList(): MutableList<Goods> {
        JsonUtils.getJson("test.json", mContext).let {
            return Gson().fromJson(it, object : TypeToken<List<Goods>>() {}.type)
        }
    }

}