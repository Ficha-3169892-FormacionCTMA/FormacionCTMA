package com.miguelloaiza.miformacionctma.fake

import com.miguelloaiza.miformacionctma.data.preferencias.IPreferenciasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Doble de prueba para IPreferenciasRepository.
 *
 * Implementa la interfaz directamente — no necesita Context ni DataStore.
 * El test controla el filtro con [emitirFiltro] (CA-03).
 */
class FakePreferenciasRepository : IPreferenciasRepository {

    private val _filtro = MutableStateFlow("TODAS")
    private val _token = MutableStateFlow<String?>(null)

    override fun obtenerFiltro(): Flow<String> = _filtro.asStateFlow()

    override suspend fun guardarFiltro(filtro: String) {
        _filtro.value = filtro
    }

    override fun obtenerToken(): Flow<String?> = _token.asStateFlow()

    override suspend fun guardarToken(token: String) {
        _token.value = token
    }

    override suspend fun borrarToken() {
        _token.value = null
    }

    /** Emite un filtro directamente sin corrutinas — útil en setUp(). */
    fun emitirFiltro(filtro: String) {
        _filtro.value = filtro
    }

    /** Emite un token directamente sin corrutinas. */
    fun emitirToken(token: String?) {
        _token.value = token
    }
}
