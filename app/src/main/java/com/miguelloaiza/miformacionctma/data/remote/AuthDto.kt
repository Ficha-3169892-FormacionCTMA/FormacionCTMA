package com.miguelloaiza.miformacionctma.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val clave: String
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val nombre: String? = null
)
