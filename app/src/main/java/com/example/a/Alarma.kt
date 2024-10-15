package com.example.a

data class Alarma(
    val id: Int,
    val hora: Int,
    val diasSemana: BooleanArray,
    val etiqueta: String,
    var activada: Boolean
)
