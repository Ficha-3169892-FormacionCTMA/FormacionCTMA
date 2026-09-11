
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModel
import com.miguelloaiza.miformacionctma.rules.viewmodel.ActividadesViewModelFactory
import com.miguelloaiza.miformacionctma.ui.estado.ListadoUiState

@Composable
fun PantallaActividades(
    viewModel: ActividadesViewModel = viewModel(
        factory = ActividadesViewModelFactory(LocalContext.current)
    )
) {
    // collectAsStateWithLifecycle: deja de recolectar cuando la pantalla no está
    // visible (ej. app en background) y vuelve a recolectar al reanudar,
    // en vez de mantener la suscripción viva todo el tiempo como collectAsState.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(tonalElevation = 3.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "Mi Formación CTMA - Actividades",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val estado = uiState) {

                is ListadoUiState.Cargando -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cargando actividades...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is ListadoUiState.Vacio -> {
                    // CA-01 / "vacío con acción": no solo informa, ofrece una
                    // acción concreta en vez de dejar la pantalla en silencio.
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay actividades registradas.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.actualizarBusqueda("") }) {
                            Text("Actualizar")
                        }
                    }
                }

                is ListadoUiState.Error -> {
                    // CA-05: error legible + botón de reintento visible.
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${estado.mensaje}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.reintentar() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is ListadoUiState.Contenido -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Total de actividades: ${estado.actividades.size}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        estado.actividades.forEach { actividad ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = actividad.titulo,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
 