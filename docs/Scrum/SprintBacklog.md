# Sprint Backlog – Mi Formación CTMA

## Sprint

Sprint de implementación y validación de las funcionalidades principales.

## Objetivo

Implementar, integrar y validar las funcionalidades necesarias para consultar y gestionar la información de las actividades formativas.

## Historias y tareas

### HU-01 – Consultar actividades formativas

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Crear la estructura de actividades formativas.
- [x] Mostrar las actividades en la interfaz.
- [x] Mostrar información básica de cada actividad.
- [x] Verificar la visualización en el emulador.

---

### HU-02 – Consultar el progreso

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Definir el porcentaje de progreso.
- [x] Mostrar el progreso de las actividades.
- [x] Validar los valores permitidos.
- [x] Crear pruebas para los valores de progreso.

---

### HU-03 – Identificar el estado

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Implementar las reglas para determinar el estado.
- [x] Considerar actividades pendientes.
- [x] Considerar actividades en proceso.
- [x] Considerar actividades completadas.
- [x] Considerar actividades vencidas.
- [x] Probar las reglas de estado.

---

### HU-04 – Identificar actividades urgentes

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Implementar la regla de urgencia.
- [x] Considerar los días restantes.
- [x] Excluir actividades completadas de la condición de urgencia.
- [x] Validar la regla mediante pruebas.

---

### HU-05 – Consultar promedio de progreso

**Prioridad:** Media  
**Estado:** Completada

**Tareas:**
- [x] Implementar el cálculo del promedio.
- [x] Considerar una lista sin actividades.
- [x] Verificar el resultado del cálculo.
- [x] Crear pruebas unitarias.

---

### HU-06 – Buscar actividades por título

**Prioridad:** Media  
**Estado:** Completada

**Tareas:**
- [x] Implementar búsqueda por título.
- [x] Ignorar diferencias entre mayúsculas y minúsculas.
- [x] Eliminar espacios externos de la búsqueda.
- [x] Validar búsquedas vacías.

---

### HU-07 – Priorizar actividades

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Implementar el ordenamiento por prioridad.
- [x] Considerar actividades vencidas.
- [x] Considerar los días restantes.
- [x] Validar el orden resultante.

---

### HU-08 – Consultar resumen de avance

**Prioridad:** Media  
**Estado:** Completada

**Tareas:**
- [x] Calcular promedio de progreso.
- [x] Contabilizar actividades completadas.
- [x] Contabilizar actividades urgentes.
- [x] Mostrar mensaje cuando no existen datos.

---

### HU-09 – Validar datos de una actividad

**Prioridad:** Alta  
**Estado:** Completada

**Tareas:**
- [x] Validar que el título no esté vacío.
- [x] Validar que el progreso esté entre 0 y 100.
- [x] Informar los errores encontrados.
- [x] Crear pruebas para las validaciones.

---

## Resumen del Sprint

| Historia | Prioridad | Estado |
|---|---|---|
| HU-01 | Alta | Completada |
| HU-02 | Alta | Completada |
| HU-03 | Alta | Completada |
| HU-04 | Alta | Completada |
| HU-05 | Media | Completada |
| HU-06 | Media | Completada |
| HU-07 | Alta | Completada |
| HU-08 | Media | Completada |
| HU-09 | Alta | Completada |

## Definition of Done

Una tarea se considera terminada cuando:

- La funcionalidad está implementada.
- Las reglas de negocio funcionan correctamente.
- La funcionalidad está integrada en la aplicación.
- Las pruebas correspondientes se ejecutan correctamente.
- El proyecto compila sin errores.
- La funcionalidad ha sido comprobada en la aplicación cuando corresponde.
- El cambio queda registrado mediante Git.