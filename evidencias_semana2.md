# Evidencias — Semana 2

## 1. Activación y repaso

- [x] Proyecto de Semana 1/2 abierto.
- [ ] Mapa de conexión Semana 1-2.
- [x] Identificación de archivos y responsabilidades.

### Archivos principales identificados

- `ActividadFormativa.kt`: modelo de una actividad formativa.
- `EstadoActividad.kt`: estados posibles de una actividad.
- `Prioridad.kt`: niveles de prioridad.
- `ReglasActividad.kt`: reglas de negocio.
- `ReglasActividadTest.kt`: pruebas unitarias.
- `MainActivity.kt`: integración con la interfaz.
- `README.md`: documentación del incremento.
- `evidencias_semana2.md`: registro de evidencias.

---

## 2. Kata de fundamentos

- [x] Tipos de Kotlin.
- [x] `val` y `var`.
- [x] Operadores.
- [x] `when`.
- [x] Funciones.
- [x] Colecciones.

Los fundamentos se aplican en las diferentes funciones y modelos desarrollados durante la Semana 2.

---

## 3. Null safety

- [x] `String?`.
- [x] `?.`.
- [x] `?:`.
- [x] `let`.
- [x] Explicación de por qué no se utiliza `!!`.

El atributo `descripcion` de `ActividadFormativa` se maneja como un valor opcional:

```kotlin
val descripcion: String?

También se utilizan operadores de null safety para trabajar de forma segura con valores que pueden ser nulos.

Se evita el uso innecesario de !! porque puede producir una excepción si el valor es null.

4. Modelado
 ActividadFormativa.
 EstadoActividad.
 Prioridad.
ActividadFormativa
data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad
)
EstadoActividad
enum class EstadoActividad {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADA,
    VENCIDA
}
Prioridad
enum class Prioridad {
    BAJA,
    MEDIA,
    ALTA
}
5. Reglas
 validarActividad.
 estadoActividad.
 actividadesUrgentes.
 promedioProgreso.
 buscarPorTitulo.
Reglas implementadas

validarActividad:

Comprueba que el título sea obligatorio.
Comprueba que el progreso esté entre 0 y 100.

estadoActividad:

Determina si una actividad está pendiente.
Determina si está en proceso.
Determina si está completada.
Determina si está vencida.

actividadesUrgentes:

Obtiene actividades no completadas con dos días o menos restantes.

promedioProgreso:

Calcula el promedio de progreso.
Devuelve 0.0 cuando la lista está vacía.

buscarPorTitulo:

Busca actividades por título.
Ignora mayúsculas y minúsculas.
Ignora espacios externos en la consulta.
6. Escenarios
 Título vacío.
 Progreso 120.
 Vencida.
 Completada con días negativos.
 Lista vacía.
 Búsqueda flexible.
Resultados comprobados
Título vacío

Se comprueba que un título compuesto únicamente por espacios genere:

El título es obligatorio
Progreso 120

Se comprueba que un progreso superior a 100 genere:

El progreso debe estar entre 0 y 100
Actividad vencida

Una actividad con días restantes negativos se identifica como:

VENCIDA
Actividad completada con días negativos

Una actividad con progreso de 100 % se identifica como:

COMPLETADA

aunque tenga días restantes negativos.

Lista vacía

El promedio de una lista vacía devuelve:

0.0
Búsqueda flexible

La búsqueda ignora mayúsculas, minúsculas y espacios externos.

7. Integración mínima
 Lista de actividades de ejemplo.
 Resumen calculado por funciones.
 UI sin duplicar las reglas.

La interfaz utiliza las funciones de ReglasActividad para obtener los resultados.

La lógica de negocio permanece separada de la interfaz.

Flujo:

Actividades
     ↓
ReglasActividad
     ↓
Resultado calculado
     ↓
MainActivity
     ↓
Interfaz
8. Reto adicional
 Vencidas primero.
 Prioridad alta.
 Menor número de días.
 Explicación del comparador.

Se implementó:

fun ordenarActividades(
    actividades: List<ActividadFormativa>
): List<ActividadFormativa>

El orden establecido es:

Actividades vencidas primero.
Actividades con prioridad ALTA.
Menor número de días restantes.

La función utiliza sortedWith, compareBy, thenByDescending y thenBy.

Ejemplo comprobado
ID 3 → Vencida
ID 4 → Prioridad ALTA, 1 día
ID 2 → Prioridad ALTA, 2 días
ID 1 → Prioridad BAJA, 5 días

Resultado:

3 → 4 → 2 → 1
9. Pruebas unitarias

Se implementaron pruebas en:

app/src/test/java/com/miguelloaiza/miformacionctma/ReglasActividadTest.kt

Actualmente se tienen 7 pruebas.

Resultado
7 tests passed
7 tests total
BUILD SUCCESSFUL

Pruebas realizadas:

Título vacío.
Progreso mayor que 100.
Actividad vencida.
Actividad completada aunque tenga días negativos.
Lista vacía.
Búsqueda ignorando mayúsculas y espacios.
Ordenamiento de actividades.
10. Evidencias visuales
 Captura de estructura del proyecto.
 Captura de ActividadFormativa.kt.
 Captura de EstadoActividad.kt.
 Captura de Prioridad.kt.
 Captura de ReglasActividad.kt.
 Captura de ReglasActividadTest.kt.
 Captura de ejecución de la aplicación.
 Captura de pruebas unitarias mostrando 7/7.
 README actualizado.
 Commit de las pruebas.
 Commit de la documentación.
11. Git
Rama utilizada
feat/Felipe-aprendiz

El desarrollo se realizó en una rama independiente para permitir posteriormente la integración con el trabajo de los demás integrantes del equipo.

Commits

Se realizaron commits para guardar los avances realizados durante el desarrollo.

Los cambios fueron enviados al repositorio remoto mediante:

git push
12. Estado de la Semana 2
Desarrollo
 Modelado.
 Reglas de negocio.
 Ejemplos A, B y C.
 Null safety.
 Reto de ordenamiento.
 Pruebas unitarias.
 7/7 pruebas exitosas.
 README actualizado.
 Trabajo mediante rama Git.