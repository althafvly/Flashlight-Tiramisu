package com.pdb82.flashlighttiramisu

import android.content.Context
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import com.ss.svs.SegmentedVerticalSeekBar

class MainActivity : AppCompatActivity() {

    private val cameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seekBar = findViewById<SegmentedVerticalSeekBar>(R.id.seekBar)
        val errorText = findViewById<TextView>(R.id.textView)
        val card = findViewById<CardView>(R.id.card)

        val list = cameraManager.cameraIdList
        val outCameraId = list[0]
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(outCameraId)

        val torchMaxLevel =
            cameraCharacteristics[CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL] ?: -1

        if (torchMaxLevel > 1) {
            seekBar.max = torchMaxLevel
            seekBar.setOnBoxedPointsChangeListener(object :
                SegmentedVerticalSeekBar.OnValuesChangeListener {
                override fun onProgressChanged(
                    p0: SegmentedVerticalSeekBar?, p1: Int
                ) {
                    if (p1 == 0) {
                        cameraManager.setTorchMode(outCameraId, false)
                    } else {
                        cameraManager.turnOnTorchWithStrengthLevel(outCameraId, p1)
                    }
                }

                override fun onStartTrackingTouch(p0: SegmentedVerticalSeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SegmentedVerticalSeekBar?) {
                }

            })
        } else {
            errorText.visibility = View.VISIBLE
            card.visibility = View.VISIBLE
            errorText.text = getString(R.string.device_not_supported)
            seekBar.isEnabled = false
        }
    }
}
