# Casos de Prueba – Mi Formación CTMA

## Objetivo

Documentar los casos de prueba utilizados para verificar las reglas de negocio y funcionalidades principales de Mi Formación CTMA.

## Convenciones

- **PASS:** El resultado obtenido coincide con el resultado esperado.
- **FAIL:** El resultado obtenido no coincide con el resultado esperado.
- **N/A:** No aplica al caso.

---

## CP-01 – Validar título vacío

**Historia relacionada:** HU-09  
**Tipo:** Prueba unitaria

**Precondición:** La función de validación está disponible.

**Entrada:**
- Título: `" "`
- Progreso: `50`

**Resultado esperado:**
- Se debe generar el error `"El título es obligatorio"`.

**Resultado obtenido:** Se genera el error esperado.

**Estado:** PASS

---

## CP-02 – Validar progreso fuera del rango permitido

**Historia relacionada:** HU-09  
**Tipo:** Prueba unitaria

**Precondición:** La función de validación está disponible.

**Entrada:**
- Título: `"Actividad de prueba"`
- Progreso: `120`

**Resultado esperado:**
- Se debe generar el error `"El progreso debe estar entre 0 y 100"`.

**Resultado obtenido:** Se genera el error esperado.

**Estado:** PASS

---

## CP-03 – Identificar actividad vencida

**Historia relacionada:** HU-03  
**Tipo:** Prueba unitaria

**Entrada:**
- Progreso: `80`
- Días restantes: `-1`
- Prioridad: Alta

**Resultado esperado:**
- El estado debe ser `VENCIDA`.

**Resultado obtenido:** La actividad es identificada como vencida.

**Estado:** PASS

---

## CP-04 – Identificar actividad completada

**Historia relacionada:** HU-03  
**Tipo:** Prueba unitaria

**Entrada:**
- Progreso: `100`
- Días restantes: `-2`
- Prioridad: Media

**Resultado esperado:**
- El estado debe ser `COMPLETADA`, aunque los días restantes sean negativos.

**Resultado obtenido:** La actividad es identificada como completada.

**Estado:** PASS

---

## CP-05 – Calcular promedio con lista vacía

**Historia relacionada:** HU-05  
**Tipo:** Prueba unitaria

**Entrada:**
- Lista de actividades vacía.

**Resultado esperado:**
- El promedio debe ser `0.0`.

**Resultado obtenido:** El resultado es `0.0`.

**Estado:** PASS

---

## CP-06 – Buscar actividad ignorando mayúsculas y espacios

**Historia relacionada:** HU-06  
**Tipo:** Prueba unitaria

**Datos:**
- `"Kotlin básico"`
- `"Android Studio"`

**Entrada de búsqueda:**
- `" kotlin "`

**Resultado esperado:**
- Debe encontrarse la actividad `"Kotlin básico"`.
- La búsqueda debe ignorar mayúsculas/minúsculas y espacios externos.

**Resultado obtenido:** Se encuentra `"Kotlin básico"`.

**Estado:** PASS

---

## CP-07 – Ordenar actividades por prioridad

**Historia relacionada:** HU-07  
**Tipo:** Prueba unitaria

**Datos de prueba:**

| ID | Actividad | Progreso | Días restantes | Prioridad |
|---|---|---:|---:|---|
| 1 | Actividad normal | 40 | 5 | Baja |
| 2 | Actividad urgente | 50 | 2 | Alta |
| 3 | Actividad vencida | 80 | -1 | Media |
| 4 | Actividad alta cercana | 30 | 1 | Alta |

**Resultado esperado:**

El orden de los ID debe ser:

`3, 4, 2, 1`

**Resultado obtenido:**

`3, 4, 2, 1`

**Estado:** PASS

---

## CP-08 – Identificar actividad urgente

**Historia relacionada:** HU-04  
**Tipo:** Prueba de regla de negocio

**Entrada:**
- Actividad no completada.
- Días restantes: `2`.

**Resultado esperado:**
- La actividad debe considerarse urgente.

**Resultado obtenido:** La regla de actividades urgentes considera la actividad dentro del resultado.

**Estado:** PASS

---

## CP-09 – Calcular resumen de progreso

**Historia relacionada:** HU-08  
**Tipo:** Prueba de regla de negocio

**Entrada:**
- Lista de actividades con diferentes porcentajes de progreso.

**Resultado esperado:**
- El resumen debe incluir:
    - promedio de progreso,
    - cantidad de actividades completadas,
    - cantidad de actividades urgentes.

**Resultado obtenido:** El resumen contiene los datos calculados.

**Estado:** PASS

---

## CP-10 – Obtener nombre visible

**Historia relacionada:** HU-10  
**Tipo:** Prueba de regla de negocio

**Entrada:**
- Nombre completo vacío.
- Alias disponible.

**Resultado esperado:**
- Se debe utilizar el alias como nombre visible.

**Resultado obtenido:** Se utiliza el alias cuando el nombre completo no contiene información válida.

**Estado:** PASS

---

## CP-11 – Mostrar actividades en la interfaz

**Historia relacionada:** HU-01  
**Tipo:** Prueba funcional/instrumentada

**Precondición:**
- La aplicación se encuentra instalada y ejecutándose en el emulador.

**Pasos:**
1. Iniciar la aplicación.
2. Visualizar la pantalla principal.
3. Revisar las actividades mostradas.

**Resultado esperado:**
- Se debe mostrar el título de la aplicación.
- Se deben mostrar las actividades disponibles.
- Cada actividad debe mostrar información de progreso, estado, entrega y prioridad.

**Resultado obtenido:** Las actividades se muestran correctamente en el emulador.

**Estado:** PASS

---

## CP-12 – Filtrar actividades

**Historia relacionada:** HU-01  
**Tipo:** Prueba funcional

**Pasos:**
1. Abrir la aplicación.
2. Seleccionar el filtro "Pendientes".
3. Seleccionar el filtro "Completadas".
4. Seleccionar el filtro "Todas".

**Resultado esperado:**
- "Pendientes" debe mostrar actividades no completadas.
- "Completadas" debe mostrar únicamente actividades completadas.
- "Todas" debe mostrar todas las actividades.

**Resultado obtenido:** Los filtros funcionan según el estado de las actividades.

**Estado:** PASS

---

## Resumen de resultados

| Caso | Descripción | Estado |
|---|---|---|
| CP-01 | Validar título vacío | PASS |
| CP-02 | Validar progreso inválido | PASS |
| CP-03 | Actividad vencida | PASS |
| CP-04 | Actividad completada | PASS |
| CP-05 | Promedio de lista vacía | PASS |
| CP-06 | Búsqueda por título | PASS |
| CP-07 | Ordenamiento de actividades | PASS |
| CP-08 | Actividad urgente | PASS |
| CP-09 | Resumen de progreso | PASS |
| CP-10 | Nombre visible | PASS |
| CP-11 | Mostrar actividades | PASS |
| CP-12 | Filtrar actividades | PASS |

## Evidencias

Las evidencias de ejecución pueden incluir:

- Resultados de las pruebas unitarias de `ReglasActividadTest`.
- Capturas de la aplicación ejecutándose en el emulador.
- Capturas de las funcionalidades probadas.
- Resultados de pruebas posteriores a modificaciones o correcciones.

## Relación con el código

Las pruebas unitarias de las reglas de negocio se encuentran en:

`app/src/test/java/com/miguelloaiza/miformacionctma/ReglasActividadTest.kt`

Las reglas probadas se encuentran principalmente en:

`app/src/main/java/com/miguelloaiza/miformacionctma/rules/ReglasActividad.kt`