package com.miguelloaiza.miformacionctma.rules.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository

/**
 * Factory necesaria porque ActividadesViewModel recibe dependencias
 * (repository y preferencias) en su constructor, y el sistema de
 * ViewModelProvider por defecto solo sabe crear ViewModels sin argumentos.
 */
class ActividadesViewModelFactory(
    private val repository: ActividadRepository,
    private val preferencias: PreferenciasRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActividadesViewModel::class.java)) {
            return ActividadesViewModel(repository, preferencias) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
    }
}