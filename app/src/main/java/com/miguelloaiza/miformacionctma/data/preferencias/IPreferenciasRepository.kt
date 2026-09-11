package com.miguelloaiza.miformacionctma.data.preferencias

import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de preferencias.
 * Permite sustituir la implementación real (DataStore) por un
 * doble de prueba sin Android Context.
 */
interface IPreferenciasRepository {
    fun obtenerFiltro(): Flow<String>
    suspend fun guardarFiltro(filtro: String)
}
