package com.miguelloaiza.miformacionctma.data

import com.miguelloaiza.miformacionctma.data.local.ActividadDao
import com.miguelloaiza.miformacionctma.data.local.aDominio
import com.miguelloaiza.miformacionctma.data.local.aEntity
import com.miguelloaiza.miformacionctma.data.remote.RemoteActividadDataSource
import com.miguelloaiza.miformacionctma.data.remote.aDominio
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class ActividadRepository(
    private val dao: ActividadDao,
    private val remoto: RemoteActividadDataSource? = null
) {

    open fun obtenerActividades(): Flow<List<ActividadFormativa>> {
        return dao.obtenerTodas().map { lista ->
            lista.map { it.aDominio() }
        }
    }

    open suspend fun insertar(actividad: ActividadFormativa) {
        dao.insertar(actividad.aEntity())
    }

    open suspend fun eliminar(actividad: ActividadFormativa) {
        dao.eliminar(actividad.aEntity())
    }

    open suspend fun actualizarDesdeRed(): Result<Unit> {
        val fuenteRemota = remoto ?: return Result.failure(IllegalStateException("Servicio no configurado"))
        return fuenteRemota.consultar().mapCatching { dtos ->
            dao.reemplazarTodas(dtos.map { it.aDominio().aEntity() })
        }
    }
}
