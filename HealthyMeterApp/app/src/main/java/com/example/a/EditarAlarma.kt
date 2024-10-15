package com.example.a

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.a.databinding.ActivityEditarAlarmaBinding
import java.util.Calendar

class EditarAlarma : AppCompatActivity() {
    private lateinit var binding: ActivityEditarAlarmaBinding
    private lateinit var db: DatabaseHelper
    private var alarmaId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarAlarmaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)
        alarmaId = intent.getIntExtra("ALARMA_ID", -1)
        if (alarmaId != -1) {
            cargarAlarma(alarmaId)
        }
        binding.buttonGuardar.setOnClickListener {
            if (validarEntradaUsuario()) {
                if (alarmaId == -1) {
                    guardarAlarmaEnBaseDeDatos()
                } else {
                    actualizarAlarmaEnBaseDeDatos()
                }
                finish()
            }
        }
    }
    private fun cargarAlarma(id: Int) {
        db.getAlarma(id)?.let { alarma ->
            binding.timePicker.apply {
                hour = alarma.hora / 60
                minute = alarma.hora % 60
            }
            setDiasSemanaVista(alarma.diasSemana)
            binding.editTextEtiqueta.setText(alarma.etiqueta)
        }
    }
    private fun validarEntradaUsuario(): Boolean {
        val hora = binding.timePicker.hour * 60 + binding.timePicker.minute
        val diasSemana = obtenerDiasSemanaDesdeVista()
        val etiqueta = binding.editTextEtiqueta.text.toString()
        return when {
            hora == -1 -> {
                Toast.makeText(this, "Seleccione una hora para la alarma", Toast.LENGTH_SHORT).show()
                false
            }
            !diasSemana.contains(true) -> {
                Toast.makeText(this, "Seleccione al menos un día de la semana", Toast.LENGTH_SHORT).show()
                false
            }
            etiqueta.isEmpty() -> {
                Toast.makeText(this, "Ingrese una etiqueta para la alarma", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
    private fun obtenerDiasSemanaDesdeVista(): BooleanArray {
        return BooleanArray(7) { index ->
            when (index) {
                0 -> binding.checkBoxLunes.isChecked
                1 -> binding.checkBoxMartes.isChecked
                2 -> binding.checkBoxMiercoles.isChecked
                3 -> binding.checkBoxJueves.isChecked
                4 -> binding.checkBoxViernes.isChecked
                5 -> binding.checkBoxSabado.isChecked
                6 -> binding.checkBoxDomingo.isChecked
                else -> false
            }
        }
    }
    private fun setDiasSemanaVista(diasSemana: BooleanArray) {
        binding.checkBoxLunes.isChecked = diasSemana[0]
        binding.checkBoxMartes.isChecked = diasSemana[1]
        binding.checkBoxMiercoles.isChecked = diasSemana[2]
        binding.checkBoxJueves.isChecked = diasSemana[3]
        binding.checkBoxViernes.isChecked = diasSemana[4]
        binding.checkBoxSabado.isChecked = diasSemana[5]
        binding.checkBoxDomingo.isChecked = diasSemana[6]
    }
    private fun guardarAlarmaEnBaseDeDatos() {
        val hora = binding.timePicker.hour * 60 + binding.timePicker.minute
        val diasSemana = obtenerDiasSemanaDesdeVista()
        val etiqueta = binding.editTextEtiqueta.text.toString()

        if (diasSemana.none { it }) {
            Toast.makeText(this, "Debes seleccionar al menos un día de la semana", Toast.LENGTH_SHORT).show()
            return
        }
        if (etiqueta.isEmpty()) {
            Toast.makeText(this, "Debes ingresar una etiqueta para la alarma", Toast.LENGTH_SHORT).show()
            return
        }
        val nuevaAlarma = Alarma(0, hora, diasSemana, etiqueta, true)
        db.agregarAlarma(nuevaAlarma)
        AlarmScheduler.scheduleAlarm(this, nuevaAlarma)

        Toast.makeText(this, "Alarma guardada correctamente", Toast.LENGTH_SHORT).show()
    }
    private fun actualizarAlarmaEnBaseDeDatos() {
        val hora = binding.timePicker.hour * 60 + binding.timePicker.minute
        val diasSemana = obtenerDiasSemanaDesdeVista()
        val etiqueta = binding.editTextEtiqueta.text.toString()
        val alarma = Alarma(id = alarmaId, hora = hora, diasSemana = diasSemana, etiqueta = etiqueta, activada = true)
        db.updateAlarma(alarma)
        AlarmScheduler.cancelAlarm(this, alarma)
        AlarmScheduler.scheduleAlarm(this, alarma)
    }
}