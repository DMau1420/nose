package com.example.a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a.databinding.ActivityQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Bitmap
import com.google.gson.Gson


class QrCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrCodeBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val alarmas = db.getAllAlarms()
        val alarmasJson = Gson().toJson(alarmas)

        try {
            val bitmap = generateQrCode(alarmasJson)
            binding.imageViewQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateQrCode(text: String): Bitmap {
        val size = 512 // Tamaño del código QR
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }
}