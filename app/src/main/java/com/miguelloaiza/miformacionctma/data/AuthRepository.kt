package com.miguelloaiza.miformacionctma.data

import com.miguelloaiza.miformacionctma.data.preferencias.IPreferenciasRepository
import com.miguelloaiza.miformacionctma.data.remote.ActividadApi
import com.miguelloaiza.miformacionctma.data.remote.LoginRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val api: ActividadApi,
    private val preferencias: IPreferenciasRepository
) {
    fun estaLogueado(): Flow<Boolean> = preferencias.obtenerToken().map { it != null }

    suspend fun login(email: String, clave: String): Result<Unit> = runCatching {
        val response = api.login(LoginRequestDto(email, clave))
        if (response.isSuccessful) {
            val body = response.body() ?: error("Respuesta vacía del servidor")
            preferencias.guardarToken(body.token)
        } else {
            error("Credenciales inválidas o error de servidor: ${response.code()}")
        }
    }

    suspend fun logout() {
        preferencias.borrarToken()
    }

    fun obtenerToken(): Flow<String?> = preferencias.obtenerToken()
}
