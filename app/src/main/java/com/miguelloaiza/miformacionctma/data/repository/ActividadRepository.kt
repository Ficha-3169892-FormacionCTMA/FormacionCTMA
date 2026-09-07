package com.miguelloaiza.miformacionctma.data.repository

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa

interface ActividadRepository {

    fun obtenerActividades(): List<ActividadFormativa>

    fun obtenerActividad(id: Long): ActividadFormativa?
}