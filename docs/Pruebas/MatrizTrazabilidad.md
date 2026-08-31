# Matriz de Trazabilidad – Mi Formación CTMA

## Objetivo

Relacionar las historias de usuario del Product Backlog con sus criterios de aceptación y los casos de prueba correspondientes.

La trazabilidad permite verificar que cada funcionalidad tenga una forma definida de validación.

## Matriz

| Historia de usuario | Criterio de aceptación | Caso de prueba | Resultado |
|---|---|---|---|
| HU-01 | Se deben mostrar las actividades formativas disponibles. | CP-11 | PASS |
| HU-01 | Las actividades deben mostrar información relevante. | CP-11 | PASS |
| HU-01 | Se deben poder filtrar las actividades según su estado. | CP-12 | PASS |
| HU-02 | El progreso debe estar entre 0 y 100. | CP-02 | PASS |
| HU-02 | Un progreso de 100 representa una actividad completada. | CP-04 | PASS |
| HU-03 | Una actividad con progreso 0 debe considerarse pendiente. | CP-03 / CP-04 | PASS |
| HU-03 | Una actividad con progreso entre 1 y 99 debe considerarse en proceso. | CP-03 | PASS |
| HU-03 | Una actividad con progreso 100 debe considerarse completada. | CP-04 | PASS |
| HU-03 | Una actividad vencida debe identificarse correctamente. | CP-03 | PASS |
| HU-04 | Una actividad no completada con 2 días o menos debe considerarse urgente. | CP-08 | PASS |
| HU-04 | Una actividad completada no debe considerarse urgente. | CP-08 | PASS |
| HU-05 | El promedio debe calcularse utilizando los porcentajes de progreso. | CP-05 | PASS |
| HU-05 | Una lista sin actividades debe producir un promedio de 0.0. | CP-05 | PASS |
| HU-06 | La búsqueda debe utilizar el título de la actividad. | CP-06 | PASS |
| HU-06 | La búsqueda debe ignorar mayúsculas y minúsculas. | CP-06 | PASS |
| HU-06 | Los espacios externos de la consulta deben ignorarse. | CP-06 | PASS |
| HU-07 | Las actividades deben poder ordenarse según prioridad y situación. | CP-07 | PASS |
| HU-07 | Las actividades vencidas deben tener prioridad en el ordenamiento. | CP-07 | PASS |
| HU-08 | El resumen debe mostrar el promedio de progreso. | CP-09 | PASS |
| HU-08 | El resumen debe mostrar la cantidad de actividades completadas. | CP-09 | PASS |
| HU-08 | El resumen debe mostrar la cantidad de actividades urgentes. | CP-09 | PASS |
| HU-09 | El título no puede estar vacío. | CP-01 | PASS |
| HU-09 | El progreso debe estar entre 0 y 100. | CP-02 | PASS |
| HU-09 | Se deben informar los errores de validación. | CP-01 / CP-02 | PASS |
| HU-10 | Si el nombre completo no es válido, se debe utilizar el alias. | CP-10 | PASS |

## Trazabilidad de pruebas automatizadas

Las pruebas unitarias se relacionan principalmente con las reglas de negocio de la aplicación.

Archivo:

`app/src/test/java/com/miguelloaiza/miformacionctma/ReglasActividadTest.kt`

Entre las reglas verificadas se encuentran:

- Validación de título.
- Validación del progreso.
- Determinación del estado.
- Identificación de actividades vencidas.
- Identificación de actividades completadas.
- Cálculo del promedio.
- Búsqueda de actividades.
- Ordenamiento de actividades.

## Trazabilidad funcional

Las pruebas funcionales permiten complementar las pruebas unitarias comprobando el comportamiento de la aplicación en el emulador.

Se relacionan principalmente con:

- Visualización de actividades.
- Información mostrada al usuario.
- Filtrado de actividades.
- Funcionamiento general de la interfaz.

## Flujo de trazabilidad

La relación establecida para el producto es:

Historia de usuario
→ Criterio de aceptación
→ Caso de prueba
→ Resultado
→ Defecto, si aplica
→ Corrección
→ Prueba de regresión

## Estado general

Las historias incluidas en el Sprint cuentan con criterios de aceptación y casos de prueba asociados.

Los resultados documentados corresponden a las pruebas realizadas sobre las funcionalidades implementadas.

## Evidencias

Las evidencias asociadas pueden incluir:

- Ejecución de `ReglasActividadTest`.
- Ejecución de la aplicación en el emulador.
- Capturas de las funcionalidades.
- Registro de defectos encontrados.
- Evidencias de correcciones.
- Ejecución de pruebas de regresión.