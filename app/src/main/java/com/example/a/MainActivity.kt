package com.example.a


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a.databinding.ActivityMainBinding
import android.Manifest

class MainActivity : AppCompatActivity(), AlarmaAdapter.OnItemClickListener, AlarmaAdapter.OnSwitchToggleListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private val CAMERA_PERMISSION_REQUEST_CODE = 101
    private val handler = Handler(Looper.getMainLooper())
    private val clockUpdater = object : Runnable {
        override fun run() {
            binding.textViewClock.text = getCurrentTime()
            handler.postDelayed(this, 0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAlarms.adapter = AlarmaAdapter(db.getAllAlarms(), this, this, this)
        binding.buttonAddAlarm.setOnClickListener {
            if (db.getAllAlarms().size < 3) {
                startActivity(Intent(this, EditarAlarma::class.java))
            } else {
                Toast.makeText(this, "Solo puedes tener un máximo de 3 alarmas", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonZonaHoraria.setOnClickListener {
            startActivity(Intent(this, RelojMundial::class.java))
        }
        binding.buttonGenerateQr.setOnClickListener {
            startActivity(Intent(this, QrCodeActivity::class.java))
        }
        binding.buttonScanQr.setOnClickListener {
            startActivity(Intent(this, ScanQrCodeActivity::class.java))
        }
        checkCameraPermission()
    }
    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(clockUpdater)
        (binding.recyclerViewAlarms.adapter as AlarmaAdapter).updateAlarms(db.getAllAlarms())
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(clockUpdater)
    }
    override fun onItemClick(alarma: Alarma) {
        Intent(this, EditarAlarma::class.java).apply {
            putExtra("ALARMA_ID", alarma.id)
            startActivity(this)
        }
    }
    override fun onSwitchToggle(alarma: Alarma, isChecked: Boolean) {
        alarma.activada = isChecked
        db.updateAlarma(alarma)
        if (isChecked) {
            AlarmScheduler.scheduleAlarm(this, alarma)
            Toast.makeText(this, "Alarma activada", Toast.LENGTH_SHORT).show()
        } else {
            AlarmScheduler.cancelAlarm(this, alarma)
            Toast.makeText(this, "Alarma desactivada", Toast.LENGTH_SHORT).show()
        }
        (binding.recyclerViewAlarms.adapter as AlarmaAdapter).updateAlarms(db.getAllAlarms())
    }
    fun onDeleteClick(alarma: Alarma) {
        db.deleteAlarma(alarma.id)
        AlarmScheduler.cancelAlarm(this, alarma)
        (binding.recyclerViewAlarms.adapter as AlarmaAdapter).updateAlarms(db.getAllAlarms())
        Toast.makeText(this, "Alarma eliminada", Toast.LENGTH_SHORT).show()
    }
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }
}