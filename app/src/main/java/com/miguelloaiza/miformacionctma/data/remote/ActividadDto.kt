package com.miguelloaiza.miformacionctma.data.remote

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import kotlinx.serialization.Serializable

@Serializable
data class ActividadDto(
    val id: Long,
    val titulo: String,
    val descripcion: String? = null,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: String
)

fun ActividadDto.aDominio(): ActividadFormativa = ActividadFormativa(
    id, titulo, descripcion, progreso, diasRestantes, Prioridad.valueOf(prioridad)
)
