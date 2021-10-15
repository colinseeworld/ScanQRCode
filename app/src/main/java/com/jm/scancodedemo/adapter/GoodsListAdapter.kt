package com.jm.scancodedemo.adapter


import com.jm.scancodedemo.R
import com.jm.scancodedemo.databinding.ItemGoodsBinding
import com.jm.scancodedemo.db.Goods

class GoodsListAdapter : BaseDBAdapter<Goods, ItemGoodsBinding>(R.layout.item_goods) {
    init {
        addChildClickViewIds(R.id.iv_add)
        addChildClickViewIds(R.id.iv_reduce)
    }
    override fun convert(
        holder: BaseDBViewHolder<ItemGoodsBinding>,
        type: Int,
        item: Goods,
        position: Int
    ) {
        holder.binding.data = item
    }
}