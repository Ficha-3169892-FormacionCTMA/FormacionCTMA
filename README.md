# Mi Formación CTMA — Semana 2

Proyecto organizado siguiendo la estructura que se utiliza en el proyecto Android de la guía:

```text
app
└── src
    ├── androidTest
    ├── main
    │   ├── java
    │   │   └── com.miguelloaiza.miformacionctma
    │   │       ├── domain
    │   │       │   ├── ActividadFormativa.kt
    │   │       │   ├── EstadoActividad.kt
    │   │       │   └── Prioridad.kt
    │   │       ├── rules
    │   │       │   └── ReglasActividad.kt
    │   │       ├── ui.theme
    │   │       │   ├── Color.kt
    │   │       │   ├── Theme.kt
    │   │       │   └── Type.kt
    │   │       ├── keepRules
    │   │       │   └── KeepRules.kt
    │   │       └── MainActivity.kt
    │   └── res
    └── test
```

## Reglas de la guía implementadas

- `validarActividad`
- `estadoActividad`
- `actividadesUrgentes`
- `promedioProgreso`
- `buscarPorTitulo`

También están incluidos:
- tipos y operadores
- `when`
- funciones
- colecciones
- null safety con `?.`, `?:`, `let`
- ejemplos A, B y C
- reto de ordenamiento
- pruebas unitarias
- integración mínima con Compose

## Escenarios solicitados

1. Título vacío `" "` con progreso 50.
2. Progreso 120.
3. Actividad vencida: progreso 80, días -1.
4. Actividad completa: progreso 100, días -2.
5. Lista vacía.
6. Búsqueda `" kotlin "` sobre `"Kotlin básico"`.

## Importante

La guía indica que las reglas de negocio deben permanecer independientes de la interfaz. Por eso `MainActivity.kt` calcula el resumen mediante `ReglasActividad` y solo muestra el resultado.

No se agregó navegación ni persistencia, porque la guía indica que esta semana todavía no se construyen esas partes.


# Semana 3 — Jetpack Compose

La Semana 3 se implementó sobre la base existente de Semana 2 sin eliminar sus modelos, reglas, validaciones ni pruebas.

## Implementaciones agregadas
- `TarjetaActividad` parametrizada y reutilizable.
- 4 Previews para escenarios de estado, texto largo, fontScale 1.5 y ancho ampliado.
- 10 actividades de demostración basadas en `ActividadFormativa`.
- Estado vacío.
- Lista compacta y `LazyVerticalGrid` adaptativo.
- Selector local Todas / Pendientes / Completadas.
- Manejo semántico de iconos y tarjetas.
- Checklist de accesibilidad y corrección documentada.
- Demostración de recomposición cambiando progreso de 60% a 100%.

## Umbral de adaptación
El umbral usado para la demostración es 700dp. En pantallas compactas se conserva una lista de una columna; en anchos mayores se habilita una cuadrícula con tarjetas de mínimo 280dp.

## Base de Semana 2 conservada
`ActividadFormativa`, `EstadoActividad`, `Prioridad`, `ResultadoRegistro`, `ReglasActividad`, `ValidadorActividad`, pruebas unitarias y el resumen de negocio continúan presentes y se reutilizan desde la interfaz de Semana 3.

---

# Semana 7 — Corrutinas, Flow, StateFlow y ciclo de vida

Rama: `feature/semana-07-coroutines-flow`

## Arquitectura

```
Room (Flow<Entity>) ──► ActividadRepository (Flow<Dominio>, suspend)
DataStore (Flow<String>) ──► PreferenciasRepository
                                      │
                              ActividadesViewModel
                              ├── combine() + flatMapLatest()
                              ├── debounce(300ms) para búsqueda
                              ├── stateIn(WhileSubscribed 5s)
                              ├── uiState: StateFlow<ListadoUiState>
                              └── operacionState: StateFlow<OperacionUiState>
                                      │
                              ActividadesScreen (Compose)
                              └── collectAsStateWithLifecycle()
```

## Estados implementados

| Estado listado | Estado operación |
|---|---|
| `Cargando` | `Inactiva` |
| `Contenido(actividades)` | `EnCurso` |
| `Vacio` | `Exitosa` |
| `Error(mensaje)` | `Fallida(error)` |

## Decisiones de dispatcher

| Capa | Dispatcher | Justificación |
|---|---|---|
| Room DAO | IO (interno de Room) | Room maneja su propio dispatcher |
| DataStore | IO (interno) | DataStore es main-safe |
| Repository | Sin cambio (main-safe) | Delega al DAO/DataStore |
| ViewModel | `viewModelScope` (Main) | Transforma flujos; no bloquea |
| Guardado/eliminación | `viewModelScope.launch` | Room es main-safe con `room-ktx` |

