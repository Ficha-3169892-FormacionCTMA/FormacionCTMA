package com.miguelloaiza.miformacionctma.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ActividadApi {
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @GET("v1/actividades")
    suspend fun obtenerActividades(): Response<List<ActividadDto>>

    @Multipart
    @POST("v1/actividades/{id}/evidencia")
    suspend fun subirEvidencia(
        @Path("id") id: Long,
        @Part imagen: MultipartBody.Part
    ): Response<Unit>
}
