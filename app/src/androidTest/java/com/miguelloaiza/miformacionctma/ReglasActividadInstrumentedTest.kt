package com.miguelloaiza.miformacionctma

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.ui.Semana3Screen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReglasActividadInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val actividadesPrueba = listOf(
        ActividadFormativa(
            id = 1L,
            titulo = "Actividad completada",
            descripcion = "Actividad finalizada",
            progreso = 100,
            diasRestantes = -2,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 2L,
            titulo = "Actividad pendiente",
            descripcion = "Actividad pendiente de realizar",
            progreso = 0,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 3L,
            titulo = "Actividad en proceso",
            descripcion = "Actividad actualmente en desarrollo",
            progreso = 60,
            diasRestantes = 2,
            prioridad = Prioridad.MEDIA
        )
    )

    private fun cargarPantalla() {
        composeTestRule.setContent {
            MiFormacionCTMATheme {
                Semana3Screen(
                    actividades = actividadesPrueba,
                    resumenSemana2 = "Pruebas completadas"
                )
            }
        }
    }

    @Test
    fun pantallaDebeMostrarTituloYActividades() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Mi Formación CTMA")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Semana 3 · Jetpack Compose")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad completada")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad pendiente")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad en proceso")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun filtroCompletadasDebeMostrarSoloCompletadas() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Completadas")
            .performClick()

        composeTestRule
            .onNodeWithText("Actividad completada")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithText("Actividad pendiente")
            .assertCountEquals(0)

        composeTestRule
            .onAllNodesWithText("Actividad en proceso")
            .assertCountEquals(0)
    }

    @Test
    fun filtroPendientesDebeOcultarCompletadas() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Pendientes")
            .performClick()

        composeTestRule
            .onAllNodesWithText("Actividad completada")
            .assertCountEquals(0)

        composeTestRule
            .onNodeWithText("Actividad pendiente")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad en proceso")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun filtroTodasDebeMostrarTodasLasActividades() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Todas")
            .performClick()

        composeTestRule
            .onNodeWithText("Actividad completada")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad pendiente")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Actividad en proceso")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun demostracionDeRecomposicionDebeCambiarElProgreso() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Progreso de demostración: 60%")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Cambiar a 100%")
            .performClick()

        composeTestRule
            .onNodeWithText("Progreso de demostración: 100%")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Volver a 60%")
            .assertIsDisplayed()
    }

    @Test
    fun tarjetaDebeTenerDescripcionSemanticaYAccion() {
        cargarPantalla()

        composeTestRule
            .onNodeWithContentDescription(
                "Abrir actividad Actividad completada. Estado: Completada. Progreso: 100 por ciento"
            )
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun tarjetasDebenMostrarEstadoYProgreso() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Completada")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pendiente")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("En proceso")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("100%")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("60%")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun tarjetasDebenMostrarPrioridad() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Prioridad: Alta")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Prioridad: Baja")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Prioridad: Media")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun debeMostrarMensajeCuandoNoHayResultados() {
        val soloPendiente = listOf(
            ActividadFormativa(
                id = 10L,
                titulo = "Actividad única",
                descripcion = "Prueba de estado vacío",
                progreso = 0,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            )
        )

        composeTestRule.setContent {
            MiFormacionCTMATheme {
                Semana3Screen(
                    actividades = soloPendiente,
                    resumenSemana2 = "Sin actividades completadas"
                )
            }
        }

        composeTestRule
            .onNodeWithText("Completadas")
            .performClick()

        composeTestRule
            .onNodeWithText("No hay actividades para este filtro")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sin actividades")
            .assertIsDisplayed()
    }

    @Test
    fun debeMostrarTextoDeEntrega() {
        cargarPantalla()

        composeTestRule
            .onNodeWithText("Entrega: 5 días restantes")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Entrega: 2 días restantes")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Entrega: vencida hace 2 días")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun checklistDeAccesibilidadDebeEstarVisible() {
        cargarPantalla()

        composeTestRule
            .onNode(hasScrollToNodeAction())
            .performScrollToNode(
                hasText("Checklist de accesibilidad")
            )

        composeTestRule
            .onNodeWithText("Checklist de accesibilidad")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Estado comunicado mediante texto, no solo color"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Tarjeta con acción y descripción semántica"
            )
            .assertIsDisplayed()
    }
}