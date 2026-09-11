package com.miguelloaiza.miformacionctma.rules.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.preferencias.IPreferenciasRepository
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class ActividadesViewModel(
    private val repository: ActividadRepository,
    private val preferencias: IPreferenciasRepository
) : ViewModel() {

    private val _busquedaQuery = MutableStateFlow("")

    // CA-05: cada incremento fuerza una nueva suscripcion a
    // repository.obtenerActividades() a traves del flatMapLatest de abajo,
    // que es la unica forma de "revivir" el Flow despues de que .catch
    // consumio una excepcion anterior.
    private val _intentos = MutableStateFlow(0)

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacionState: StateFlow<OperacionUiState> = _operacionState.asStateFlow()

    private var operacionJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<ListadoUiState> = combine(
        _busquedaQuery.debounce(300),
        _intentos
    ) { query, _ -> query }
        .flatMapLatest { query ->
            combine(
                repository.obtenerActividades(),
                preferencias.obtenerFiltro()
            ) { actividades, filtro ->

                val actividadesPorFiltro = if (filtro == "TODAS") {
                    actividades
                } else {
                    actividades.filter { it.prioridad.name == filtro }
                }

                val resultadoFinal = if (query.isBlank()) {
                    actividadesPorFiltro
                } else {
                    actividadesPorFiltro.filter { it.titulo.contains(query, ignoreCase = true) }
                }

                if (resultadoFinal.isEmpty()) {
                    ListadoUiState.Vacio
                } else {
                    ListadoUiState.Contenido(resultadoFinal)
                }
            }
        }
        .catch { excepcion ->
            emit(ListadoUiState.Error(excepcion.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListadoUiState.Cargando
        )

    fun actualizarBusqueda(query: String) {
        _busquedaQuery.value = query
    }

    // CA-05: llamado desde el boton "Reintentar" en la UI.
    fun reintentar() {
        _intentos.value += 1
    }

    fun guardarActividad(actividad: ActividadFormativa) {
        operacionJob?.cancel()
        operacionJob = viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.insertar(actividad)
                _operacionState.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error al guardar")
            }
        }
    }

    fun eliminarActividad(actividad: ActividadFormativa) {
        operacionJob?.cancel()
        operacionJob = viewModelScope.launch {
            _operacionState.value = OperacionUiState.EnCurso
            try {
                repository.eliminar(actividad)
                _operacionState.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Fallida(e.message ?: "Error al eliminar")
            }
        }
    }

    fun reiniciarOperacion() {
        _operacionState.value = OperacionUiState.Inactiva
    }
}
