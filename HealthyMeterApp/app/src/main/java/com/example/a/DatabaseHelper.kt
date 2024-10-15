package com.example.a

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "alarmas_db"
        private const val TABLE_ALARMAS = "alarmas"
        private const val KEY_ID = "id"
        private const val KEY_HORA = "hora"
        private const val KEY_DIAS_SEMANA = "dias_semana"
        private const val KEY_ETIQUETA = "etiqueta"
        private const val KEY_ACTIVADA = "activada"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_ALARMAS_TABLE = ("CREATE TABLE $TABLE_ALARMAS("
                + "$KEY_ID INTEGER PRIMARY KEY,"
                + "$KEY_HORA INTEGER,"
                + "$KEY_DIAS_SEMANA TEXT,"
                + "$KEY_ETIQUETA TEXT,"
                + "$KEY_ACTIVADA INTEGER)")
        db.execSQL(CREATE_ALARMAS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALARMAS")
        onCreate(db)
    }
    fun agregarAlarma(alarma: Alarma) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_HORA, alarma.hora)
        values.put(KEY_DIAS_SEMANA, alarma.diasSemana.joinToString(","))
        values.put(KEY_ETIQUETA, alarma.etiqueta)
        values.put(KEY_ACTIVADA, if(alarma.activada) 1 else 0)
        db.insert(TABLE_ALARMAS, null, values)
        db.close()
    }
    @SuppressLint("Range")
    fun getAllAlarms(): List<Alarma> {
        val alarmList = mutableListOf<Alarma>()
        val selectQuery = "SELECT * FROM $TABLE_ALARMAS"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var hora: Int
        var diasSemanaString: String
        var etiqueta: String
        var activada : Boolean
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                hora = cursor.getInt(cursor.getColumnIndex(KEY_HORA))
                diasSemanaString = cursor.getString(cursor.getColumnIndex(KEY_DIAS_SEMANA))
                etiqueta = cursor.getString(cursor.getColumnIndex(KEY_ETIQUETA))
                activada = cursor.getInt(cursor.getColumnIndex(KEY_ACTIVADA)) == 1
                // Convertir la cadena de días de la semana en un BooleanArray
                val diasSemana = diasSemanaString.split(",").map { it == "true" }.toBooleanArray()
                val alarma = Alarma(id, hora, diasSemana, etiqueta, activada)
                alarmList.add(alarma)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return alarmList
    }
    @SuppressLint("Range")
    fun getAlarma(id: Int): Alarma? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ALARMAS,
            null,
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        var alarma: Alarma? = null
        if (cursor.moveToFirst()) {
            val hora = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HORA))
            val diasSemana = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIAS_SEMANA)).split(",").map { it == "true" }.toBooleanArray()
            val etiqueta = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ETIQUETA))
            val activada = cursor.getInt(cursor.getColumnIndex(KEY_ACTIVADA)) == 1
            alarma = Alarma(id, hora, diasSemana, etiqueta, activada)
        }
        cursor.close()
        return alarma
    }
    fun updateAlarma(alarma: Alarma): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_HORA, alarma.hora)
            put(KEY_DIAS_SEMANA, alarma.diasSemana.joinToString(","))
            put(KEY_ETIQUETA, alarma.etiqueta)
            put(KEY_ACTIVADA, if (alarma.activada)1 else 0)
        }
        return db.update(TABLE_ALARMAS, values, "$KEY_ID = ?", arrayOf(alarma.id.toString()))
    }
    fun deleteAlarma(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ALARMAS, "$KEY_ID = ?", arrayOf(id.toString()))
    }
}
