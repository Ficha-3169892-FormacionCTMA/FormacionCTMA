package com.miguelloaiza.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.EvidenciaRepository
import com.miguelloaiza.miformacionctma.data.remote.ActividadApi
import com.miguelloaiza.miformacionctma.data.remote.RemoteActividadDataSource
import com.miguelloaiza.miformacionctma.data.local.AppDatabase
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository
import com.miguelloaiza.miformacionctma.ui.screens.ActividadesScreen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Construccion de dependencias (Semana 7: sin Hilt todavia,
        // se instancian aqui a mano y se inyectan via Factory al ViewModel).
        val database = AppDatabase.obtenerInstancia(applicationContext)
        val json = Json { ignoreUnknownKeys = true }
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ActividadApi::class.java)
        val repository = ActividadRepository(database.actividadDao(), RemoteActividadDataSource(api))
        val evidenciaRepository = EvidenciaRepository(contentResolver, database.evidenciaDao())
        val preferencias = PreferenciasRepository(applicationContext)

        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActividadesScreen(
                        repository = repository,
                        preferencias = preferencias,
                        evidenciaRepository = evidenciaRepository
                    )
                }
            }
        }
    }
}
