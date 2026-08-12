package com.miguelloaiza.miformacionctma

/**
 * Resultado de la validación de una actividad.
 *
 * Permite representar de forma clara si una actividad
 * es válida y cuáles errores fueron encontrados.
 */
data class ResultadoRegistro(
    val valido: Boolean,
    val errores: List<String> = emptyList()
)