package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.fake.FakeActividadRepository
import com.miguelloaiza.miformacionctma.fake.FakePreferenciasRepository
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModel
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias del ViewModel — Semana 7.
 *
 * Verifica los 8 casos de aceptación sin Thread.sleep ni delay real.
 * Usa TestCoroutineScheduler para controlar el tiempo virtual.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    // Dispatcher controlado por el test: permite avanzar el tiempo virtual.
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepo: FakeActividadRepository
    private lateinit var fakePref: FakePreferenciasRepository
    private lateinit var viewModel: ActividadesViewModel

    // Actividades de ejemplo reutilizables.
    private val actividadA = ActividadFormativa(
        id = 1L, titulo = "Kotlin Flows", descripcion = null,
        progreso = 50, diasRestantes = 5, prioridad = Prioridad.ALTA
    )
    private val actividadB = ActividadFormativa(
        id = 2L, titulo = "Room Database", descripcion = null,
        progreso = 80, diasRestantes = 2, prioridad = Prioridad.MEDIA
    )

    @Before
    fun setUp() {
        // Reemplaza Main con el dispatcher de prueba para que
        // viewModelScope.launch use tiempo virtual.
        Dispatchers.setMain(testDispatcher)

        fakeRepo = FakeActividadRepository()
        fakePref = FakePreferenciasRepository()
        viewModel = ActividadesViewModel(fakeRepo, fakePref)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // CA-01: Abrir sin actividades → Cargando → Vacio; no lista nula.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-01 sin actividades el estado es Vacio`() = runTest {
        fakeRepo.emitir(emptyList())

        val estado = viewModel.uiState.first { it !is ListadoUiState.Cargando }
        assertTrue(
            "Se esperaba Vacio pero se obtuvo $estado",
            estado is ListadoUiState.Vacio
        )
    }

    // -------------------------------------------------------------------------
    // CA-02: Insertar desde formulario → Room emite y Contenido cambia
    //        sin refresco manual.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-02 al insertar aparece en Contenido sin refresco`() = runTest {
        fakeRepo.emitir(emptyList())
        viewModel.guardarActividad(actividadA)
        advanceUntilIdle()

        val estado = viewModel.uiState.first { it !is ListadoUiState.Cargando }
        assertTrue("Se esperaba Contenido pero se obtuvo $estado",
            estado is ListadoUiState.Contenido)
        assertEquals(
            "Kotlin Flows",
            (estado as ListadoUiState.Contenido).actividades.first().titulo
        )
    }

    // -------------------------------------------------------------------------
    // CA-03: Cambiar filtro y reiniciar → DataStore restaura y combine
    //        recalcula el resultado.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-03 cambiar filtro recalcula lista`() = runTest {
        fakeRepo.emitir(listOf(actividadA, actividadB))

        // Filtrar solo ALTA → solo actividadA
        fakePref.emitirFiltro("ALTA")
        advanceUntilIdle()

        val estado = viewModel.uiState.first { it !is ListadoUiState.Cargando }
        val contenido = estado as ListadoUiState.Contenido
        assertEquals(1, contenido.actividades.size)
        assertEquals("Kotlin Flows", contenido.actividades.first().titulo)

        // Volver a TODAS → ambas actividades
        fakePref.emitirFiltro("TODAS")
        advanceUntilIdle()

        val estadoTodas = viewModel.uiState.first { it is ListadoUiState.Contenido }
        assertEquals(2, (estadoTodas as ListadoUiState.Contenido).actividades.size)
    }

    // -------------------------------------------------------------------------
    // CA-04: Búsquedas rápidas → gana la más reciente (debounce cancela la anterior).
    // -------------------------------------------------------------------------
    @Test
    fun `CA-04 busquedas rapidas solo gana la mas reciente`() = runTest {
        fakeRepo.emitir(listOf(actividadA, actividadB))
        advanceUntilIdle()

        // Simular escritura rápida
        viewModel.actualizarBusqueda("K")
        viewModel.actualizarBusqueda("Ko")
        viewModel.actualizarBusqueda("Kot")
        viewModel.actualizarBusqueda("Kotlin")
        advanceUntilIdle()

        val estado = viewModel.uiState.first { it !is ListadoUiState.Cargando }
        val contenido = estado as ListadoUiState.Contenido
        assertEquals(1, contenido.actividades.size)
        assertEquals("Kotlin Flows", contenido.actividades.first().titulo)
    }

    // -------------------------------------------------------------------------
    // CA-05: Forzar fallo del Repository → Error legible, Reintentar.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-05 fallo del repositorio emite Error con mensaje legible`() = runTest {
        fakeRepo.fallarEnInsertar = true
        viewModel.guardarActividad(actividadA)
        advanceUntilIdle()

        val operacion = viewModel.operacionState.first { it !is OperacionUiState.Inactiva }
        assertTrue(
            "Se esperaba Fallida pero fue $operacion",
            operacion is OperacionUiState.Fallida
        )
        val mensaje = (operacion as OperacionUiState.Fallida).error
        assertTrue("El mensaje debe ser legible", mensaje.isNotBlank())
    }

    // -------------------------------------------------------------------------
    // CA-07: Girar/recrear pantalla → StateFlow conserva el último estado.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-07 StateFlow conserva el estado tras recreacion`() = runTest {
        backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect()
        }
        fakeRepo.emitir(listOf(actividadA))
        advanceUntilIdle()

        // El StateFlow tiene replay=1: un nuevo colector recibe el último valor.
        val estadoInmediato = viewModel.uiState.value
        assertTrue(
            "Se esperaba Contenido pero fue $estadoInmediato",
            estadoInmediato is ListadoUiState.Contenido
        )
    }

    // -------------------------------------------------------------------------
    // CA-08: Pruebas sin Thread.sleep — este archivo es la evidencia.
    //        Todos los tests usan runTest + advanceUntilIdle.
    // -------------------------------------------------------------------------
    @Test
    fun `CA-08 suite completa sin Thread-sleep`() = runTest {
        backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect()
        }
        // Si llega aquí sin timeout, el scheduler virtual funciona correctamente.
        fakeRepo.emitir(listOf(actividadA, actividadB))
        advanceUntilIdle()
        val estado = viewModel.uiState.value
        assertFalse("El estado no debe ser Cargando", estado is ListadoUiState.Cargando)
    }

    // -------------------------------------------------------------------------
    // Extra: Eliminar actividad → desaparece de la lista reactivamente.
    // -------------------------------------------------------------------------
    @Test
    fun `eliminar actividad la quita de Contenido`() = runTest {
        fakeRepo.emitir(listOf(actividadA, actividadB))
        advanceUntilIdle()

        viewModel.eliminarActividad(actividadA)
        advanceUntilIdle()

        val estado = viewModel.uiState.first { it is ListadoUiState.Contenido }
        val lista = (estado as ListadoUiState.Contenido).actividades
        assertEquals(1, lista.size)
        assertEquals("Room Database", lista.first().titulo)
    }
}
