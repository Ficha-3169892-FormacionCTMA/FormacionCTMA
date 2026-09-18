package com.miguelloaiza.miformacionctma.data.remote

import android.content.ContentResolver
import android.net.Uri
import com.miguelloaiza.miformacionctma.data.EvidenciaRemoteDataSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class RetrofitEvidenciaDataSource(
    private val api: ActividadApi,
    private val resolver: ContentResolver
) : EvidenciaRemoteDataSource {

    override suspend fun enviar(actividadId: Long, uriString: String, mimeType: String): Result<Unit> = runCatching {
        val uri = Uri.parse(uriString)
        val stream = resolver.openInputStream(uri) ?: error("No se pudo abrir el stream de la URI")
        val bytes = stream.use { it.readBytes() }
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("imagen", "evidencia.jpg", body)
        
        val response = api.subirEvidencia(actividadId, part)
        if (!response.isSuccessful) {
            error("Error en servidor: ${response.code()}")
        }
    }
}
