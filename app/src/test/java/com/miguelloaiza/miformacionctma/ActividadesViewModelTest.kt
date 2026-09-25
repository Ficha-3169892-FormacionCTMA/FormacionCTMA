package com.miguelloaiza.miformacionctma

import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.fake.FakeActividadRepository
import com.miguelloaiza.miformacionctma.fake.FakeEvidenciaDao
import com.miguelloaiza.miformacionctma.fake.FakeEvidenciaRemoteDataSource
import com.miguelloaiza.miformacionctma.fake.FakeEvidenciaRepository
import com.miguelloaiza.miformacionctma.fake.FakePreferenciasRepository
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModel
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepo: FakeActividadRepository
    private lateinit var fakePref: FakePreferenciasRepository
    private lateinit var fakeEvidenciaRepo: FakeEvidenciaRepository
    private lateinit var fakeEvidenciaDao: FakeEvidenciaDao
    private lateinit var fakeRemote: FakeEvidenciaRemoteDataSource
    private lateinit var viewModel: ActividadesViewModel

    private val actividadA = ActividadFormativa(
        id = 1L,
        titulo = "Kotlin Flows",
        descripcion = null,
        progreso = 50,
        diasRestantes = 5,
        prioridad = Prioridad.ALTA
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeRepo = FakeActividadRepository()
        fakePref = FakePreferenciasRepository()
        fakeEvidenciaDao = FakeEvidenciaDao()
        fakeRemote = FakeEvidenciaRemoteDataSource()

        fakeEvidenciaRepo = FakeEvidenciaRepository(
            fakeEvidenciaDao,
            fakeRemote
        )

        viewModel = ActividadesViewModel(
            fakeRepo,
            fakePref,
            fakeEvidenciaRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `S11-CA-01 varias actividades aparecen en contenido`() = runTest {

        val actividadB = ActividadFormativa(
            id = 2L,
            titulo = "Android Studio",
            descripcion = "Prueba de Android",
            progreso = 30,
            diasRestantes = 10,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(
            listOf(
                actividadA,
                actividadB
            )
        )

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val actividades =
            (estado as ListadoUiState.Contenido).actividades

        assertEquals(2, actividades.size)
    }

    @Test
    fun `S11-CA-02 actividad conserva su porcentaje de progreso`() = runTest {

        val actividad = ActividadFormativa(
            id = 5L,
            titulo = "Prueba Kotlin",
            descripcion = "Actividad de prueba",
            progreso = 75,
            diasRestantes = 4,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(
            listOf(actividad)
        )

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(
            75,
            resultado.progreso
        )
    }


    @Test
    fun `S11-CA-03 actividad conserva los dias restantes`() = runTest {

        val actividad = ActividadFormativa(
            id = 6L,
            titulo = "Actividad de prueba",
            descripcion = null,
            progreso = 20,
            diasRestantes = 12,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(
            listOf(actividad)
        )

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(
            12,
            resultado.diasRestantes
        )
    }


    @Test
    fun `S11-CA-04 actividad conserva su prioridad`() = runTest {

        val actividad = ActividadFormativa(
            id = 7L,
            titulo = "Actividad prioritaria",
            descripcion = null,
            progreso = 90,
            diasRestantes = 2,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(
            listOf(actividad)
        )

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(
            Prioridad.ALTA,
            resultado.prioridad
        )
    }


    @Test
    fun `S11-CA-05 actividad conserva su descripcion`() = runTest {

        val actividad = ActividadFormativa(
            id = 8L,
            titulo = "Actividad con descripcion",
            descripcion = "Descripcion de la actividad",
            progreso = 40,
            diasRestantes = 8,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(
            listOf(actividad)
        )

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(
            "Descripcion de la actividad",
            resultado.descripcion
        )
    }
}