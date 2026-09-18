package com.miguelloaiza.miformacionctma.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: String
)

fun ActividadEntity.aDominio(): ActividadFormativa {
    return ActividadFormativa(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        diasRestantes = diasRestantes,
        prioridad = Prioridad.valueOf(prioridad)
    )
}

fun ActividadFormativa.aEntity(): ActividadEntity {
    return ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        diasRestantes = diasRestantes,
        prioridad = prioridad.name
    )
}