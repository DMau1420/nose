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
    private var myAppSocket: Socket? = null
    companion object {
        var wifiModule = ""
        var wifiModulePort = 0
        var CMD = "0"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val raspid = intent?.getStringExtra("raspid") ?: "192.168.9.241:5000"
        val id = intent?.getIntExtra("id", 0)

        if (id != null) {
            Toast.makeText(context, "Conchetumare el id: $id", Toast.LENGTH_SHORT).show()
            flashlightManager = FlashlightManager(context!!)
            flashlightManager?.startFlashing()

            handler.postDelayed({
                flashlightManager?.stopFlashing()
            }, 5000) // Parpadea durante 5 segundos
            when (id) {
                1 -> {Toast.makeText(context, "Alarma $id sonando 1", Toast.LENGTH_SHORT).show()
                    repeat(0) {
                    getIPandPort(raspid)
                    CMD = "on"
                    val cmdIncreaseServo = SocketAsyncTask()
                    cmdIncreaseServo.execute()
                    }
                }
                2 -> {Toast.makeText(context, "Alarma $id sonando 2", Toast.LENGTH_SHORT).show()
                    repeat(1) {
                        getIPandPort(raspid)
                        CMD = "on"
                        val cmdIncreaseServo = SocketAsyncTask()
                        cmdIncreaseServo.execute()
                    }
                }
                3 -> {Toast.makeText(context, "Alarma $id sonando 3", Toast.LENGTH_SHORT).show()
                    repeat(2) {
                        getIPandPort(raspid)
                        CMD = "on"
                        val cmdIncreaseServo = SocketAsyncTask()
                        cmdIncreaseServo.execute()
                    }
                }
            }
        }
    }
    private fun getIPandPort(raspid: String) {
        Log.d("MYTEST", "IP String $raspid")
        val temp = raspid.split(":")
        wifiModule = temp[0]
        wifiModulePort = temp[1].toInt()
        Log.d("MY TEST", "IP $wifiModule")
        Log.d("MY TEST", "PORT $wifiModulePort")
    }

    private class SocketAsyncTask : AsyncTask<Void, Void, Void>() {
        private var socket: Socket? = null

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val inetAddress = InetAddress.getByName(wifiModule)
                socket = Socket(inetAddress, wifiModulePort)
                Log.d("MYTEST", "Connected to $wifiModule:$wifiModulePort")
                val dataOutputStream = DataOutputStream(socket!!.getOutputStream())
                dataOutputStream.writeBytes(CMD)
                Log.d("MYTEST", "Sent command: $CMD")
                dataOutputStream.close()
                socket!!.close()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                Log.e("MYTEST", "UnknownHostException: ${e.message}")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("MYTEST", "IOException: ${e.message}")
            }
            return null
        }
    }
}

