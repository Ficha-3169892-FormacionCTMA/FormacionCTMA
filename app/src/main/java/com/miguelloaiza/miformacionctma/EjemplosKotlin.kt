package com.miguelloaiza.miformacionctma

/**
 * Ejemplos de fundamentos de Kotlin solicitados en la guía.
 *
 * Este archivo reúne ejemplos de:
 * - val y var
 * - tipos de datos
 * - operadores
 * - when
 * - funciones
 * - colecciones
 * - null safety
 */

// Tipos, val y var
fun ejemploVariables(): String {
    val nombre: String = "Aprendiz"
    var progreso: Int = 50

    progreso += 10

    return "$nombre tiene un progreso de $progreso%"
}

// Operadores
fun ejemploOperadores(a: Int, b: Int): Int {
    return (a + b) * 2
}

// when
fun ejemploWhen(progreso: Int): String =
    when {
        progreso >= 100 -> "Completada"
        progreso > 0 -> "En proceso"
        else -> "Pendiente"
    }

// Funciones
fun calcularPromedio(
    valores: List<Int>
): Double =
    if (valores.isEmpty()) {
        0.0
    } else {
        valores.average()
    }

// Colecciones
fun ejemploColecciones(): List<String> {
    val actividades = listOf(
        "Kotlin",
        "Android Studio",
        "Git"
    )

    return actividades.filter {
        it.isNotBlank()
    }
}

// Null safety
fun ejemploNullSafety(
    nombreCompleto: String?,
    alias: String
): String =
    nombreCompleto
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: alias

// let para trabajar de forma segura con un valor opcional
fun ejemploLet(
    descripcion: String?
): String {
    var resultado = "Sin descripción"

    descripcion?.let {
        if (it.isNotBlank()) {
            resultado = it.trim()
        }
    }

    return resultado
}