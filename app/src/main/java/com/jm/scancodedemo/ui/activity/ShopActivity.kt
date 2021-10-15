package com.jm.scancodedemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.zxing.integration.android.IntentIntegrator
import com.jm.scancodedemo.R
import com.jm.scancodedemo.adapter.GoodsListAdapter
import com.jm.scancodedemo.databinding.ActivityShopBinding
import com.jm.scancodedemo.ui.viewmodel.GoodsViewModel

class ShopActivity : BaseDBActivity<ActivityShopBinding>(R.layout.activity_shop) {
    private val viewModel by viewModels<GoodsViewModel>()
    override fun initView(savedInstanceState: Bundle?) {
        binding.model = viewModel
        initAdapter()
        binding.fabAdd.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.captureActivity = ScanQRCodeActivity::class.java
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan QR Code")
            integrator.setCameraId(0) // Use a specific camera of the device
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }
        binding.btnSubmit.setOnClickListener {
            Toast.makeText(this, "${viewModel.totalPrice}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.btn_empty -> viewModel.emptyShopCart()
        }
        return true
    }


    private fun initAdapter() {
        binding.rvGoods.adapter = GoodsListAdapter().apply {
            viewModel.goodsListLiveData.observe(this@ShopActivity) {
                setData(it)
            }
            setItemOnClickListener { view, position, data ->

            }
            setItemChildOnClickListener { view, position, data ->
                when (view.id) {
                    R.id.iv_add -> {
                        viewModel.addQuantity(data.id)
                    }
                    R.id.iv_reduce -> {
                        viewModel.reduceQuantity(data.id)
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.initData()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan failed, please retry", Toast.LENGTH_LONG).show()
            } else {
                viewModel.addQuantity(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}