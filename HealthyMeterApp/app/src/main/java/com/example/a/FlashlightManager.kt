package com.example.a

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper


class FlashlightManager(private val context: Context) {
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isFlashing = false

    init {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun startFlashing() {
        if (isFlashing) return
        isFlashing = true
        flash()
    }

    fun stopFlashing() {
        isFlashing = false
        handler.removeCallbacksAndMessages(null)
        setFlashlight(false)
    }

    private fun flash() {
        if (!isFlashing) return
        setFlashlight(true)
        handler.postDelayed({
            setFlashlight(false)
            handler.postDelayed({
                flash()
            }, 300) // Tiempo de apagado
        }, 300) // Tiempo de encendido
    }

    private fun setFlashlight(state: Boolean) {
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, state)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}