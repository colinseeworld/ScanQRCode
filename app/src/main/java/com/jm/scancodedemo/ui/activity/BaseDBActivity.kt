package com.jm.scancodedemo.ui.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseDBActivity<DB : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    AppCompatActivity() {
    lateinit var binding: DB

    protected inline fun <reified DB : ViewDataBinding> binding(
        @LayoutRes resId: Int
    ): Lazy<DB> = lazy {
        DataBindingUtil.setContentView<DB>(this, resId).apply {
            lifecycleOwner = this@BaseDBActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.apply {
            lifecycleOwner = this@BaseDBActivity
        }
        setContentView(binding.root)
        initView(savedInstanceState)
        initData()
    }
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
}