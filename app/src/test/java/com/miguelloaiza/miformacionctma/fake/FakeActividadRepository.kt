package com.miguelloaiza.miformacionctma.fake

import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.local.ActividadDao
import com.miguelloaiza.miformacionctma.data.local.ActividadEntity
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositorio falso para pruebas unitarias (CA-01 a CA-08).
 *
 * Extiende ActividadRepository con un DAO nulo (nunca se llama),
 * y sobreescribe todos los métodos para usar un MutableStateFlow
 * que el test controla directamente. Sin base de datos real,
 * sin Thread.sleep.
 */
class FakeActividadRepository : ActividadRepository(dao = EmptyDao()) {

    private val _actividades = MutableStateFlow<List<ActividadFormativa>>(emptyList())

    /** El test llama a esto para simular emisiones de Room. */
    fun emitir(lista: List<ActividadFormativa>) {
        _actividades.value = lista
    }

    /** Si es true, insertar() lanza una excepción (CA-05). */
    var fallarEnInsertar = false

    override fun obtenerActividades(): Flow<List<ActividadFormativa>> =
        _actividades.asStateFlow()

    override suspend fun insertar(actividad: ActividadFormativa) {
        if (fallarEnInsertar) throw RuntimeException("Error simulado de BD")
        _actividades.update { lista ->
            lista + actividad.copy(id = (lista.size + 1).toLong())
        }
    }

    override suspend fun eliminar(actividad: ActividadFormativa) {
        _actividades.update { lista -> lista.filter { it.id != actividad.id } }
    }

    /** DAO vacío: ninguno de sus métodos se invoca en pruebas unitarias. */
    private class EmptyDao : ActividadDao {
        override fun obtenerTodas(): Flow<List<ActividadEntity>> =
            MutableStateFlow(emptyList<ActividadEntity>()).asStateFlow()

        override suspend fun insertar(actividad: ActividadEntity) = Unit
        override suspend fun eliminar(actividad: ActividadEntity) = Unit
        override suspend fun eliminarPorId(id: Long) = Unit
    }
}
