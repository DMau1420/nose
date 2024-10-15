package com.example.a

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a.databinding.ActivityScanQrCodeBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanQrCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrCodeBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.buttonScanQrCode.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            val alarmas: List<Alarma> = Gson().fromJson(result.contents, Array<Alarma>::class.java).toList()
            alarmas.forEach { db.agregarAlarma(it) }
            Toast.makeText(this, "Alarmas añadidas correctamente", Toast.LENGTH_SHORT).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}