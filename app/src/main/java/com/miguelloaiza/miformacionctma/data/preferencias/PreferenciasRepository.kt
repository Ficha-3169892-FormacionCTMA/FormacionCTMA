package com.miguelloaiza.miformacionctma.data.preferencias

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias_app")

class PreferenciasRepository(private val context: Context) {

    private val FILTRO_KEY = stringPreferencesKey("filtro_prioridad")

    fun obtenerFiltro(): Flow<String> {
        return context.dataStore.data.map { preferencias ->
            preferencias[FILTRO_KEY] ?: "TODAS"
        }
    }

    suspend fun guardarFiltro(filtro: String) {
        context.dataStore.edit { preferencias ->
            preferencias[FILTRO_KEY] = filtro
        }
    }
}