package com.miguelloaiza.miformacionctma.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.EvidenciaRepository
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository
import com.miguelloaiza.miformacionctma.domain.ActividadFormativa
import com.miguelloaiza.miformacionctma.domain.Prioridad
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModel
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModelFactory
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState
import com.miguelloaiza.miformacionctma.ui.estado.OperacionUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadesScreen(
    repository: ActividadRepository,
    preferencias: PreferenciasRepository,
    evidenciaRepository: EvidenciaRepository,
    viewModel: ActividadesViewModel = viewModel(
        factory = ActividadesViewModelFactory(repository, preferencias, evidenciaRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }
    var recordatoriosActivos by remember { mutableStateOf(false) }
    val permisoNotificaciones = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        recordatoriosActivos = concedido
    }

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
            TopAppBar(
                title = { Text("Mis actividades") },
                actions = {
                    TextButton(onClick = viewModel::actualizarDesdeRed) { Text("Actualizar") }
                    TextButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else recordatoriosActivos = true
                    }) { Text(if (recordatoriosActivos) "Recordatorios ON" else "Recordatorios OFF") }
                    TextButton(onClick = {
                        scope.launch {
                            preferencias.borrarToken()
                        }
                    }) { Text("Salir") }
                }
            )
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
                    viewModel = viewModel,
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
    viewModel: ActividadesViewModel,
    operacionEnCurso: Boolean,
    onEliminar: (ActividadFormativa) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(actividades, key = { it.id }) { actividad ->
            ActividadItem(
                actividad = actividad,
                viewModel = viewModel,
                habilitado = !operacionEnCurso,
                onEliminar = { onEliminar(actividad) }
            )
        }
    }
}

@Composable
private fun ActividadItem(
    actividad: ActividadFormativa,
    viewModel: ActividadesViewModel,
    habilitado: Boolean,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = actividad.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prioridad: ${actividad.prioridad.name} · Días: ${actividad.diasRestantes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onEliminar, enabled = habilitado) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar actividad",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (!actividad.descripcion.isNullOrBlank()) {
                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LinearProgressIndicator(
                progress = { actividad.progreso / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "Progreso: ${actividad.progreso}%",
                style = MaterialTheme.typography.labelSmall
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            EvidenciaControls(actividad.id, viewModel)
        }
    }
}

@Composable
private fun EvidenciaControls(actividadId: Long, viewModel: ActividadesViewModel) {
    val evidencia by viewModel.observarEvidencia(actividadId).collectAsStateWithLifecycle(initialValue = null)
    var uriCaptura by remember { mutableStateOf<Uri?>(null) }
    var mensajeLocal by remember { mutableStateOf<String?>(null) }

    val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.guardarEvidencia(actividadId, uri.toString())
            mensajeLocal = null
        } else {
            mensajeLocal = "Selección cancelada"
        }
    }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) {
            uriCaptura?.let { viewModel.guardarEvidencia(actividadId, it.toString()) }
            mensajeLocal = null
        } else {
            mensajeLocal = "Captura cancelada"
        }
    }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        if (evidencia != null) {
            AndroidView(
                factory = { 
                    ImageView(it).apply { 
                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    } 
                },
                update = { view ->
                    view.post {
                        try {
                            view.setImageURI(Uri.parse(evidencia!!.uri))
                        } catch (e: Exception) {
                            // Error silencioso
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(vertical = 4.dp)
            )
            Text(
                "Evidencia: ${evidencia!!.nombre} (${evidencia!!.estado.lowercase()})",
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            Text("Sin evidencia fotográfica", style = MaterialTheme.typography.labelSmall)
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedButton(onClick = {
                selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(if (evidencia == null) "Elegir imagen" else "Reemplazar") }
            
            OutlinedButton(onClick = {
                val uri = viewModel.obtenerUriTemporal(actividadId)
                uriCaptura = uri
                camara.launch(uri)
            }) { Text("Tomar foto") }
        }

        if (evidencia != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { viewModel.eliminarEvidencia(actividadId) }) { 
                    Text("Borrar foto", color = MaterialTheme.colorScheme.error) 
                }
                TextButton(onClick = { viewModel.sincronizarEvidencia(actividadId) }) {
                    Text(if (evidencia!!.estado == "FALLIDA") "Reintentar" else "Sincronizar")
                }
            }
        }
        
        mensajeLocal?.let { 
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary) 
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
