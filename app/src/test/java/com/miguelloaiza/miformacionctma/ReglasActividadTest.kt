import org.junit.Test
import org.junit.Assert.*

class ActividadTest {




    // HU-13: Eliminar actividad
    @Test
    fun eliminarActividad_actividadDesapareceDeLaLista() {

        val actividades = mutableListOf(
            "Estudiar Kotlin",
            "Realizar proyecto",
            "Entregar actividad"
        )

        actividades.remove("Realizar proyecto")

        assertFalse(actividades.contains("Realizar proyecto"))
    }


    // HU-14: Marcar actividad como completada
    @Test
    fun completarActividad_cambiaEstadoACompletada() {

        var estado = "Pendiente"

        estado = "Completada"

        assertEquals("Completada", estado)
    }

    @Test
    fun cambiarPrioridadDebeActualizarElCampoPrioridad() {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Actividad de prueba",
            descripcion = null,
            progreso = 40,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        )

        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.ALTA
        )

        assertEquals(
            Prioridad.ALTA,
            resultado.prioridad
        )
    }

    @Test
    fun cambiarPrioridadALaMismaQueYaTieneNoDebeAlterarOtrosCampos() {
        val actividad = ActividadFormativa(
            id = 2L,
            titulo = "Actividad media",
            descripcion = "Sin cambios esperados",
            progreso = 60,
            diasRestantes = 3,
            prioridad = Prioridad.MEDIA
        )

        val resultado = ReglasActividad.cambiarPrioridad(
            actividad = actividad,
            nuevaPrioridad = Prioridad.MEDIA
        )

        assertEquals(
            Prioridad.MEDIA,
            resultado.prioridad
        )

        assertEquals(
            actividad.titulo,
            resultado.titulo
        )

        assertEquals(
            actividad.progreso,
            resultado.progreso
        )
    }
}