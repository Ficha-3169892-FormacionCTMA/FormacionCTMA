package com.miguelloaiza.miformacionctma.ui.estado

// Aquí corregimos la ruta usando tu paquete real y tu carpeta 'domain'
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa

/**
 * Representa el estado de la lista de actividades en la pantalla.
 */
sealed interface ListadoUiState {
    object Cargando : ListadoUiState
    data class Contenido(val actividades: List<ActividadFormativa>) : ListadoUiState
    object Vacio : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

/**
 * Representa el estado de una operación de escritura (Guardar o Eliminar).
 */
sealed interface OperacionUiState {
    object Inactiva : OperacionUiState
    object EnCurso : OperacionUiState
    object Exitosa : OperacionUiState
    data class Fallida(val error: String) : OperacionUiState
}