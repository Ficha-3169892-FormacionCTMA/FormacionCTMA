package com.miguelloaiza.miformacionctma.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "evidencias", indices = [Index("actividadId")])
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Long,
    val uri: String,
    val mimeType: String,
    val tamanoBytes: Long,
    val nombre: String,
    val estado: String
)

enum class EstadoEvidencia { LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA }
