package com.miguelloaiza.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguelloaiza.miformacionctma.ui.Semana3Screen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.miguelloaiza.miformacionctma.viewmodel.ActividadesViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val actividadesViewModel: ActividadesViewModel = viewModel()

                    val uiState by actividadesViewModel.uiState.collectAsState()

                    Semana3Screen(
                        actividades = uiState.actividades,
                        resumenSemana2 = "Datos gestionados por el repositorio"
                    )
                }
            }
        }
    }
}