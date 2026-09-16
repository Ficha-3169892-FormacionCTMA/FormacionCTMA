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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
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
import java.io.File

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

    // CA-06/CA-07: reacciona a operacionState sin bloquear la UI.
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
                EvidenciaControls(actividad.id, viewModel)
            }
            TextButton(onClick = onEliminar, enabled = habilitado) {
                Text("Eliminar")
            }
        }
    }
}

/** Selección puntual: no solicita permiso de galería ni conserva bytes en Room. */
@Composable
private fun EvidenciaControls(actividadId: Long, viewModel: ActividadesViewModel) {
    val context = LocalContext.current
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
                factory = { ImageView(it).apply { adjustViewBounds = true; scaleType = ImageView.ScaleType.CENTER_CROP } },
                update = { it.setImageURI(Uri.parse(evidencia!!.uri)) },
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
            Text("Evidencia: ${evidencia!!.nombre} (${evidencia!!.estado.lowercase()})")
        } else {
            Text("Sin evidencia fotográfica")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(if (evidencia == null) "Elegir imagen" else "Reemplazar") }
            OutlinedButton(onClick = {
                val carpeta = File(context.cacheDir, "evidencias").apply { mkdirs() }
                val archivo = File(carpeta, "evidencia_${actividadId}_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                uriCaptura = uri
                camara.launch(uri)
            }) { Text("Tomar foto") }
            if (evidencia != null) {
                TextButton(onClick = { viewModel.eliminarEvidencia(actividadId) }) { Text("Eliminar") }
                TextButton(onClick = { viewModel.sincronizarEvidencia(actividadId) }) {
                    Text(if (evidencia!!.estado == "FALLIDA") "Reintentar" else "Sincronizar")
                }
            }
        }
        mensajeLocal?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
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
