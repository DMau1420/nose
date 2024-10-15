package com.example.a

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(context: Context, alarma: Alarma) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (i in alarma.diasSemana.indices) {
            if (alarma.diasSemana[i]) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("id", alarma.id)
                    putExtra("etiqueta", alarma.etiqueta)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, alarma.id * 10 + i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, alarma.hora / 60)
                    set(Calendar.MINUTE, alarma.hora % 60)
                    set(Calendar.SECOND, 0)
                    set(Calendar.DAY_OF_WEEK, i + 2) // Calendar.DAY_OF_WEEK starts on Sunday (1), so offset by 2
                    if (before(Calendar.getInstance())) {
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            }
        }
    }
    fun cancelAlarm(context: Context, alarma: Alarma) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        for (i in alarma.diasSemana.indices) {
            if (alarma.diasSemana[i]) {
                val pendingIntent = PendingIntent.getBroadcast(
                    context, alarma.id * 10 + i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}