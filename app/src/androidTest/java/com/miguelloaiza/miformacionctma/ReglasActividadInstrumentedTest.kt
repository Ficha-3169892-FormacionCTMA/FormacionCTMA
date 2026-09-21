package com.miguelloaiza.miformacionctma

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReglasActividadInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun marcadorDePosicion() {
        // Test basico para evitar errores de compilacion mientras se configuran los fakes
        assertTrue(true)
    }
}
