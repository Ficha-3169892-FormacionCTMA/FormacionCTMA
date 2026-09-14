package com.miguelloaiza.miformacionctma.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ActividadApi {
    @GET("v1/actividades")
    suspend fun obtenerActividades(): Response<List<ActividadDto>>
}
