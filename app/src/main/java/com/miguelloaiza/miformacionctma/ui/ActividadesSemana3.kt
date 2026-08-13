package com.miguelloaiza.miformacionctma.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.EstadoActividad
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.ReglasActividad
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme

private enum class FiltroActividad(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    COMPLETADAS("Completadas")
}

@Composable
fun Semana3Screen(
    actividades: List<ActividadFormativa>,
    resumenSemana2: String,
    modifier: Modifier = Modifier
) {
    var filtro by remember { mutableStateOf(FiltroActividad.TODAS) }
    var progresoDemo by remember { mutableIntStateOf(60) }

    val filtradas = remember(actividades, filtro) {
        when (filtro) {
            FiltroActividad.TODAS -> actividades
            FiltroActividad.PENDIENTES -> actividades.filter {
                ReglasActividad.estadoActividad(it) != EstadoActividad.COMPLETADA
            }
            FiltroActividad.COMPLETADAS -> actividades.filter {
                ReglasActividad.estadoActividad(it) == EstadoActividad.COMPLETADA
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Mi Formación CTMA",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Semana 3 · Jetpack Compose",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Semana 2 conservada: $resumenSemana2",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroActividad.entries.forEach { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { filtro = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }
        }

        item {
            RecomposicionDemo(
                progreso = progresoDemo,
                onCambiarProgreso = {
                    progresoDemo = if (progresoDemo == 60) 100 else 60
                }
            )
        }

        item {
            Text(
                text = if (filtradas.isEmpty()) "No hay actividades para este filtro" else "Actividades (${filtradas.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (filtradas.isEmpty()) {
            item { EstadoVacio() }
        } else {
            item {
                ListaOGridAdaptativo(actividades = filtradas)
            }
        }

        item { ChecklistAccesibilidad() }
    }
}

@Composable
private fun ListaOGridAdaptativo(
    actividades: List<ActividadFormativa>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val anchoParaGrid = 700.dp
        if (maxWidth >= anchoParaGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                modifier = Modifier.fillMaxWidth().size(520.dp),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(actividades, key = { it.id }) { actividad ->
                    TarjetaActividad(actividad = actividad)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                actividades.forEach { actividad ->
                    TarjetaActividad(actividad = actividad)
                }
            }
        }
    }
}

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val estado = ReglasActividad.estadoActividad(actividad)
    val estadoTexto = estadoTexto(estado)

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                contentDescription = "Abrir actividad ${actividad.titulo}. Estado: $estadoTexto. Progreso: ${actividad.progreso} por ciento"
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = actividad.titulo,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                EstadoIcono(estado)
            }

            actividad.descripcion?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "Entrega: ${diasTexto(actividad.diasRestantes)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(estadoTexto) }
                )
                Text(
                    text = "${actividad.progreso}%",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            LinearProgressIndicator(
                progress = { actividad.progreso.coerceIn(0, 100) / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Prioridad: ${actividad.prioridad.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun EstadoIcono(estado: EstadoActividad) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun EstadoVacio() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sin actividades",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "No existen actividades que coincidan con el filtro seleccionado.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RecomposicionDemo(
    progreso: Int,
    onCambiarProgreso: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Demostración de recomposición",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Progreso de demostración: $progreso%",
                style = MaterialTheme.typography.bodyLarge
            )
            LinearProgressIndicator(
                progress = { progreso / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onCambiarProgreso) {
                Text(text = if (progreso == 60) "Cambiar a 100%" else "Volver a 60%")
            }
            Text(
                text = "Al cambiar el estado observable, Compose recompone las partes que leen este valor; no es necesario reconstruir toda la aplicación.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ChecklistAccesibilidad() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Checklist de accesibilidad",
                style = MaterialTheme.typography.titleMedium
            )
            val items = listOf(
                "Estado comunicado mediante texto, no solo color",
                "Texto adaptable y títulos largos probados",
                "Icono decorativo excluido de la semántica duplicada",
                "Tarjeta con acción y descripción semántica",
                "Componentes Material 3 con objetivos táctiles adecuados"
            )
            items.forEach { texto ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = texto, style = MaterialTheme.typography.bodyMedium)
                }
            }
            HorizontalDivider()
            Text(
                text = "Hallazgo corregido: la tarjeta ahora comunica el estado con texto y semántica, evitando depender únicamente del color.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun estadoTexto(estado: EstadoActividad): String = when (estado) {
    EstadoActividad.PENDIENTE -> "Pendiente"
    EstadoActividad.EN_PROCESO -> "En proceso"
    EstadoActividad.COMPLETADA -> "Completada"
    EstadoActividad.VENCIDA -> "Vencida"
}

private fun diasTexto(dias: Int): String = when {
    dias < 0 -> "vencida hace ${-dias} días"
    dias == 0 -> "vence hoy"
    dias == 1 -> "1 día restante"
    else -> "$dias días restantes"
}

@Preview(showBackground = true)
@Composable
private fun TarjetaPendientePreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(actividadPreview(progreso = 0, dias = 5, titulo = "Actividad pendiente"))
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaCompletadaPreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(actividadPreview(progreso = 100, dias = -2, titulo = "Actividad completada"))
    }
}

@Preview(showBackground = true, fontScale = 1.5f)
@Composable
private fun TarjetaFuenteGrandePreview() {
    MiFormacionCTMATheme {
        TarjetaActividad(actividadPreview(progreso = 60, dias = 2, titulo = "Título largo para comprobar que la tarjeta soporta texto extenso y una fuente ampliada sin perder información importante"))
    }
}

@Preview(showBackground = true, widthDp = 900)
@Composable
private fun ActividadesAnchoAmpliadoPreview() {
    MiFormacionCTMATheme {
        ListaOGridAdaptativo(
            actividades = listOf(
                actividadPreview(20, 5, "Kotlin"),
                actividadPreview(60, 2, "Jetpack Compose"),
                actividadPreview(100, -1, "Pruebas")
            )
        )
    }
}

private fun actividadPreview(
    progreso: Int,
    dias: Int,
    titulo: String
) = ActividadFormativa(
    id = titulo.hashCode().toLong(),
    titulo = titulo,
    descripcion = "Descripción de demostración",
    progreso = progreso,
    diasRestantes = dias,
    prioridad = Prioridad.MEDIA
)
