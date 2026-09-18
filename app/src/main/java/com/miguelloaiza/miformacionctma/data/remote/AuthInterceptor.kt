package com.miguelloaiza.miformacionctma.data.remote

import com.miguelloaiza.miformacionctma.data.preferencias.IPreferenciasRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preferencias: IPreferenciasRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { preferencias.obtenerToken().first() }
        val request = chain.request()
        
        return if (token != null && !request.url.encodedPath.contains("auth/login")) {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }
}
