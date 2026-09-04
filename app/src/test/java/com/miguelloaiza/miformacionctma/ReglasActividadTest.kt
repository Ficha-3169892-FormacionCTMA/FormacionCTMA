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


    // HU-14: Marcar actividad como completada-xxx
    @Test
    fun completarActividad_cambiaEstadoACompletada() {

        var estado = "Pendiente"

        estado = "Completada"

        assertEquals("Completada", estado)
    }
}