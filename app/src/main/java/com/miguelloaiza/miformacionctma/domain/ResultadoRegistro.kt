package com.miguelloaiza.miformacionctma.domain

sealed class ResultadoRegistro {

    data class Exito(
        val actividad: ActividadFormativa
    ) : ResultadoRegistro()

    data class ErrorValidacion(
        val mensaje: String
    ) : ResultadoRegistro()
}

fun describir(resultado: ResultadoRegistro): String = when (resultado) {
    is ResultadoRegistro.Exito ->
        "Registrada: ${resultado.actividad.titulo}"

    is ResultadoRegistro.ErrorValidacion ->
        "Error: ${resultado.mensaje}"
}