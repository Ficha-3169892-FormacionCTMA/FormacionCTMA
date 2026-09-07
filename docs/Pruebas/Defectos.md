# Registro de Defectos – Mi Formación CTMA

## Objetivo

Registrar los defectos o problemas encontrados durante las actividades de compilación, ejecución y pruebas del proyecto, indicando su estado y las acciones realizadas para solucionarlos.

## Estados utilizados

- **Abierto:** El problema está pendiente de solución.
- **En proceso:** Se está trabajando en la solución.
- **Corregido:** Se aplicó una solución.
- **Cerrado:** Se verificó la solución y no volvió a presentarse.

---

## DEF-01 – Bloqueo de archivos generados durante la compilación

**Fecha:** 31/08/2026

**Tipo:** Problema técnico de compilación.

**Descripción:**

Durante la ejecución del proyecto se presentó un error de Gradle relacionado con la imposibilidad de eliminar un directorio dentro de:

`app/build/intermediates`

El mensaje mostrado fue:

`Unable to delete directory`

El problema estaba relacionado con archivos generados durante la compilación que se encontraban bloqueados.

**Impacto:**

Impedía completar correctamente una compilación del proyecto.

**Pasos realizados:**

1. Se cerró la ejecución del proyecto.
2. Se cerró Android Studio.
3. Se eliminaron las carpetas generadas `build` del proyecto y del módulo `app`.
4. Se abrió nuevamente el proyecto.
5. Se ejecutó una nueva compilación.
6. Se verificó la ejecución de la aplicación en el emulador.

**Resultado:**

La compilación terminó correctamente con:

`BUILD SUCCESSFUL`

Posteriormente la aplicación se ejecutó correctamente en el emulador.

**Estado:** Cerrado

**Tipo de solución:** Limpieza de archivos generados por Gradle.

**Regresión:** Realizada. La aplicación volvió a compilar y ejecutar correctamente después de la solución.

---

## DEF-02 – Sin defectos funcionales críticos registrados

**Descripción:**

Durante la validación realizada hasta el momento no se ha identificado un defecto funcional crítico que impida utilizar las funcionalidades principales de la aplicación.

**Estado:** Cerrado

**Observación:**

La ausencia de defectos registrados no significa que no puedan aparecer nuevos defectos durante futuras pruebas. Los nuevos problemas encontrados deberán registrarse en este documento.

---

## Seguimiento de defectos

| ID | Descripción | Tipo | Estado | Regresión |
|---|---|---|---|---|
| DEF-01 | Bloqueo de archivos generados de Gradle | Técnico | Cerrado | PASS |
| DEF-02 | Sin defectos funcionales críticos registrados | Funcional | Cerrado | PASS |

## Proceso de corrección

Cuando se encuentre un nuevo defecto se seguirá el siguiente proceso:

1. Registrar el defecto.
2. Describir el comportamiento observado.
3. Identificar el impacto.
4. Aplicar una corrección.
5. Ejecutar nuevamente las pruebas relacionadas.
6. Realizar pruebas de regresión.
7. Registrar el resultado.
8. Cerrar el defecto cuando la solución haya sido verificada.

## Relación con las pruebas

Los defectos encontrados durante la ejecución de los casos de prueba deberán relacionarse con el caso de prueba correspondiente.

El flujo utilizado será:

Caso de prueba → Defecto → Corrección → Prueba de regresión → Cierre.