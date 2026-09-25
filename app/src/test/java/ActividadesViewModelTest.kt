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

        fakeRepo.emitir(listOf(actividad))

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(75, resultado.progreso)
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

        fakeRepo.emitir(listOf(actividad))

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(12, resultado.diasRestantes)
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

        fakeRepo.emitir(listOf(actividad))

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

        fakeRepo.emitir(listOf(actividad))

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


    @Test
    fun `S12-CA-01 actividad conserva su identificador`() = runTest {
        val actividad = ActividadFormativa(
            id = 25L,
            titulo = "Actividad con ID",
            descripcion = "Prueba del identificador",
            progreso = 60,
            diasRestantes = 7,
            prioridad = Prioridad.ALTA
        )

        viewModel.guardarActividad(actividad)
        advanceUntilIdle()

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(25L, resultado.id)
    }

    @Test
    fun `S12-CA-02 actividad conserva su titulo`() = runTest {
        val actividad = ActividadFormativa(
            id = 26L,
            titulo = "Nueva actividad Kotlin",
            descripcion = "Descripcion",
            progreso = 35,
            diasRestantes = 9,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(listOf(actividad))

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertEquals(
            "Nueva actividad Kotlin",
            resultado.titulo
        )
    }

    @Test
    fun `S12-CA-03 actividad permite descripcion nula`() = runTest {
        val actividad = ActividadFormativa(
            id = 27L,
            titulo = "Actividad sin descripcion",
            descripcion = null,
            progreso = 10,
            diasRestantes = 15,
            prioridad = Prioridad.ALTA
        )

        fakeRepo.emitir(listOf(actividad))

        val estado = viewModel.uiState.first {
            it is ListadoUiState.Contenido
        }

        val resultado =
            (estado as ListadoUiState.Contenido)
                .actividades
                .first()

        assertNull(resultado.descripcion)
    }

    @Test
    fun `S12-CA-04 sincronizacion exitosa cambia estado a SINCRONIZADA`() = runTest {
        viewModel.guardarEvidencia(
            2L,
            "content://media/2"
        )

        advanceUntilIdle()

        fakeRemote.deberiaFallar = false

        viewModel.sincronizarEvidencia(2L)

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(2L)
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
    fun `S12-CA-05 guardar evidencia termina con operacion exitosa`() = runTest {
        viewModel.guardarEvidencia(
            3L,
            "content://media/3"
        )

        advanceUntilIdle()

        val evidencia = viewModel
            .observarEvidencia(3L)
            .first()

        assertNotNull(evidencia)

        assertEquals(
            "content://media/3",
            evidencia?.uri
        )

        assertTrue(
            viewModel.operacionState.value
                    is OperacionUiState.Exitosa
        )
    }
}
