package com.miguelloaiza.miformacionctma.data

import android.net.Uri

/** Contrato aislado de red: la UI nunca conoce URL, token ni multipart. */
interface EvidenciaRemoteDataSource {
    suspend fun enviar(actividadId: Long, uri: Uri, mimeType: String): Result<Unit>
}

/** Se sustituye por Retrofit cuando el servicio de Semana 8 exponga el endpoint. */
object EvidenciaRemoteNoConfigurada : EvidenciaRemoteDataSource {
    override suspend fun enviar(actividadId: Long, uri: Uri, mimeType: String): Result<Unit> =
        Result.failure(IllegalStateException("Servicio de evidencias no configurado"))
}
