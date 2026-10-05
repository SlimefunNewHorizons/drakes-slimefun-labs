# Documentación del monorepo

Fuente de verdad para **DrakesCraft Slimefun Foundry** en la rama **`main`**: **Paper 1.21.x**, **Java 21**, reactor Maven + proyectos Gradle en la raiz.

## Qué está hecho vs qué sigue

| Hecho en el repo (laboratorio) | Sigue en el mundo real |
|-------------------------------|-------------------------|
| Compilación del reactor en **CI Monorepo 1.21** | Bugs de gameplay, balance, interacciones con otros plugins |
| **Smoke** Paper 1.21.1 / 1.21.11 (perfiles en `scripts/smoke/`) | Pruebas largas en survival, reportes de jugadores |
| **Release** opcional: muchos `.jar` en un solo GitHub Release ([workflow](../.github/workflows/release-monorepo-jars.yml)) | Pasta fina **addon por addon** (Chagui, comunidad, staff) |
| Matriz y tablas generadas | **[DrakesCraft](https://drakescraft.cl)** (Chile) como servidor de referencia del pack |

La linea **Paper 26.x** se trabaja en la rama **[`26.X-ToTheStars`](https://github.com/SlimefunNewHorizons/drakes-slimefun-labs/tree/26.X-ToTheStars)**; no sustituye a `main` hasta que ese porte este listo.

## Wiki del laboratorio

- **Índice wiki (mapa + runtime updater):** [wiki/README.md](wiki/README.md).
- La **Wiki de GitHub** del repo (si existe) debe enlazar a `docs/wiki/` y a este `docs/README.md` para una sola fuente de verdad.

## Dónde empezar

| Objetivo | Documento |
|----------|-------------|
| **Wiki (mapa y updater)** | [wiki/README.md](wiki/README.md) |
| Vision general del proyecto, reactor, scripts y direccion tecnica | [README raiz](../README.md) |
| Matriz auditada por módulo (no editar a mano) | [es/PLUGIN_MATRIX.md](es/PLUGIN_MATRIX.md) |
| Qué queda a nivel build / historial técnico | [es/pending-modules.md](es/pending-modules.md) |
| Arranque local y convenciones | [en/development-setup.md](en/development-setup.md) · [es/development-setup.md](es/development-setup.md) |
| Smoke test (servidor real) | [en/smoke-test-guide.md](en/smoke-test-guide.md) · [es/smoke-test-guide.md](es/smoke-test-guide.md) |
| **DrakesLabPresence** (latido HTTPS opcional hacia tu webhook; ver en qué servidores corre el pack) | [README del addon](../sources/community-addons/DrakesLabPresence/README.md) |
| Roles (QA / beta), acuerdos de equipo y “campo de pruebas” | [es/colaboracion-y-qa.md](es/colaboracion-y-qa.md) |
| Scripts Python y PowerShell | [../scripts/README.md](../scripts/README.md) |
| Tablero GitHub Projects (org) | [PROJECT_BOARD_SYNC.md](PROJECT_BOARD_SYNC.md) |
| Actions, releases (assets JAR), PRs, alertas | [github-maintenance.md](github-maintenance.md) |
| Contexto rápido del monorepo (copiar/pegar) | [es/contexto-rapido-repo.md](es/contexto-rapido-repo.md) · [en/repo-session-brief.md](en/repo-session-brief.md) |
| Guía de trabajo en el repositorio | [es/guia-monorepo.md](es/guia-monorepo.md) · [en/monorepo-work-guide.md](en/monorepo-work-guide.md) |

## Idiomas

- **Inglés**: índice [en/home.md](en/home.md); guías bajo `docs/en/`.
- **Español**: índice [es/home.md](es/home.md); guías bajo `docs/es/`.

Si un texto discrepa del workflow en `.github/workflows/`, manda el **workflow** como referencia.

## Qué queda fuera de esta carpeta

- `sources/**/README.md`: documentación por addon o upstream; no es el manual central del laboratorio.
- Wiki de GitHub (si existe): enlazar a [wiki/README.md](wiki/README.md) y a esta carpeta `docs/`; evitar duplicar texto largo fuera del repo.

## Compilación (addons y Slimefun sombreado)

En el reactor, el `package` de **Slimefun** sombrea Dough bajo `com.github.drakescraft_labs.slimefun4.libraries.dough.protection`. Los módulos que dependen de Slimefun compilan **después** de ese paso, así que las llamadas a `Slimefun.getProtectionManager()` deben usar esos tipos relocados, no `dev.drake.dough.protection` (salvo en `slimefun-core` y `dough-core`). La verificación alineada con CI es `mvn package`. Ajuste masivo: `python scripts/fix_dough_compilation_imports.py` (ver [scripts/README.md](../scripts/README.md)).

## Estado operativo (referencia rápida)

- Rama estable **1.21.x**: `main`.
- CI principal: **CI Monorepo 1.21** (`.github/workflows/ci-monorepo-121.yml`).
- Smoke opcional: **Smoke Runtime 1.21** (`workflow_dispatch`).
- Servidor de referencia del pack: **[drakescraft.cl](https://drakescraft.cl)**.

Para fechas y cortes históricos, usa Issues o el Project de la org.
