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
