# Mi Formación CTMA - Semana 2

## Kotlin para resolver problemas móviles

Proyecto desarrollado para la Semana 2 del caso integrador **Mi Formación CTMA**.

El objetivo de esta semana es aplicar fundamentos de Kotlin para modelar actividades formativas, implementar reglas de negocio y comprobar su funcionamiento mediante pruebas unitarias.

---

## Objetivos

Durante esta semana se trabajaron los siguientes conceptos:

- Tipos de datos y operadores.
- `val` y `var`.
- Expresiones condicionales.
- `when`.
- Funciones.
- Colecciones.
- Operaciones como `filter`, `count`, `map`, `average` y `sortedWith`.
- Null safety con `?.`, `?:` y `let`.
- `data class`.
- `enum class`.
- `sealed class`.
- Interfaces.
- Reglas de negocio independientes de la interfaz.
- Pruebas unitarias.
- Trabajo colaborativo mediante Git y ramas.

---

# Estructura del proyecto

La estructura principal del proyecto es:

```text
app/
└── src/
    ├── main/
    │   └── java/
    │       └── com.miguelloaiza.miformacionctma/
    │           ├── domain/
    │           │   ├── ActividadFormativa.kt
    │           │   ├── EstadoActividad.kt
    │           │   ├── Prioridad.kt
    │           │   └── ResultadoRegistro.kt
    │           │
    │           ├── keepRules/
    │           │   └── KeepRules.kt
    │           │
    │           ├── rules/
    │           │   ├── ReglasActividad.kt
    │           │   └── ValidadorActividad.kt
    │           │
    │           ├── examples/
    │           │   └── EjemplosKotlin.kt
    │           │
    │           └── MainActivity.kt
    │
    └── test/
        └── java/
            └── com.miguelloaiza.miformacionctma/
                └── ReglasActividadTest.kt
Modelo de dominio
ActividadFormativa

Las actividades formativas se representan mediante una data class.

data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad
)

La clase contiene:

id: identificador de la actividad.
titulo: título de la actividad.
descripcion: descripción opcional.
progreso: porcentaje de avance.
diasRestantes: cantidad de días restantes.
prioridad: prioridad asignada a la actividad.

Se utiliza val para representar datos que no necesitan ser reasignados.

Prioridad

Las actividades pueden tener tres niveles de prioridad:

enum class Prioridad {
    BAJA,
    MEDIA,
    ALTA
}

Los niveles permiten clasificar las actividades según su importancia.

Estado de una actividad

Los estados disponibles son:

enum class EstadoActividad {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADA,
    VENCIDA
}

La función estadoActividad() determina el estado de una actividad utilizando sus datos de progreso y días restantes.

La evaluación se realiza de la siguiente manera:

Si el progreso es 100, la actividad está COMPLETADA.
Si los días restantes son menores que 0, la actividad está VENCIDA.
Si el progreso es 0, la actividad está PENDIENTE.
En los demás casos, la actividad está EN_PROCESO.

Esto permite que una actividad completada siga siendo considerada completada aunque su fecha ya haya pasado.

Reglas de negocio

Las reglas principales se encuentran en:

rules/ReglasActividad.kt

Las reglas permanecen separadas de la interfaz para poder probarlas de manera independiente.

Validación de actividades

La función:

validarActividad()

comprueba que:

El título sea obligatorio.
El progreso esté entre 0 y 100.

Ejemplo:

val errores = ReglasActividad.validarActividad(
    titulo = " ",
    progreso = 120
)

Los errores esperados son:

El título es obligatorio
El progreso debe estar entre 0 y 100

La función devuelve todos los errores encontrados.

Actividades urgentes

La función:

actividadesUrgentes()

obtiene las actividades que:

No están completadas.
Tienen dos días o menos restantes.

Esto permite identificar compromisos que requieren atención.

Promedio de progreso

La función:

promedioProgreso()

calcula el promedio del progreso de las actividades.

Cuando la lista está vacía:

0.0

Esto permite manejar correctamente el caso en el que todavía no existen actividades.

Búsqueda por título

La función:

buscarPorTitulo()

permite buscar actividades por su título.

La búsqueda:

Ignora mayúsculas y minúsculas.
Elimina espacios externos de la consulta.
Devuelve una lista con las actividades encontradas.

Ejemplo:

" kotlin "

puede encontrar:

"Kotlin básico"
Ejemplos de Kotlin
Ejemplo A - Priorización

Se implementó el concepto de priorización de compromisos mediante una data class:

data class Compromiso(
    val titulo: String,
    val diasRestantes: Int,
    val completado: Boolean
)

La función prioridad() utiliza when para clasificar los compromisos.

Los resultados posibles son:

Finalizado
Vencido
Urgente
Planificado
Ejemplo B - Resumen de progreso

Se implementó la función:

resumenProgresos()

Esta función permite obtener un resumen de una lista de porcentajes.

Calcula:

Promedio de progreso.
Cantidad de elementos completados.

También controla listas vacías y datos fuera del rango válido.

Ejemplo C - Null safety

Se implementó la función:

nombreVisible()

Esta función demuestra el uso de null safety en Kotlin.

Utiliza:

?.

para acceder de forma segura a un valor opcional.

También utiliza:

?:

para proporcionar un valor alternativo cuando el dato es null o está vacío.

Reto adicional: ordenamiento de actividades

Se implementó la función:

ordenarActividades()

El criterio de ordenamiento es:

Actividades vencidas primero.
Actividades con prioridad ALTA.
Menor número de días restantes.

La implementación utiliza:

sortedWith()

junto con:

compareBy()

y:

thenByDescending()
Ejemplo

Si tenemos:

ID 3 → Vencida
ID 4 → Prioridad ALTA, 1 día
ID 2 → Prioridad ALTA, 2 días
ID 1 → Prioridad BAJA, 5 días

el resultado esperado es:

3 → 4 → 2 → 1

La lógica de ordenamiento permanece dentro de ReglasActividad.kt y no dentro de la interfaz.

Pruebas unitarias

Las pruebas se encuentran en:

app/src/test/java/com/miguelloaiza/miformacionctma/ReglasActividadTest.kt

Se implementaron siete pruebas unitarias.

Pruebas realizadas
1. Título vacío

Comprueba que un título vacío genere:

El título es obligatorio
2. Progreso mayor que 100

Comprueba que un progreso de 120 genere:

El progreso debe estar entre 0 y 100
3. Actividad vencida

Comprueba que una actividad con días restantes negativos sea:

VENCIDA
4. Actividad completada

Comprueba que una actividad con progreso 100 sea:

COMPLETADA

incluso cuando los días restantes sean negativos.

5. Lista vacía

Comprueba que el promedio de una lista vacía sea:

0.0
6. Búsqueda

Comprueba que la búsqueda ignore:

Mayúsculas.
Minúsculas.
Espacios externos.
7. Ordenamiento

Comprueba que las actividades se ordenen correctamente según:

Vencimiento.
Prioridad.
Días restantes.
Resultado de las pruebas

Las pruebas fueron ejecutadas desde Android Studio mediante:

ReglasActividadTest

Resultado obtenido:

7 tests passed
7 tests total
BUILD SUCCESSFUL

Por lo tanto, las siete pruebas unitarias implementadas actualmente pasan correctamente.

Integración con Compose

La aplicación cuenta con una integración mínima con la interfaz.

MainActivity.kt utiliza las reglas de negocio para calcular el resumen de las actividades y posteriormente muestra el resultado en la interfaz.

El flujo utilizado es:

Actividades
     ↓
ReglasActividad
     ↓
Resumen calculado
     ↓
MainActivity
     ↓
Interfaz Compose

La interfaz no duplica las reglas de negocio.

Esto permite mantener separadas:

La lógica de dominio.
Las reglas de negocio.
La presentación.
Decisiones técnicas
Uso de val

Se utiliza val cuando una referencia no necesita ser reasignada.

Esto reduce la mutabilidad innecesaria.

Null safety

Los valores que pueden estar ausentes se representan utilizando tipos anulables:

String?

Se utilizan los operadores:

?.

y:

?:

para trabajar de manera segura con valores opcionales.

Se evita el uso innecesario de:

!!
Colecciones

Se utilizan operaciones expresivas de Kotlin como:

filter()
count()
map()
average()
sortedWith()

Estas funciones permiten trabajar con las colecciones de manera clara y concisa.

Separación de responsabilidades

Las reglas de negocio se mantienen fuera de MainActivity.kt.

De esta forma:

Las reglas pueden probarse independientemente.
La interfaz solamente presenta los resultados.
El código es más fácil de mantener.
Se evita duplicar lógica.
Trabajo colaborativo con Git

El desarrollo se realiza mediante ramas para evitar trabajar directamente sobre main.

La rama utilizada para este desarrollo es:

feat/Felipe-aprendiz

Los cambios se prueban antes de realizar los commits.

Posteriormente se utiliza:

git push

para enviar la rama al repositorio remoto.

La integración con las ramas de los demás integrantes se realizará posteriormente mediante el proceso de integración definido por el equipo.

Evidencias

El proyecto contiene el archivo:

evidencias_semana2.md

Este archivo se utiliza para registrar las evidencias relacionadas con el desarrollo de la guía.

Las evidencias principales incluyen:

Ejecución de la aplicación.
Ejecución de las pruebas unitarias.
Resultado de las pruebas.
Cambios realizados.
Trabajo mediante ramas de Git.
Estado actual del proyecto
Semana 2 - Kotlin
 Modelado de ActividadFormativa.
 Enum Prioridad.
 Enum EstadoActividad.
 Reglas de validación.
 Determinación del estado de actividades.
 Actividades urgentes.
 Promedio de progreso.
 Búsqueda por título.
 Ejemplo A: priorización.
 Ejemplo B: resumen de progreso.
 Ejemplo C: null safety.
 Reto de ordenamiento.
 Pruebas unitarias.
 7/7 pruebas exitosas.
 Integración mínima con Compose.
 Documentación.
 Trabajo en rama feat/Felipe-aprendiz.
Conclusión

En esta semana se desarrolló el núcleo de lógica de negocio de Mi Formación CTMA utilizando Kotlin.

Se aplicaron conceptos fundamentales del lenguaje, modelado mediante data class y enum class, funciones, colecciones, when, null safety y pruebas unitarias.

Las reglas de negocio se mantienen independientes de la interfaz y actualmente las siete pruebas unitarias implementadas se ejecutan correctamente.

El proyecto queda preparado para continuar con los siguientes incrementos del caso integrador.