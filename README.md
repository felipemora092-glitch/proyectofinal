# Proyecto Integrador — Plataforma de Monitoreo Ambiental Urbano

## Visión general

Este repositorio contiene la solución completa del proyecto integrado que se desarrolla a lo largo de **siete semanas** del curso de Estructuras de Datos. Cada semana añade una capa de funcionalidad, manteniendo un único punto de entrada (`IngestaSensores.java`).  

| Semana | Tema | Principales clases añadidas |
|--------|------|------------------------------|
| 1 | Ingesta de datos y validación | `IngestaSensores`, `LecturaSensor` |
| 2 | Almacenamiento en arreglo | `Repositorio`, `RepositorioArreglo`, `RepositorioLecturas` |
| 3 | Búsqueda (lineal y binaria) | `BuscadorLecturas`, `AnalizadorMatriz` |
| 4 | Algoritmos de ordenamiento | `Ordenador`, `BancoDeOrdenamiento` |
| 5 | Listas enlazadas y doblemente enlazadas | `Nodo`, `RepositorioEnlazado`, `HistorialNavegable`, `BancoDeListas` |
| 6 | Pilas, Colas y Montículos | `PilaDeAcciones`, `ColaDeIngesta`, `ColaDeAlertas`, `Alerta`, `BancoDeFlujo` |
| 7 | Java Collections Framework (JCF) | `RepositorioColeccion`, `BancoDeColecciones`, `GestorDeRed` |

## Arquitectura y flujo

1. **Ingesta** (`IngestaSensores.java`):
   - Lee el archivo CSV `data/lecturas_ampliadas.csv`.
   - Valida cada línea (formato y rangos físicos).
   - Inserta lecturas en el **Repositorio** configurado (por defecto `RepositorioArreglo`).
   - Actualiza el **AnalizadorMatriz** para estadísticas por hora.
2. **Almacenamiento** (`RepositorioArreglo` / `RepositorioEnlazado` / `RepositorioColeccion`):
   - Implementan la interfaz `Repositorio` que expone `agregarAlFinal`, `agregarAlInicio`, `obtener`, `eliminar`, `tamano` y `nombre`.
   - `RepositorioArreglo` mantiene un arreglo dinámico con contadores de movimientos.
   - `RepositorioEnlazado` es una lista enlazada simple con optimizaciones (uso del puntero `ultimo`).
   - `RepositorioColeccion` delega la funcionalidad al JCF (`ArrayList` o `LinkedList`).
3. **Búsqueda** (`BuscadorLecturas`):
   - Búsqueda lineal y binaria por *timestamp*.
   - Búsqueda binaria por *PM2.5* (requiere datos ordenados por ese criterio).
4. **Ordenamiento** (`Ordenador`):
   - Implementaciones de Bubble‑Sort (con corte temprano), Selection‑Sort, Insertion‑Sort, Merge‑Sort, Quick‑Sort (pivote aleatorio) y Heap‑Sort.
   - Contadores de comparaciones e intercambios para medir coste.
   - `quickSortPivoteAleatorio` es el método recomendado; `quickSortPivotePrimero` se conserva como alias de compatibilidad.
5. **Listas enlazadas** (`RepositorioEnlazado`, `HistorialNavegable`):
   - Inserción/ eliminación en O(1) o O(n) según caso.
   - `HistorialNavegable` permite navegación hacia adelante y atrás con un cursor, usado por la UI de control.
6. **Control de Flujo (Semana 6)**:
   - **Pila de Acciones**: Gestiona el historial de operaciones del operador para permitir el "Deshacer" (LIFO).
   - **Cola de Ingesta**: Buffer circular que recibe lecturas de la red para procesarlas secuencialmente sin perder datos (FIFO).
   - **Cola de Alertas**: Gestión de prioridad mediante un **Montículo (Heap)**, asegurando que las alertas críticas se atiendan primero, con desempate por orden de llegada.
7. **Colecciones Estándar (Semana 7)**:
   - Implementación de repositorios usando `List` del JCF.
   - Análisis de rendimiento entre `ArrayList` y `LinkedList`.
   - Uso de `Comparator` para ordenamientos personalizados y estables.
   - `GestorDeRed` integra `List`, `Deque`, `Queue` y `Map` para resolver los diez retos de la guía.

## Cómo compilar y ejecutar

El proyecto utiliza Maven y Java 17. Desde la raíz del repositorio:

```bash
# Ejecutar todas las pruebas unitarias
mvn test

# Empaquetar el proyecto
mvn package

# Ejecutar la aplicación principal
java -cp target/classes ingest.IngestaSensores
```

> **Requisitos**: tener instalado JDK 17 o superior y Maven 3.8 o superior. El archivo `data/lecturas_ampliadas.csv` debe estar presente.

## Ejecutar experimentos por semana

- **Semana 3**: los experimentos de búsqueda se ejecutan desde `ingest.IngestaSensores`; `BancoDePruebas` no tiene un método `main` independiente.
- **Semana 4**: `java -cp target/classes experiment.BancoDeOrdenamiento` (prueba los algoritmos de ordenamiento).
- **Semana 5**: `java -cp target/classes experiment.BancoDeListas` (compara arreglo vs lista enlazada y prueba la lista doble).
- **Semana 6**: `java -cp target/classes experiment.BancoDeFlujo` (prueba pila, cola circular y cola de prioridad).
- **Semana 7**: `java -cp target/classes experiment.BancoDeColecciones` (compara implementaciones del JCF).

## Tests

El proyecto incluye un conjunto completo de pruebas unitarias bajo `test/` que cubren:
- Validación y acceso de `LecturaSensor`.
- Todas las implementaciones de `Repositorio`.
- Algoritmos de ordenamiento y sus contadores.
- Búsqueda lineal y binaria.
- Navegación en la lista doble (`HistorialNavegable`).
- Operaciones de Pila, Cola Circular y Montículo.
- Delegación a colecciones estándar del JCF.

Ejecutar todas las pruebas con JUnit:
```bash
mvn test
```

## Decisiones de diseño (ver `docs/decisiones.md`)

- Uso de la interfaz `Repositorio` para aislar la lógica de la aplicación del mecanismo de almacenamiento.
- Uso de `ArrayDeque` para `Deque` y `Queue`, y `HashMap` para la lista de adyacencia del grafo en `GestorDeRed`.
- Uso de `ArrayList` para el historial de eventos y las listas de vecinos.
- Implementación de **pivote aleatorio** en Quick‑Sort para evitar `StackOverflowError` con datos ya ordenados.
- Añadido de un puntero `ultimo` en la lista enlazada para `agregarAlFinal` O(1).
- Introducción de la lista doble (`HistorialNavegable`) para navegación bidireccional en la UI.
- Uso de **aritmética de índices** para el Montículo, evitando el uso de nodos y referencias para optimizar el rendimiento.
- Criterio de desempate en la Cola de Prioridad basado en el **orden de llegada** para garantizar equidad en alertas de misma severidad.
- Elección de `ArrayList` para el `RepositorioColeccion` basándose en la frecuencia de inserciones al final y recorridos completos.

---

**Proyecto académico** — Módulo Integrador de Estructuras de Datos.
