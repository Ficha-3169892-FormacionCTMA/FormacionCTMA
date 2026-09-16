package com.miguelloaiza.miformacionctma.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.miguelloaiza.miformacionctma.data.local.EstadoEvidencia
import com.miguelloaiza.miformacionctma.data.local.EvidenciaDao
import com.miguelloaiza.miformacionctma.data.local.EvidenciaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

open class EvidenciaRepository(
    private val resolver: ContentResolver,
    private val dao: EvidenciaDao,
    private val remote: EvidenciaRemoteDataSource = EvidenciaRemoteNoConfigurada
) {
    companion object { const val MAX_BYTES = 10L * 1024 * 1024 }

    open fun observar(actividadId: Long): Flow<EvidenciaEntity?> = dao.observarPorActividad(actividadId)

    open suspend fun guardar(actividadId: Long, uriString: String): Result<Unit> = runCatching {
        val uri = Uri.parse(uriString)
        val tipo = resolver.getType(uri) ?: error("No se pudo identificar el tipo de archivo")
        require(tipo.startsWith("image/")) { "Solo se permiten imágenes" }
        val metadata = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val name = cursor.getString(0) ?: "evidencia.jpg"
            val size = cursor.getLong(1)
            name to size
        } ?: ("evidencia.jpg" to -1L)
        val size = if (metadata.second >= 0) metadata.second else resolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(8192); var total = 0L; while (true) { val n = input.read(buffer); if (n < 0) break; total += n; require(total <= MAX_BYTES) { "La imagen supera 10 MB" } }; total
        } ?: error("No se puede leer la imagen seleccionada")
        require(size <= MAX_BYTES) { "La imagen supera 10 MB" }
        dao.eliminarPorActividad(actividadId)
        dao.guardar(EvidenciaEntity(actividadId = actividadId, uri = uri.toString(), mimeType = tipo, tamanoBytes = size, nombre = metadata.first, estado = EstadoEvidencia.LOCAL.name))
    }

    open suspend fun eliminar(actividadId: Long) = dao.eliminarPorActividad(actividadId)

    open suspend fun sincronizar(actividadId: Long): Result<Unit> = runCatching {
        val evidencia = observarUnaVez(actividadId) ?: error("No hay evidencia para sincronizar")
        dao.actualizarEstado(actividadId, EstadoEvidencia.SUBIENDO.name)
        remote.enviar(actividadId, evidencia.uri, evidencia.mimeType).getOrThrow()
        dao.actualizarEstado(actividadId, EstadoEvidencia.SINCRONIZADA.name)
    }.onFailure {
        dao.actualizarEstado(actividadId, EstadoEvidencia.FALLIDA.name)
    }

    private suspend fun observarUnaVez(actividadId: Long): EvidenciaEntity? =
        observar(actividadId).first()
}
