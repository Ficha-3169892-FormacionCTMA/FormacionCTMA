package com.miguelloaiza.miformacionctma.data.remote

import kotlinx.coroutines.CancellationException
import java.io.IOException

class RemoteActividadDataSource(private val api: ActividadApi) {
    suspend fun consultar(): Result<List<ActividadDto>> = try {
        val respuesta = api.obtenerActividades()
        when {
            respuesta.isSuccessful -> Result.success(respuesta.body() ?: emptyList())
            respuesta.code() == 401 -> Result.failure(EstadoRemotoException("Sesión vencida. Renueva la sesión."))
            respuesta.code() == 404 -> Result.failure(EstadoRemotoException("Recurso no encontrado."))
            respuesta.code() >= 500 -> Result.failure(EstadoRemotoException("El servicio no está disponible."))
            else -> Result.failure(EstadoRemotoException("No se pudo actualizar la información."))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Result.failure(EstadoRemotoException("Sin conexión o tiempo de espera agotado."))
    } catch (e: Exception) {
        Result.failure(EstadoRemotoException("Respuesta del servicio no válida."))
    }
}

class EstadoRemotoException(message: String) : Exception(message)