**Regla aplicada:** La UI no crea `CoroutineScope`, no usa `GlobalScope` ni decide el dispatcher de datos.

## Casos de aceptación (CA-01 a CA-08)

| Caso | Descripción | Verificado |
|---|---|---|
| CA-01 | Sin actividades → Cargando → Vacío | ✅ Test |
| CA-02 | Insertar → lista actualiza sin refresco | ✅ Test |
| CA-03 | Filtro cambia → combine recalcula | ✅ Test |
| CA-04 | Búsquedas rápidas → solo gana la última | ✅ Test |
| CA-05 | Fallo del repository → Error con Reintentar | ✅ Test |
| CA-06 | Salir durante operación → Job se cancela con el scope | ✅ Manual |
| CA-07 | Girar pantalla → StateFlow conserva estado | ✅ Test |
| CA-08 | Suite sin Thread.sleep | ✅ runTest + advanceUntilIdle |

## Pruebas

```bash
./gradlew test
```

- `ActividadesViewModelTest` — 7 pruebas unitarias con `runTest`
- Usan `FakeActividadRepository` y `FakePreferenciasRepository`
- Sin delay real, sin base de datos, sin Android Context

## Uso de IA

Se utilizó Antigravity (IA) para:
- Revisar la arquitectura existente de Semana 6 y diagnosticar qué faltaba
- Generar los repositorios falsos (`FakeActividadRepository`, `FakePreferenciasRepository`)
- Escribir los tests con `runTest` y `advanceUntilIdle`
- Actualizar el README

Todo el código generado fue revisado y validado contra la compilación real del proyecto (`./gradlew compileDebugKotlin` exitoso). Las decisiones de arquitectura (dispatchers, `stateIn`, `CancellationException`) corresponden a las semanas anteriores y al conocimiento propio del equipo.

# Semana 9 — Capacidades del dispositivo y seguridad

Cada actividad puede adjuntar una única evidencia. La app usa el selector de fotos del sistema y `TakePicture` con un `content://` de `FileProvider`; no pide acceso general a la galería. Room solo conserva URI, tipo MIME, tamaño, nombre y estado, nunca un Bitmap ni Base64.

## Documentación de la Semana 9
- [Matriz de Riesgos y Controles](docs/Seguridad/MatrizRiesgosSemana9.md)
- [Reporte de Casos de Aceptación](docs/Pruebas/ReporteCasosAceptacionS9.md)

## Ambientes de Ejecución (Build Variants)
El proyecto cuenta con tres variantes de ambiente configuradas en Gradle:
- **dev**: Para desarrollo local. Usa `http://10.0.2.2:8080/`.
- **stage**: Ambiente de pruebas/QA. Usa HTTPS.
- **prod**: Producción. Forzado HTTPS y seguridad máxima.

Para cambiar de ambiente, use la pestaña **Build Variants** en Android Studio.

## Pruebas manuales
1. Crear una actividad, elegir una imagen y comprobar la vista previa.
2. Cancelar selector o cámara y comprobar que el estado anterior permanece.
3. Tomar una foto y comprobar que no se comparte ningún `file://`.
4. Probar archivo no-imagen o mayor de 10 MB; debe rechazarse.
5. Reiniciar la app y comprobar que la evidencia local persiste.
6. Pulsar Sincronizar sin configurar servicio; debe quedar `FALLIDA`, conservar la evidencia y permitir Reintentar.
7. Activar recordatorios en Android 13+; el permiso se solicita solo al pulsar el botón.
8. Eliminar la evidencia y comprobar que desaparece de Room.
9. Ejecutar `prodRelease` y comprobar HTTPS/configuración sin secretos.

## Matriz de riesgos y controles

| Riesgo | Control |
|---|---|
| Acceso masivo a galería | `PickVisualMedia` sin permiso de almacenamiento |
| Exposición de `file://` | `FileProvider` limitado a `cache/evidencias` |
| Archivo malicioso | Validación MIME `image/*` y lectura verificable |
| Archivo excesivo | Límite de 10 MB antes de persistir |
| Pérdida de evidencia por red | Estados Local/Subiendo/Sincronizada/Fallida y reintento |
| URI o token en logs | No se registra información de evidencia ni credenciales |
| HTTP en producción | `usesCleartextTraffic=false` y configuración de red |
| Secretos embebidos | URLs por variantes; ningún token en BuildConfig |
| Notificaciones invasivas | Permiso contextual, solo tras acción de la persona |
| Evidencia ajena o sensible | Vista previa y eliminación antes de sincronizar |
