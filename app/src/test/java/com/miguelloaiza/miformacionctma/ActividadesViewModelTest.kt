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
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState
import com.miguelloaiza.miformacionctma.data.local.EstadoEvidencia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // =========================================================
    // TESTS SEMANA 7
    // =========================================================

    @Test
    fun `CA-01 sin actividades el estado es Vacio`() = runTest {
        fakeRepo.emitir(emptyList())

        val estado = viewModel.uiState.first {
            it !is ListadoUiState.Cargando
        }

        assertTrue(estado is ListadoUiState.Vacio)
    }

    @Test
    fun `CA-02 al insertar aparece en Contenido sin refresco`() = runTest {
        fakeRepo.emitir(emptyList())

        viewModel.guardarActividad(actividadA)

        advanceUntilIdle()

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        assertEquals(
            "Kotlin Flows",
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()
                .titulo
        )
    }

    // =========================================================
    // TESTS SEMANA 9
    // =========================================================

    @Test
    fun `S9-CA-01 elegir imagen valida persiste URI y estado LOCAL`() = runTest {
        viewModel.guardarEvidencia(
            1L,
            "content://media/1"
        )

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(1L)
            .first()

        assertNotNull(evidencia)

        assertEquals(
            EstadoEvidencia.LOCAL.name,
            evidencia?.estado
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Exitosa
        )
    }

    @Test
    fun `S9-CA-04 imagen mayor al limite es rechazada`() = runTest {
        fakeEvidenciaRepo.deberiaFallarValidacion = true

        viewModel.guardarEvidencia(
            1L,
            "content://media/1"
        )

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(1L)
            .first()

        assertNull(evidencia)

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Fallida
        )

        assertEquals(
            "La imagen supera 10 MB",
            (
                    viewModel.operacionState.value
                            as OperacionUiState.Fallida
                    ).error
        )
    }

    @Test
    fun `S9-CA-06 falla la subida actualiza a FALLIDA y mantiene local`() = runTest {
        viewModel.guardarEvidencia(
            1L,
            "content://media/1"
        )

        advanceUntilIdle()

        fakeRemote.deberiaFallar = true

        viewModel.sincronizarEvidencia(1L)

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(1L)
            .first()

        assertEquals(
            EstadoEvidencia.FALLIDA.name,
            evidencia?.estado
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Fallida
        )
    }

    @Test
    fun `S9-CA-08 eliminar evidencia borra el registro`() = runTest {
        viewModel.guardarEvidencia(
            1L,
            "content://media/1"
        )

        advanceUntilIdle()

        assertNotNull(
            viewModel.observarEvidencia(1L).first()
        )

        viewModel.eliminarEvidencia(1L)

        advanceUntilIdle()

        assertNull(
            viewModel.observarEvidencia(1L).first()
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Exitosa
        )
    }

    @Test
    fun `S9-CA-06 reintentar sincronizacion tras fallo`() = runTest {
        viewModel.guardarEvidencia(
            1L,
            "content://media/1"
        )

        advanceUntilIdle()

        fakeRemote.deberiaFallar = true

        viewModel.sincronizarEvidencia(1L)

        advanceUntilIdle()

        fakeRemote.deberiaFallar = false

        viewModel.sincronizarEvidencia(1L)

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(1L)
            .first()

        assertEquals(
            EstadoEvidencia.SINCRONIZADA.name,
            evidencia?.estado
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Exitosa
        )
    }
    @Test
    fun `S10-CA-01 consultar evidencia inexistente devuelve null`() = runTest {
        val evidencia = viewModel
            .observarEvidencia(999L)
            .first()

        assertNull(evidencia)
    }

    @Test
    fun `S10-CA-02 guardar evidencia nueva genera estado LOCAL`() = runTest {
        viewModel.guardarEvidencia(
            2L,
            "content://media/2"
        )

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(2L)
            .first()

        assertNotNull(evidencia)

        assertEquals(
            EstadoEvidencia.LOCAL.name,
            evidencia?.estado
        )
    }

    @Test
    fun `S10-CA-03 evidencia guardada puede sincronizarse correctamente`() = runTest {
        viewModel.guardarEvidencia(
            3L,
            "content://media/3"
        )

        advanceUntilIdle()

        fakeRemote.deberiaFallar = false

        viewModel.sincronizarEvidencia(3L)

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(3L)
            .first()

        assertNotNull(evidencia)

        assertEquals(
            EstadoEvidencia.SINCRONIZADA.name,
            evidencia?.estado
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Exitosa
        )
    }

    @Test
    fun `S10-CA-04 eliminar evidencia inexistente no genera registro`() = runTest {
        viewModel.eliminarEvidencia(500L)

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(500L)
            .first()

        assertNull(evidencia)
    }

    @Test
    fun `S10-CA-05 dos evidencias de actividades diferentes se mantienen independientes`() = runTest {
        viewModel.guardarEvidencia(
            10L,
            "content://media/10"
        )

        viewModel.guardarEvidencia(
            20L,
            "content://media/20"
        )

        advanceUntilIdle()

        val evidencia10 = viewModel
            .observarEvidencia(10L)
            .first()

        val evidencia20 = viewModel
            .observarEvidencia(20L)
            .first()

        assertNotNull(evidencia10)
        assertNotNull(evidencia20)

        assertEquals(
            EstadoEvidencia.LOCAL.name,
            evidencia10?.estado
        )

        assertEquals(
            EstadoEvidencia.LOCAL.name,
            evidencia20?.estado
        )
    }
}