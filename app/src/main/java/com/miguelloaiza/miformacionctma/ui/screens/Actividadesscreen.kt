package com.miguelloaiza.miformacionctma.ui.screens

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                snackbarHostState.showSnackbar("Operación realizada con éxito")
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
                title = {
                    Text(
                        "Mis actividades",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::actualizarDesdeRed) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                    IconButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else recordatoriosActivos = true
                    }) {
                        Icon(
                            imageVector = if (recordatoriosActivos) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = if (recordatoriosActivos) "Recordatorios activos" else "Recordatorios inactivos",
                            tint = if (recordatoriosActivos) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            preferencias.borrarToken()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar actividad")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                    viewModel.actualizarBusqueda(it)
                },
                label = { Text("Buscar actividad") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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
            Text("Aún no tienes actividades registradas")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Usa el botón + para agregar la primera")
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
            Text("Ocurrió un error: $mensaje")
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            .fillMaxWidth(),
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
                        text = "Prioridad: ${actividad.prioridad.name} · Días restantes: ${actividad.diasRestantes}",
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
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { actividad.progreso / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
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

    val context = LocalContext.current
    var imageBitmap by remember(evidencia?.uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(evidencia?.uri) {
        val uriString = evidencia?.uri
        if (uriString != null) {
            withContext(Dispatchers.IO) {
                runCatching {
                    val uri = Uri.parse(uriString)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }?.let { bitmap ->
                imageBitmap = bitmap
            }
        } else {
            imageBitmap = null
        }
    }

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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Evidencia Fotográfica",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (evidencia != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!,
                            contentDescription = "Vista previa de evidencia",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cargando imagen...", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = evidencia!!.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val tamanoKb = evidencia!!.tamanoBytes / 1024
                            Text(
                                text = if (tamanoKb > 1024) "${tamanoKb / 1024} MB" else "$tamanoKb KB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val estadoColor = when (evidencia!!.estado) {
                            "SINCRONIZADA" -> MaterialTheme.colorScheme.primary
                            "SUBIENDO" -> MaterialTheme.colorScheme.tertiary
                            "FALLIDA" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        }

                        SuggestionChip(
                            onClick = {},
                            label = { Text(evidencia!!.estado, style = MaterialTheme.typography.labelSmall) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = estadoColor.copy(alpha = 0.15f),
                                labelColor = estadoColor
                            )
                        )
                    }
                }
            }
        } else {
            Text(
                "Sin evidencia adjunta",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (evidencia == null) "Elegir foto" else "Reemplazar")
            }

            OutlinedButton(
                onClick = {
                    val uri = viewModel.obtenerUriTemporal(actividadId)
                    if (uri != null) {
                        uriCaptura = uri
                        camara.launch(uri)
                    } else {
                        mensajeLocal = "No se pudo generar archivo temporal"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tomar foto")
            }
        }

        if (evidencia != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                TextButton(onClick = { viewModel.eliminarEvidencia(actividadId) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Borrar foto", color = MaterialTheme.colorScheme.error)
                }

                FilledTonalButton(onClick = { viewModel.sincronizarEvidencia(actividadId) }) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (evidencia!!.estado == "FALLIDA") "Reintentar" else "Sincronizar")
                }
            }
        }

        mensajeLocal?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
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
                    label = { Text("Título") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") }
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
                    label = { Text("Días restantes") },
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
