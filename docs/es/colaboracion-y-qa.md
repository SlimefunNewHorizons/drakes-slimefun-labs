# Colaboración, roles y campo de pruebas (acuerdo de equipo)

Resumen de lo acordado entre **Pablo** y **Chagui** (abril 2026) para que quede en el repo y en GitHub, alineado con la [documentación central](../README.md) y la [wiki del proyecto](https://github.com/SlimefunNewHorizons/drakes-slimefun-labs/wiki) cuando exista.

## Contexto

- El porte del Slimefun y del monorepo a **Paper 1.21.x** con **Java 21** es trabajo denso; no todo el mundo tiene que entender Maven, Gradle ni el sombreado de dependencias para aportar.
- La base (compilación en CI, smoke, JARs) puede estar **lista** mientras aún falta **funcionalidad real** probada en juego: recetas, GUIs, interacciones con otros plugins, errores concretos al usar cada addon.

## División de trabajo (sin presión)

| Quién | Enfoque |
|-------|---------|
| **Pablo** | Porte, builds, fixes técnicos, rama **26.x** cuando toque, integración de plugins en el servidor de pruebas. |
| **Chagui** | **QA / beta tester**: jugar en el servidor de pruebas, reportar fallos reproducibles (qué hiciste, qué esperabas, qué pasó). Opcional a futuro: nuevas mecánicas o máquinas cuando el flujo de build esté claro para él. |

Reglas explícitas: **cero presión, cero drama** en los reportes. Si algo explota (por ejemplo una GUI de Supreme con un material concreto), basta con el dato y el log o pasos; no hace falta disculparse por “no saber código”.

## Qué significa “campo de pruebas”

El servidor con el pack cargado sirve para:

- Comprobar que los plugins **arrancan** y responden en situaciones reales.
- Encontrar **errores específicos** al usar addons (no solo “no carga”).
- Acumular issues accionables para el monorepo o para el core Drake.

Eso es trabajo de **QA básico** / beta: valioso y independiente de saber varios lenguajes.

## Compilación: no uses comandos viejos

Los comandos “de un solo `mvn` para todo” que se compartieron hace muchos commits **ya no reflejan** el monorepo actual (reactor Maven + módulos Gradle, orden de módulos, sombreado, etc.).

- **Fuente de verdad:** [development-setup.md](development-setup.md) (y la versión [en inglés](../en/development-setup.md)), más el [README raíz](../../README.md) y la **wiki** del repositorio en GitHub.
- Si algo no compila en tu máquina, prioriza **abrir un issue** con el error completo y la rama (`1.21-latin` u otra), en lugar de insistir con un comando obsoleto.

## Para Chagui (y colaboradores no dev)

1. Lee el índice [docs/README.md](../README.md) y la wiki del repo.
2. Para probar en servidor: usa el entorno que indique el equipo (campo de pruebas / DrakesCraft según corresponda).
3. Al reportar: **pasos**, **versión aproximada del pack o commit**, y **fragmento de log** o captura si hay crash.

## Roadmap mencionado

- **Corto plazo:** seguir metiendo plugins y pulir carga + errores en el servidor de pruebas.
- **~Un mes:** trabajo de porte hacia línea **Paper 26.x** (rama **`26.X-ToTheStars`**); no mezclar merges entre ramas divergentes (ver README y política de PRs).

## Contenido y comunidad (idea aparte)

Formar al staff en **creación de contenido** pequeña (tutoriales, Slimefun en práctica) y posible monetización en YouTube es independiente del monorepo; si se concreta, puede vivir en wiki o en docs de comunidad sin bloquear el laboratorio técnico.

---

*Este documento fija expectativas de equipo; la política técnica de ramas y CI sigue en el README raíz y en [github-maintenance.md](../github-maintenance.md).*
