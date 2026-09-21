# Reporte de Casos de Aceptación - Semana 9

Este reporte documenta el cumplimiento de los criterios de aceptación definidos en el Laboratorio Incremental de la Semana 9.

| Caso | Condición | Resultado Esperado | Estado | Observaciones |
|---|---|---|---|---|
| **CA 01** | Elegir imagen válida vía Photo Picker. | Vista previa visible, URI en Room, estado LOCAL. | ✅ PASA | Validado en `ReglasActividadTest`. |
| **CA 02** | Cancelar selector o cámara. | Estado anterior intacto, mensaje no alarmista. | ✅ PASA | Manejado mediante `ActivityResultLauncher` en `ActividadesScreen`. |
| **CA 03** | Capturar con cámara externa. | Generación de `content://` URI mediante FileProvider. | ✅ PASA | Configurado en `AndroidManifest.xml` y `file_paths.xml`. |
| **CA 04** | Tipo inválido o archivo > 10MB. | Rechazo inmediato, no se persiste en base de datos. | ✅ PASA | Validado en `EvidenciaRepository.guardar`. |
| **CA 05** | Reiniciar app con evidencia local. | Room restaura metadatos y muestra la vista previa. | ✅ PASA | Persistencia garantizada por la entidad `EvidenciaEntity`. |
| **CA 06** | Fallo en la subida (Red/API). | Estado cambia a `FALLIDA`, conserva URI local y permite reintentar. | ✅ PASA | Flujo de estados implementado en `sincronizar()`. |
| **CA 07** | Negar o revocar notificaciones. | App sigue usable, no repite el diálogo intrusivamente. | ✅ PASA | Lógica contextual implementada en Compose. |
| **CA 08** | Eliminar evidencia. | Borra registro en Room y archivo temporal si aplica. | ✅ PASA | Implementado en `EvidenciaRepository.eliminar`. |
| **CA 09** | Ejecutar variante `prodRelease`. | Uso de HTTPS, logging mínimo y URL de producción. | ✅ PASA | Configurado mediante `buildTypes` y `productFlavors` en Gradle. |

---
**Fecha de Verificación:** 20 de Septiembre de 2026
**Responsable:** Aprendiz ADSO
