package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.rules.ReglasActividad

/**
 * Encapsula la validación de una actividad.
 *
 * La interfaz puede utilizar esta clase sin conocer
 * directamente los detalles de las reglas de negocio.
 */
object ValidadorActividad {

    fun validar(
        titulo: String,
        progreso: Int
    ): ResultadoRegistro {

        val errores = ReglasActividad.validarActividad(
            titulo = titulo,
            progreso = progreso
        )

        return ResultadoRegistro(
            valido = errores.isEmpty(),
            errores = errores
        )
    }
}