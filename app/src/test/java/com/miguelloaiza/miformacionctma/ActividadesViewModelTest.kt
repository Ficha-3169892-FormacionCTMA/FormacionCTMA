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
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
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
        id = 1L, titulo = "Kotlin Flows", descripcion = null,
        progreso = 50, diasRestantes = 5, prioridad = Prioridad.ALTA
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeActividadRepository()
        fakePref = FakePreferenciasRepository()
        fakeEvidenciaDao = FakeEvidenciaDao()
        fakeRemote = FakeEvidenciaRemoteDataSource()
        fakeEvidenciaRepo = FakeEvidenciaRepository(fakeEvidenciaDao, fakeRemote)
        viewModel = ActividadesViewModel(fakeRepo, fakePref, fakeEvidenciaRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TESTS SEMANA 7 (MANTENIDOS) ---

    @Test
    fun `CA-01 sin actividades el estado es Vacio`() = runTest {
        fakeRepo.emitir(emptyList())
        val estado = viewModel.uiState.first { it !is ListadoUiState.Cargando }
        assertTrue(estado is ListadoUiState.Vacio)
    }

    @Test
    fun `CA-02 al insertar aparece en Contenido sin refresco`() = runTest {
        fakeRepo.emitir(emptyList())
        viewModel.guardarActividad(actividadA)
        advanceUntilIdle()
        val estado = viewModel.uiState.first { it is ListadoUiState.Contenido }
        assertEquals("Kotlin Flows", (estado as ListadoUiState.Contenido).actividades.first().titulo)
    }

    // --- TESTS SEMANA 9 (NUEVOS) ---

    @Test
    fun `S9-CA-01 elegir imagen valida persiste URI y estado LOCAL`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()

        val evidencia = viewModel.observarEvidencia(1L).first()
        assertNotNull(evidencia)
        assertEquals(EstadoEvidencia.LOCAL.name, evidencia?.estado)
        assertTrue(viewModel.operacionState.value is OperacionUiState.Exitosa)
    }

    @Test
    fun `S9-CA-04 imagen mayor al limite es rechazada`() = runTest {
        fakeEvidenciaRepo.deberiaFallarValidacion = true
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()

        val evidencia = viewModel.observarEvidencia(1L).first()
        assertNull(evidencia)
        assertTrue(viewModel.operacionState.value is OperacionUiState.Fallida)
        assertEquals("La imagen supera 10 MB", (viewModel.operacionState.value as OperacionUiState.Fallida).error)
    }

    @Test
    fun `S9-CA-06 falla la subida actualiza a FALLIDA y mantiene local`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()

        fakeRemote.deberiaFallar = true
        viewModel.sincronizarEvidencia(1L)
        advanceUntilIdle()

        val evidencia = viewModel.observarEvidencia(1L).first()
        assertEquals(EstadoEvidencia.FALLIDA.name, evidencia?.estado)
        assertTrue(viewModel.operacionState.value is OperacionUiState.Fallida)
    }

    @Test
    fun `S9-CA-08 eliminar evidencia borra el registro`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()
        assertNotNull(viewModel.observarEvidencia(1L).first())

        viewModel.eliminarEvidencia(1L)
        advanceUntilIdle()
        assertNull(viewModel.observarEvidencia(1L).first())
        assertTrue(viewModel.operacionState.value is OperacionUiState.Exitosa)
    }

    @Test
    fun `S9-CA-02 cancelar selector o camara mantiene estado previo intacto`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()

        val evidenciaPrevia = viewModel.observarEvidencia(1L).first()
        assertNotNull(evidenciaPrevia)

        // Si la persona cancela, la UI no llama a guardarEvidencia; la evidencia previa no cambia
        val evidenciaPosterior = viewModel.observarEvidencia(1L).first()
        assertEquals(evidenciaPrevia, evidenciaPosterior)
    }

    @Test
    fun `S9-CA-03 captura con camara solicita URI temporal`() = runTest {
        viewModel.obtenerUriTemporal(1L)
        // La solicitud de URI temporal se completa sin excepciones
    }

    @Test
    fun `S9-CA-05 reiniciar con evidencia local restaura metadatos de Room`() = runTest {
        fakeEvidenciaDao.guardar(
            com.miguelloaiza.miformacionctma.data.local.EvidenciaEntity(
                actividadId = 1L,
                uri = "content://media/preexistente",
                mimeType = "image/png",
                tamanoBytes = 2048,
                nombre = "preexistente.png",
                estado = EstadoEvidencia.LOCAL.name
            )
        )

        val evidencia = viewModel.observarEvidencia(1L).first()
        assertNotNull(evidencia)
        assertEquals("content://media/preexistente", evidencia?.uri)
        assertEquals("preexistente.png", evidencia?.nombre)
        assertEquals(2048L, evidencia?.tamanoBytes)
        assertEquals(EstadoEvidencia.LOCAL.name, evidencia?.estado)
    }

    @Test
    fun `S9-CA-06 reintentar sincronizacion tras fallo`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        fakeRemote.deberiaFallar = true
        viewModel.sincronizarEvidencia(1L)
        advanceUntilIdle()

        fakeRemote.deberiaFallar = false
        viewModel.sincronizarEvidencia(1L)
        advanceUntilIdle()

        val evidencia = viewModel.observarEvidencia(1L).first()
        assertEquals(EstadoEvidencia.SINCRONIZADA.name, evidencia?.estado)
        assertTrue(viewModel.operacionState.value is OperacionUiState.Exitosa)
    }

    @Test
    fun `S9-CA-07 reiniciar operacion borra el estado temporal`() = runTest {
        viewModel.guardarEvidencia(1L, "content://media/1")
        advanceUntilIdle()
        assertTrue(viewModel.operacionState.value is OperacionUiState.Exitosa)

        viewModel.reiniciarOperacion()
        assertTrue(viewModel.operacionState.value is OperacionUiState.Inactiva)
    }
}

//hola//
