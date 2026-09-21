# Matriz de Riesgos y Controles - Semana 9

**Proyecto:** Mi Formación CTMA
**Resultado de Aprendizaje:** Integrar capacidades del dispositivo con seguridad y mínimo privilegio.

| ID | Riesgo | Probabilidad | Impacto | Control Implementado |
|---|---|---|---|---|
| R-01 | **Acceso excesivo a datos personales:** Solicitar permisos de lectura de toda la galería de fotos. | Alta | Alta | Uso de **Photo Picker** (`PickVisualMedia`), que no requiere permisos de almacenamiento y limita el acceso a un solo archivo. |
| R-02 | **Fuga de rutas de archivos reales:** Exposición de URIs de tipo `file://` que revelan la estructura del sistema. | Media | Alta | Implementación de **FileProvider** para generar URIs seguras `content://`, ocultando la ruta física del archivo. |
| R-03 | **Denegación de Servicio Local (DoS):** Agotamiento del almacenamiento del dispositivo por archivos de evidencia gigantes. | Media | Media | Validación estricta en `EvidenciaRepository` que rechaza archivos superiores a **10 MB** antes de persistir. |
| R-04 | **Inyección de archivos maliciosos:** Carga de ejecutables o scripts disfrazados de imágenes. | Baja | Alta | Validación de **Tipo MIME** (`image/*`) mediante `ContentResolver.getType()` y verificación de lectura del stream. |
| R-05 | **Interceptación de datos en tránsito:** Envío de evidencias a través de canales no cifrados (HTTP). | Alta | Crítica | Configuración de **Network Security Config** con `cleartextTrafficPermitted="false"` forzando HTTPS en producción. |
| R-06 | **Exposición de credenciales en logs:** Filtración de Tokens o URIs sensibles a través de Logcat. | Media | Alta | Configuración dinámica de `HttpLoggingInterceptor` (Level.BASIC/NONE en release) y desactivación de logs sensibles. |
| R-07 | **Persistencia de datos sensibles innecesarios:** Almacenamiento de metadatos de imágenes que contienen ubicación (EXIF) no requerida. | Media | Media | El sistema solo persiste `URI`, `mimeType`, `tamaño` y `nombre` generado; no se procesan ni guardan metadatos EXIF. |
| R-08 | **Notificaciones intrusivas:** Solicitud de permisos de notificación sin contexto o consentimiento claro. | Alta | Baja | Solicitud de `POST_NOTIFICATIONS` **contextual**, disparada únicamente cuando el usuario activa explícitamente los recordatorios. |
