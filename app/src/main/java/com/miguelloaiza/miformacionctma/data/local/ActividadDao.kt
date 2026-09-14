package com.miguelloaiza.miformacionctma.data.local
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {

    @Query("SELECT * FROM actividades ORDER BY diasRestantes ASC")
    fun obtenerTodas(): Flow<List<ActividadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(actividad: ActividadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(actividades: List<ActividadEntity>)

    @Query("DELETE FROM actividades")
    suspend fun eliminarTodas()

    @Delete
    suspend fun eliminar(actividad: ActividadEntity)

    @Query("DELETE FROM actividades WHERE id = :id")
    suspend fun eliminarPorId(id: Long)

    @Transaction
    suspend fun reemplazarTodas(actividades: List<ActividadEntity>) {
        eliminarTodas()
        insertarTodas(actividades)
    }
}
