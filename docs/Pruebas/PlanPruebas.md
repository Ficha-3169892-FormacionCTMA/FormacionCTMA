# Plan de Pruebas – Mi Formación CTMA

## 1. Objetivo

Definir las pruebas necesarias para verificar que las funcionalidades principales de Mi Formación CTMA funcionen de acuerdo con las reglas de negocio y los criterios de aceptación definidos en el Product Backlog.

## 2. Alcance

Se probarán las funcionalidades relacionadas con:

- Consulta de actividades formativas.
- Consulta del progreso.
- Determinación del estado de las actividades.
- Identificación de actividades urgentes.
- Cálculo del promedio de progreso.
- Búsqueda de actividades.
- Priorización y ordenamiento.
- Consulta del resumen de avance.
- Validación de datos.

## 3. Tipos de pruebas

### Pruebas unitarias

Permiten verificar las reglas de negocio de manera independiente.

Se probarán principalmente:

- Determinación del estado.
- Determinación de urgencia.
- Cálculo del promedio.
- Búsqueda.
- Ordenamiento.
- Validación de datos.
- Obtención del nombre del usuario.

### Pruebas instrumentadas

Permiten comprobar el comportamiento de la aplicación en el entorno Android.

Se utilizarán para validar las funcionalidades que correspondan a la interfaz y su ejecución en el dispositivo o emulador.

### Pruebas de regresión

Después de realizar cambios o correcciones se volverán a ejecutar las pruebas relacionadas para comprobar que las funcionalidades que anteriormente funcionaban continúen funcionando correctamente.

## 4. Criterios de entrada

Las pruebas pueden comenzar cuando:

- El código de la funcionalidad está implementado.
- Las reglas de negocio están disponibles.
- La aplicación puede compilarse.
- Existen criterios de aceptación definidos.
- Se cuenta con los casos de prueba correspondientes.

## 5. Criterios de salida

Las pruebas se consideran satisfactorias cuando:

- Los casos de prueba ejecutados obtienen el resultado esperado.
- No existen defectos críticos pendientes.
- Las reglas de negocio funcionan correctamente.
- La aplicación compila correctamente.
- La aplicación puede ejecutarse en el emulador.
- Las correcciones realizadas pasan nuevamente las pruebas correspondientes.

## 6. Evidencias

Las evidencias de las pruebas estarán constituidas por:

- Resultados de pruebas unitarias.
- Resultados de pruebas instrumentadas.
- Capturas de la aplicación cuando corresponda.
- Registro de defectos.
- Resultados de las pruebas posteriores a las correcciones.
- Commits relacionados con las correcciones.

## 7. Trazabilidad

Cada caso de prueba deberá relacionarse con una historia de usuario del Product Backlog.

La relación será:

Historia de usuario → Criterio de aceptación → Caso de prueba → Resultado → Defecto, si aplica → Corrección → Regresión.