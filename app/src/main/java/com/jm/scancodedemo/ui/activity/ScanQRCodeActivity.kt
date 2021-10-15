package com.jm.scancodedemo.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import com.jm.scancodedemo.databinding.ActivityScanQrCodeBinding
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScanQRCodeActivity :
    BaseDBActivity<ActivityScanQrCodeBinding>(com.jm.scancodedemo.R.layout.activity_scan_qr_code),
    DecoratedBarcodeView.TorchListener {
    lateinit var capture: CaptureManager
    var isLightOn = false

    override fun initView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        binding.dbv.setTorchListener(this)
        capture = CaptureManager(this, binding.dbv)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()
        binding.llFlash.setOnClickListener {
            if (isLightOn) {
                binding.dbv.setTorchOff()
            } else {
                binding.dbv.setTorchOn()
            }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }


    override fun onPause() {
        super.onPause()
        capture.onPause()
    }


    override fun onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.dbv.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    override fun onTorchOn() {
        isLightOn = true
        binding.ivFlash.isActivated = true
    }

    override fun onTorchOff() {
        isLightOn = false
        binding.ivFlash.isActivated = false
    }
}