package com.miguelloaiza.miformacionctma.fake

import android.net.Uri
import com.miguelloaiza.miformacionctma.data.EvidenciaRemoteDataSource

class FakeEvidenciaRemoteDataSource : EvidenciaRemoteDataSource {
    var deberiaFallar = false
    var llamadaRealizada = false

    override suspend fun enviar(actividadId: Long, uriString: String, mimeType: String): Result<Unit> {
        llamadaRealizada = true
        return if (deberiaFallar) {
            Result.failure(Exception("Error simulado en red"))
        } else {
            Result.success(Unit)
        }
    }
}
