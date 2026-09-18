package com.miguelloaiza.miformacionctma.fake

import android.content.ContentResolver
import android.net.Uri
import com.miguelloaiza.miformacionctma.data.EvidenciaRepository
import com.miguelloaiza.miformacionctma.data.local.EstadoEvidencia
import com.miguelloaiza.miformacionctma.data.local.EvidenciaDao
import com.miguelloaiza.miformacionctma.data.local.EvidenciaEntity

class FakeEvidenciaRepository(
    private val dao: EvidenciaDao,
    private val remote: FakeEvidenciaRemoteDataSource = FakeEvidenciaRemoteDataSource()
) : EvidenciaRepository(
    resolver = DummyContentResolver(),
    dao = dao,
    remote = remote
) {
    var deberiaFallarValidacion = false

    class DummyContentResolver : ContentResolver(null)

    override suspend fun guardar(actividadId: Long, uriString: String): Result<Unit> {
        return if (deberiaFallarValidacion) {
            Result.failure(IllegalArgumentException("La imagen supera 10 MB"))
        } else {
            dao.guardar(EvidenciaEntity(
                actividadId = actividadId,
                uri = uriString,
                mimeType = "image/jpeg",
                tamanoBytes = 1024,
                nombre = "test.jpg",
                estado = EstadoEvidencia.LOCAL.name
            ))
            Result.success(Unit)
        }
    }

    override suspend fun sincronizar(actividadId: Long): Result<Unit> {
        return try {
            dao.actualizarEstado(actividadId, EstadoEvidencia.SUBIENDO.name)
            remote.enviar(actividadId, "dummy_uri", "image/jpeg").getOrThrow()
            dao.actualizarEstado(actividadId, EstadoEvidencia.SINCRONIZADA.name)
            Result.success(Unit)
        } catch (e: Exception) {
            dao.actualizarEstado(actividadId, EstadoEvidencia.FALLIDA.name)
            Result.failure(e)
        }
    }
}
