package com.miguelloaiza.miformacionctma.rules

interface ValidadorActividad {
    fun validar(
        titulo: String,
        progreso: Int
    ): List<String>
}

class ValidadorActividadBasico : ValidadorActividad {

    override fun validar(
        titulo: String,
        progreso: Int
    ): List<String> = buildList {

        if (titulo.isBlank()) {
            add("El título es obligatorio")
        }

        if (progreso !in 0..100) {
            add("El progreso debe estar entre 0 y 100")
        }
    }
}