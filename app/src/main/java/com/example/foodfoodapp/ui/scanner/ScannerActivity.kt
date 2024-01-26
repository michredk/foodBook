package com.example.foodfoodapp.ui.scanner

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodfoodapp.databinding.ActivityScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {
    private lateinit var activityScannerBinding: ActivityScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityScannerBinding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(activityScannerBinding.root)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}
