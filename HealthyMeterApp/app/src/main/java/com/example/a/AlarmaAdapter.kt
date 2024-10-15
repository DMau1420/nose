package com.example.a

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class AlarmaAdapter(
    private var alarmas: List<Alarma>,
    private val onItemClickListener: OnItemClickListener,
    private val onSwitchToggleListener: OnSwitchToggleListener,
    private val onDeleteClickListener: MainActivity
) : RecyclerView.Adapter<AlarmaAdapter.AlarmaViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(alarma: Alarma)
    }
    interface OnSwitchToggleListener {
        fun onSwitchToggle(alarma: Alarma, isChecked: Boolean)
    }
    class AlarmaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEtiqueta: TextView = view.findViewById(R.id.tvEtiqueta)
        val tvHora: TextView = view.findViewById(R.id.tvHora)
        val tvDias: TextView = view.findViewById(R.id.tvDias)
        val switchActivada: Switch = view.findViewById(R.id.switchActivada)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
    }
    interface OnDeleteClickListener {
        fun onDeleteClick(alarma: Alarma)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarma, parent, false)
        return AlarmaViewHolder(view)
    }
    override fun onBindViewHolder(holder: AlarmaViewHolder, position: Int) {
        val alarma = alarmas[position]
        holder.tvEtiqueta.text = alarma.etiqueta
        holder.tvHora.text = formatHora(alarma.hora)
        holder.tvDias.text = formatDiasSemana(alarma.diasSemana)
        holder.switchActivada.setOnCheckedChangeListener(null)
        holder.switchActivada.isChecked = alarma.activada
        holder.switchActivada.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            onSwitchToggleListener.onSwitchToggle(alarma, isChecked)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(alarma)
        }
        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(alarma)
        }
    }
    override fun getItemCount() = alarmas.size
    fun updateAlarms(newAlarms: List<Alarma>) {
        this.alarmas = newAlarms
        notifyDataSetChanged()
    }
    private fun formatHora(hora: Int): String {
        val hour = hora / 60
        val minute = hora % 60
        return String.format("%02d:%02d", hour, minute)
    }
    private fun formatDiasSemana(diasSemana: BooleanArray): String {
        val dias = listOf("L", "M", "X", "J", "V", "S", "D")
        val diasActivos = diasSemana.mapIndexed { index, isActive ->
            if (isActive) dias[index] else null
        }
        return diasActivos.filterNotNull().joinToString(", ")
    }
}