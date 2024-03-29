package com.example.task.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.example.task.databinding.ActivityScanBinding
import com.example.task.ui.models.reqmodel.EventModel
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private val authVM: AuthViewModel by viewModels()
    private val bus : EventBus = EventBus.getDefault()

    private val reqPerLunch =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamara()
            } else {
                showToast("Need Permission To Continue")
            }
        }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result : ScanIntentResult ->
        run {
            if (result.contents == null) {
                showToast("Cancelled")
            }  else {
                setResults(result.contents)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCamPermission()
    }

    private fun checkCamPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamara()
        } else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showToast("Camera Permission Required")
        } else {
            reqPerLunch.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setResults(contents: String) {
        val eventModel = EventModel()
        eventModel.id = contents
        bus.postSticky(eventModel)
        binding.count.text = contents
        Handler(Looper.getMainLooper()).postDelayed({finish()},2000)
    }

    private fun showCamara() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        options.setBarcodeImageEnabled(true)

        scanLauncher.launch(options)
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(this, msg)
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
}