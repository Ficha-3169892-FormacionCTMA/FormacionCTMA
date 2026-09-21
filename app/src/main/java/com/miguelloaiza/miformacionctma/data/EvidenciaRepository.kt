package com.miguelloaiza.miformacionctma.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.miguelloaiza.miformacionctma.data.local.EstadoEvidencia
import com.miguelloaiza.miformacionctma.data.local.EvidenciaDao
import com.miguelloaiza.miformacionctma.data.local.EvidenciaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

open class EvidenciaRepository(
    private val context: Context,
    private val dao: EvidenciaDao,
    private val remote: EvidenciaRemoteDataSource = EvidenciaRemoteNoConfigurada
) {
    private val resolver = context.contentResolver

    companion object { const val MAX_BYTES = 10L * 1024 * 1024 }

    open fun observar(actividadId: Long): Flow<EvidenciaEntity?> = dao.observarPorActividad(actividadId)

    /**
     * Crea una URI segura para la camara usando FileProvider.
     * La UI no debe conocer las rutas de archivos reales.
     */
    open fun obtenerUriTemporal(actividadId: Long): Uri {
        val carpeta = File(context.cacheDir, "evidencias").apply { mkdirs() }
        val archivo = File(carpeta, "evidencia_${actividadId}_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    open suspend fun guardar(actividadId: Long, uriString: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val uri = uriString.toUri()
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
            dao.guardar(EvidenciaEntity(actividadId = actividadId, uri = uriString, mimeType = tipo, tamanoBytes = size, nombre = metadata.first, estado = EstadoEvidencia.LOCAL.name))
        }
    }

    open suspend fun eliminar(actividadId: Long) = withContext(Dispatchers.IO) {
        dao.eliminarPorActividad(actividadId)
    }

    open suspend fun sincronizar(actividadId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val evidencia = observarUnaVez(actividadId) ?: error("No hay evidencia para sincronizar")
            dao.actualizarEstado(actividadId, EstadoEvidencia.SUBIENDO.name)
            remote.enviar(actividadId, evidencia.uri, evidencia.mimeType).getOrThrow()
            dao.actualizarEstado(actividadId, EstadoEvidencia.SINCRONIZADA.name)
        }.onFailure {
            dao.actualizarEstado(actividadId, EstadoEvidencia.FALLIDA.name)
        }
    }

    private suspend fun observarUnaVez(actividadId: Long): EvidenciaEntity? =
        observar(actividadId).first()
}
