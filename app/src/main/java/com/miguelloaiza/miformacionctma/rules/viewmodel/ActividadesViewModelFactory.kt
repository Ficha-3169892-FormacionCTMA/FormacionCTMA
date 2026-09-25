package com.miguelloaiza.miformacionctma.rules.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.local.AppDatabase
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository

class ActividadesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = AppDatabase.obtenerInstancia(context).actividadDao()
        val repository = ActividadRepository(dao)
        val preferencias = PreferenciasRepository(context)

        @Suppress("UNCHECKED_CAST")
        return ActividadesViewModel(repository, preferencias) as T
    }
}