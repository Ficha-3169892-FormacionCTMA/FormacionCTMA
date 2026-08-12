package com.miguelloaiza.miformacionctma.examples

// 5.1 Kotlin: variables, tipos e inferencia

val nombreAprendiz: String = "Felipe"
val horasPlaneadas = 12

var progreso = 40

fun ejemploVariables(): String {
    progreso += 20
    return "$nombreAprendiz lleva $progreso%"
}


// 5.2 Tipos y operadores en contexto

val idActividad: Long = 1001L
val tituloActividad: String = "Configurar Android Studio"
val horasActividad: Double = 2.5
val actividadObligatoria: Boolean = true
val progresoActividad: Int = 80

val actividadCompletada = progresoActividad >= 100


// 5.3 Condiciones como expresiones

fun estadoProgreso(progreso: Int): String = when {
    progreso !in 0..100 -> "Progreso inválido"
    progreso == 100 -> "Completada"
    progreso >= 70 -> "Avanzada"
    progreso > 0 -> "En proceso"
    else -> "Pendiente"
}


// 5.4 Funciones pequeñas y comprobables

fun estaVencida(
    diasRestantes: Int,
    completada: Boolean
): Boolean {
    return diasRestantes < 0 && !completada
}

fun mensajeActividad(
    titulo: String,
    progreso: Int
): String =
    "$titulo · ${estadoProgreso(progreso)}"


// 5.5 Colecciones y operaciones expresivas

fun resumenColeccion(): String {
    val progresos = listOf(100, 80, 40, 0)

    val completadas = progresos.count { it == 100 }
    val pendientes = progresos.filter { it < 100 }
    val promedio = progresos.average()

    return """
        Completadas: $completadas
        Pendientes: $pendientes
        Promedio: $promedio
    """.trimIndent()
}


// 5.6 Null safety

fun ejemploNullSafety(): String {
    val enlaceEvidencia: String? = null

    val longitud = enlaceEvidencia?.length ?: 0

    val mensaje = enlaceEvidencia
        ?.let { "Evidencia disponible: $it" }
        ?: "Aún no se registró evidencia"

    return "Longitud: $longitud · $mensaje"
}