# Product Backlog – Mi Formación CTMA

## Producto

Mi Formación CTMA

## Objetivo

Gestionar y consultar actividades formativas, permitiendo al usuario conocer su progreso, estado, prioridad y tiempo restante, además de identificar actividades urgentes y consultar resúmenes de avance.

## Historias de usuario

### HU-01 – Consultar actividades formativas

**Como** usuario de Mi Formación CTMA  
**Quiero** visualizar mis actividades formativas  
**Para** conocer las actividades que tengo registradas.

**Criterios de aceptación:**
- Se deben mostrar las actividades formativas disponibles.
- Cada actividad debe mostrar información relevante.
- La información debe presentarse de forma clara.
- La aplicación debe funcionar correctamente al iniciar.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-02 – Consultar el progreso de una actividad

**Como** usuario  
**Quiero** conocer el porcentaje de progreso de cada actividad  
**Para** saber cuánto he avanzado.

**Criterios de aceptación:**
- El progreso debe representarse mediante un valor entre 0 y 100.
- Un progreso de 100 representa una actividad completada.
- Los valores inválidos deben ser rechazados.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-03 – Identificar el estado de una actividad

**Como** usuario  
**Quiero** conocer el estado de cada actividad  
**Para** saber cuáles están pendientes, en proceso, completadas o vencidas.

**Criterios de aceptación:**
- Una actividad con progreso igual a 0 debe considerarse pendiente.
- Una actividad con progreso entre 1 y 99 debe considerarse en proceso.
- Una actividad con progreso igual a 100 debe considerarse completada.
- Una actividad con días restantes menores que 0 debe considerarse vencida, excepto cuando ya está completada.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-04 – Identificar actividades urgentes

**Como** usuario  
**Quiero** identificar las actividades que requieren atención  
**Para** priorizar las que tienen poco tiempo disponible.

**Criterios de aceptación:**
- Una actividad no completada con 2 días o menos debe considerarse urgente.
- Las actividades completadas no deben aparecer como urgentes.
- Las actividades vencidas pueden ser identificadas como urgentes según sus días restantes.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-05 – Consultar el promedio de progreso

**Como** usuario  
**Quiero** conocer el promedio de progreso de mis actividades  
**Para** tener una visión general de mi avance.

**Criterios de aceptación:**
- El promedio debe calcularse utilizando los porcentajes de progreso.
- Si no existen actividades, el resultado debe ser 0.
- El cálculo debe producir un valor numérico válido.

**Prioridad:** Media  
**Estado:** Implementada

---

### HU-06 – Buscar actividades por título

**Como** usuario  
**Quiero** buscar una actividad por su título  
**Para** encontrarla rápidamente.

**Criterios de aceptación:**
- La búsqueda debe utilizar el título de la actividad.
- La búsqueda debe ignorar mayúsculas y minúsculas.
- Se deben ignorar los espacios externos de la consulta.
- Una consulta vacía debe devolver una lista vacía.

**Prioridad:** Media  
**Estado:** Implementada

---

### HU-07 – Priorizar actividades

**Como** usuario  
**Quiero** visualizar las actividades ordenadas por importancia  
**Para** atender primero las que requieren mayor atención.

**Criterios de aceptación:**
- Las actividades vencidas deben aparecer primero.
- Las actividades de prioridad alta deben tener prioridad sobre las demás.
- Cuando corresponda, deben ordenarse considerando los días restantes.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-08 – Consultar resumen de avance

**Como** usuario  
**Quiero** consultar un resumen de mis actividades  
**Para** conocer rápidamente mi promedio, actividades completadas y actividades urgentes.

**Criterios de aceptación:**
- El resumen debe mostrar el promedio de progreso.
- Debe mostrar la cantidad de actividades completadas.
- Debe mostrar la cantidad de actividades urgentes.
- Si no existen datos, debe mostrar "Sin datos".

**Prioridad:** Media  
**Estado:** Implementada

---

### HU-09 – Validar datos de una actividad

**Como** usuario  
**Quiero** que los datos de una actividad sean validados  
**Para** evitar información incorrecta.

**Criterios de aceptación:**
- El título no puede estar vacío.
- El progreso debe estar entre 0 y 100.
- Se deben informar todos los errores encontrados.

**Prioridad:** Alta  
**Estado:** Implementada

---

### HU-10 – Mostrar información de usuario

**Como** usuario  
**Quiero** disponer de un nombre visible  
**Para** identificar al usuario en la aplicación.

**Criterios de aceptación:**
- Si existe un nombre completo válido, debe utilizarse.
- Si el nombre completo está vacío o no existe, debe utilizarse el alias.
- Los espacios externos deben ignorarse.

**Prioridad:** Baja  
**Estado:** Implementada

---

## Orden de prioridad

| ID | Historia | Prioridad | Estado |
|---|---|---|---|
| HU-01 | Consultar actividades formativas | Alta | Implementada |
| HU-02 | Consultar progreso | Alta | Implementada |
| HU-03 | Identificar estado | Alta | Implementada |
| HU-04 | Identificar actividades urgentes | Alta | Implementada |
| HU-05 | Consultar promedio de progreso | Media | Implementada |
| HU-06 | Buscar por título | Media | Implementada |
| HU-07 | Priorizar actividades | Alta | Implementada |
| HU-08 | Consultar resumen | Media | Implementada |
| HU-09 | Validar datos | Alta | Implementada |
| HU-10 | Mostrar información de usuario | Baja | Implementada |

## Definition of Ready

Una historia puede pasar a desarrollo cuando:

- Tiene un objetivo claro.
- Tiene criterios de aceptación definidos.
- Tiene una prioridad establecida.
- Se puede comprobar mediante pruebas.
- Está relacionada con una necesidad del producto.

## Trazabilidad

Las historias de usuario se relacionarán posteriormente con:

**Historia de usuario → código → reglas de negocio → casos de prueba → resultados → defectos/correcciones.**