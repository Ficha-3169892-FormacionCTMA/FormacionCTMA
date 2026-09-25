package com.miguelloaiza.miformacionctma.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Query("SELECT * FROM evidencias WHERE actividadId = :actividadId LIMIT 1")
    fun observarPorActividad(actividadId: Long): Flow<EvidenciaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(evidencia: EvidenciaEntity)

    @Query("DELETE FROM evidencias WHERE actividadId = :actividadId")
    suspend fun eliminarPorActividad(actividadId: Long)

    @Query("UPDATE evidencias SET estado = :estado WHERE actividadId = :actividadId")
    suspend fun actualizarEstado(actividadId: Long, estado: String)
}
