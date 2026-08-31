package com.miguelloaiza.miformacionctma.viewmodel

import androidx.lifecycle.ViewModel
import com.miguelloaiza.miformacionctma.data.repository.ActividadRepository
import com.miguelloaiza.miformacionctma.data.repository.InMemoryActividadRepository
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FiltroActividad(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    COMPLETADAS("Completadas")
}

data class ActividadesUiState(
    val actividades: List<ActividadFormativa> = emptyList(),
    val filtro: FiltroActividad = FiltroActividad.TODAS,
    val progresoDemo: Int = 60
) {
    val actividadesFiltradas: List<ActividadFormativa>
        get() = when (filtro) {
            FiltroActividad.TODAS -> actividades

            FiltroActividad.PENDIENTES -> actividades.filter {
                ReglasActividad.estadoActividad(it) != EstadoActividad.COMPLETADA
            }

            FiltroActividad.COMPLETADAS -> actividades.filter {
                ReglasActividad.estadoActividad(it) == EstadoActividad.COMPLETADA
            }
        }
}

class ActividadesViewModel(
    private val repository: ActividadRepository = InMemoryActividadRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActividadesUiState(
            actividades = repository.obtenerActividades()
        )
    )

    val uiState: StateFlow<ActividadesUiState> = _uiState.asStateFlow()

    fun cambiarFiltro(filtro: FiltroActividad) {
        _uiState.value = _uiState.value.copy(
            filtro = filtro
        )
    }

    fun cambiarProgresoDemo() {
        val progresoActual = _uiState.value.progresoDemo

        _uiState.value = _uiState.value.copy(
            progresoDemo = if (progresoActual == 60) 100 else 60
        )
    }

    fun obtenerActividad(id: Long): ActividadFormativa? {
        return repository.obtenerActividad(id)
    }
}