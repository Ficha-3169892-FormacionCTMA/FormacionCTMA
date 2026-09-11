package com.miguelloaiza.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.local.AppDatabase
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository
import com.miguelloaiza.miformacionctma.ui.screens.ActividadesScreen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Construccion de dependencias (Semana 7: sin Hilt todavia,
        // se instancian aqui a mano y se inyectan via Factory al ViewModel).
        val database = AppDatabase.obtenerInstancia(applicationContext)
        val repository = ActividadRepository(database.actividadDao())
        val preferencias = PreferenciasRepository(applicationContext)

        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActividadesScreen(
                        repository = repository,
                        preferencias = preferencias
                    )
                }
            }
        }
    }
}