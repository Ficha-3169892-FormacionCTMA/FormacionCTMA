package com.miguelloaiza.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModel
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModelFactory
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState

/**
 * Pantalla principal de actividades.
 * Recolecta uiState (listado) y operacionState (guardar/eliminar) de forma
 * consciente del ciclo de vida, y no crea ningun CoroutineScope propio:
 * toda la logica asincrona vive en el ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(
    repository: ActividadRepository,
    preferencias: PreferenciasRepository,
    viewModel: ActividadesViewModel = viewModel(
        factory = ActividadesViewModelFactory(repository, preferencias)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }

    // CA-06/CA-07: reacciona a operacionState sin bloquear la UI.
    // Al ser un LaunchedEffect atado a la clave operacionState, se relanza
    // (y cancela el anterior) cada vez que el estado cambia.
    LaunchedEffect(operacionState) {
        when (val estado = operacionState) {
            is OperacionUiState.Exitosa -> {
                snackbarHostState.showSnackbar("Operacion realizada con exito")
                mostrarFormulario = false
                viewModel.reiniciarOperacion()
            }
            is OperacionUiState.Fallida -> {
                snackbarHostState.showSnackbar("Error: ${estado.error}")
                viewModel.reiniciarOperacion()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Mis actividades") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                    viewModel.actualizarBusqueda(it)
                },
                label = { Text("Buscar actividad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val estado = uiState) {
                is ListadoUiState.Cargando -> EstadoCargando()
                is ListadoUiState.Vacio -> EstadoVacio()
                is ListadoUiState.Error -> EstadoError(
                    mensaje = estado.mensaje,
                    onReintentar = viewModel::reintentar
                )
                is ListadoUiState.Contenido -> ListaActividades(
                    actividades = estado.actividades,
                    operacionEnCurso = operacionState is OperacionUiState.EnCurso,
                    onEliminar = viewModel::eliminarActividad
                )
            }
        }

        if (mostrarFormulario) {
            FormularioActividad(
                enviando = operacionState is OperacionUiState.EnCurso,
                onCancelar = { mostrarFormulario = false },
                onGuardar = { actividad -> viewModel.guardarActividad(actividad) }
            )
        }
    }
}

@Composable
private fun EstadoCargando() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cargando actividades...")
        }
    }
}

@Composable
private fun EstadoVacio() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Aun no tienes actividades registradas")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Usa el boton + para agregar la primera")
        }
    }
}

@Composable
private fun EstadoError(mensaje: String, onReintentar: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ocurrio un error: $mensaje")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun ListaActividades(
    actividades: List<ActividadFormativa>,
    operacionEnCurso: Boolean,
    onEliminar: (ActividadFormativa) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(actividades, key = { it.id }) { actividad ->
            ActividadItem(
                actividad = actividad,
                habilitado = !operacionEnCurso,
                onEliminar = { onEliminar(actividad) }
            )
        }
    }
}

@Composable
private fun ActividadItem(
    actividad: ActividadFormativa,
    habilitado: Boolean,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(actividad.titulo, fontWeight = FontWeight.Bold)
                if (!actividad.descripcion.isNullOrBlank()) {
                    Text(actividad.descripcion)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Progreso: ${actividad.progreso}%  ·  Prioridad: ${actividad.prioridad.name}")
                Text("Dias restantes: ${actividad.diasRestantes}")
            }
            TextButton(onClick = onEliminar, enabled = habilitado) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
private fun FormularioActividad(
    enviando: Boolean,
    onCancelar: () -> Unit,
    onGuardar: (ActividadFormativa) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var progresoTexto by remember { mutableStateOf("0") }
    var diasTexto by remember { mutableStateOf("0") }
    var prioridad by remember { mutableStateOf(Prioridad.MEDIA) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripcion (opcional)") }
                )
                OutlinedTextField(
                    value = progresoTexto,
                    onValueChange = { progresoTexto = it.filter { c -> c.isDigit() } },
                    label = { Text("Progreso (0-100)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = diasTexto,
                    onValueChange = { diasTexto = it.filter { c -> c.isDigit() } },
                    label = { Text("Dias restantes") },
                    singleLine = true
                )
                Text("Prioridad")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Prioridad.entries.forEach { opcion ->
                        FilterChip(
                            selected = prioridad == opcion,
                            onClick = { prioridad = opcion },
                            label = { Text(opcion.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = titulo.isNotBlank() && !enviando,
                onClick = {
                    onGuardar(
                        ActividadFormativa(
                            id = 0L,
                            titulo = titulo,
                            descripcion = descripcion.ifBlank { null },
                            progreso = progresoTexto.toIntOrNull() ?: 0,
                            diasRestantes = diasTexto.toIntOrNull() ?: 0,
                            prioridad = prioridad
                        )
                    )
                }
            ) {
                Text(if (enviando) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, enabled = !enviando) {
                Text("Cancelar")
            }
        }
    )
}
