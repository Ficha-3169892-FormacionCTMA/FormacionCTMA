package com.miguelloaiza.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelloaiza.miformacionctma.data.ActividadRepository
import com.miguelloaiza.miformacionctma.data.AuthRepository
import com.miguelloaiza.miformacionctma.data.EvidenciaRepository
import com.miguelloaiza.miformacionctma.data.local.AppDatabase
import com.miguelloaiza.miformacionctma.data.preferencias.PreferenciasRepository
import com.miguelloaiza.miformacionctma.data.remote.ActividadApi
import com.miguelloaiza.miformacionctma.data.remote.AuthInterceptor
import com.miguelloaiza.miformacionctma.data.remote.RemoteActividadDataSource
import com.miguelloaiza.miformacionctma.data.remote.RetrofitEvidenciaDataSource
import com.miguelloaiza.miformacionctma.ui.screens.ActividadesScreen
import com.miguelloaiza.miformacionctma.ui.screens.LoginScreen
import com.miguelloaiza.miformacionctma.ui.theme.MiFormacionCTMATheme
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Construccion de dependencias
        val database = AppDatabase.obtenerInstancia(applicationContext)
        val json = Json { ignoreUnknownKeys = true }
        val preferencias = PreferenciasRepository(applicationContext)
        
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY 
                    else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(preferencias))
            .addInterceptor(logging)
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
            
        val authRepository = AuthRepository(api, preferencias)
        val repository = ActividadRepository(database.actividadDao(), RemoteActividadDataSource(api))
        val evidenciaRemote = RetrofitEvidenciaDataSource(api, contentResolver)
        val evidenciaRepository = EvidenciaRepository(this, database.evidenciaDao(), evidenciaRemote)

        setContent {
            val estaLogueado by authRepository.estaLogueado().collectAsStateWithLifecycle(initialValue = false)
            
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (estaLogueado) {
                        ActividadesScreen(
                            repository = repository,
                            preferencias = preferencias,
                            evidenciaRepository = evidenciaRepository
                        )
                    } else {
                        LoginScreen(
                            authRepository = authRepository,
                            onLoginSuccess = { /* Automático por el Flow */ }
                        )
                    }
                }
            }
        }
    }
}
