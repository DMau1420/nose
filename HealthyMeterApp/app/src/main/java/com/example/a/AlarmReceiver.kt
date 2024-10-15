package com.example.a

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.os.Handler
import java.net.Socket
import android.os.AsyncTask
import android.util.Log
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException

class AlarmReceiver : BroadcastReceiver() {
    private var flashlightManager: FlashlightManager? = null
    private val handler = Handler()

    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent?.getIntExtra("id", -1)

        if (id != null) {
            Toast.makeText(context, "Alarma recibida: $id", Toast.LENGTH_SHORT).show()
            flashlightManager = FlashlightManager(context!!)
            flashlightManager?.startFlashing()
            handler.postDelayed({
                flashlightManager?.stopFlashing()
            }, 5000) // Parpadea durante 5 segundos
            // Repetir la acción según el valor de "id"
            // Enviar el Toast según el id, repetido id + 1 veces
            val repeatCount = id
            for (i in 1..repeatCount) {
                when (id) {
                    1 -> {
                        Toast.makeText(context, "Alarma $id sonando SERVO 1, repetición $i", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        Toast.makeText(context, "Alarma $id sonando SERVO 2, repetición $i", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        Toast.makeText(context, "Alarma $id sonando SERVO 3, repetición $i", Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(context, "Alarma $id sonando, repetición $i", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}

