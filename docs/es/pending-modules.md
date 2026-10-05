# Modulos pendientes

Este documento refleja el estado **técnico** del monorepo tras la estabilización de **CI** y builds locales. No enumera “cada bug posible en survival”: eso lo absorben **Issues**, **Chagui**, la **comunidad** y el servidor **[DrakesCraft](https://drakescraft.cl)** (Chile) con el tiempo.

## Qué no cuenta como “pendiente del laboratorio”

- **Pulido addon por addon** en juego (textos, recetas raras, interacciones con economía/PvE de un servidor concreto).
- **Paridad 1:1 con cada upstream** salvo que bloquee compilación o arranque en el smoke acordado.
- **Paper 26.x**: va en la rama **`26.X-ToTheStars`**, no aquí.

## Inventario unico (fuente de verdad)

- Tabla **por modulo** con estado (Listo CI / Listo local / En curso / Bloqueado) y observaciones: [`PLUGIN_MATRIX.md`](PLUGIN_MATRIX.md) (generada; ejecutar `python scripts/generate_plugin_matrix.py`).
- Misma tabla incrustada en el [`README.md`](../../README.md) raiz.
- Tablero de organizacion: [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) — ver [`PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md) para alinear tarjetas con la matriz.

## Estado actual

- **CI Monorepo 1.21** (`ci-monorepo-121.yml`): cubre el reactor Maven completo (`maven_full_reactor`) y los 5 proyectos Gradle (`gradle_green`).
- **Compilacion local completa**: el reactor Maven completo y los 5 proyectos Gradle declarados compilan en el corte `2026-04-24`.
- **Smoke runtime**: hay perfiles (`foundation`, `monorepo-all`, variantes Paper 1.21.11, etc.); el mantenimiento del script y ejecutarlo en cada gran cambio sigue siendo buena práctica.
- **Siguiente fricción esperada**: no en “compilar”, sino en **bugs de juego** y afinado en **DrakesCraft** / reportes de jugadores.

## Auditoria tecnica reciente (build por lotes)

Fecha del corte: `2026-04-24`.

### Resumen de conteos (evidencia local en esta fecha)

| Herramienta | OK | FAIL | Notas |
|-------------|---:|-----:|-------|
| Maven (`mvn -B -DskipTests compile -fae`) | 81 modulos del reactor | 0 | `BUILD SUCCESS`; log local `drakes-maven-compile-fae.log` en `%TEMP%` durante el corte |
| Gradle (`compileJava` por proyecto declarado) | 5 proyectos Gradle | 0 | Galactifun, Bump, CustomItemGenerators, FastMachines y SlimefunTranslation compilan |

### Lote Maven completo

- Reactor Maven completo: **BUILD SUCCESS** con `mvn -B -DskipTests compile -fae`.
- Modulos que antes bloqueaban el pase (`GeneticChickengineering-Reborn`, `EMCTech`, `UltimateGenerators2`, `VillagerTrade`, `SlimeHUD`, `SfBetterChests`, entre otros) ahora compilan contra el baseline Drake.
- Persisten warnings de APIs Paper/Bukkit antiguas, pero no bloquean compilacion.

### Lote Gradle auditado

- `sources:batch-2-expansion:Galactifun`: **BUILD SUCCESSFUL**.
- `sources:community-addons:Bump`: **BUILD SUCCESSFUL**.
- `sources:community-addons:CustomItemGenerators`: **BUILD SUCCESSFUL** tras quitar `sf4k`, usar `JavaPlugin` directo y registrar via adapter `SlimefunAddon`.
- `sources:community-addons:FastMachines`: **BUILD SUCCESSFUL** tras alinear dependencias Drake, instalar `InfinityExpansion-drake` localmente y agregar bridges Kotlin locales.
- `sources:community-addons:SlimefunTranslation`: **BUILD SUCCESSFUL** tras ajustar API Paper 1.21 (`EntityType.ITEM`).

### Mitigaciones y herramientas aplicadas en este bloque

1. Timeout de wrapper Gradle aumentado para estabilizar descargas en red lenta (`networkTimeout=120000`).
2. Alineacion de coordenadas internas Gradle con el namespace canonico del monorepo (`com.github.drakescraft_labs`) para resolver artefactos locales.
3. Publicacion local de artefactos base (`dough-core`, `slimefun-core`, `SefiLib`, `InfinityLib`, `InfinityExpansion-drake`) para destrabar resolucion de dependencias sin credenciales de GitHub Packages.
4. Bridges BusyBiscuit dentro de Slimefun core (`io.github.thebusybiscuit.slimefun4.*`) para librerias compiladas contra el paquete upstream.
5. Bridges Kotlin locales para addons Gradle: `MenuBlock`, `TickingMenuBlock` y `DrakeItemBuilderCompat`.
6. Migraciones puntuales de API 1.21: enums renombrados, imports Dough/Slimefun Drake, `GuizhanBuildsUpdater.start(...)`, y reemplazo de extensiones Kotlin no disponibles por llamadas directas.

## Porteo automatizado (parches por lotes)

Herramienta: `scripts/port_paper_121.py` (reglas textuales conservadoras para Paper 1.21.1). Incluye entre otras la regla `libraries-paperlib` (`com.github.drakescraft_labs.slimefun4.libraries.paperlib` -> `io.papermc.lib`).

```bash
python scripts/port_paper_121.py --list-rules
python scripts/port_paper_121.py --dry-run --path sources/community-addons/MiAddon
python scripts/port_paper_121.py --apply --path sources/community-addons/MiAddon --rules libraries-dough,libraries-commons
```

Siempre ejecutar antes `--dry-run`, revisar diff, y luego `--apply`. Con `--backup` se escribe un `.portbak` por archivo (no versionar esos backups).

## Bloques de trabajo sugeridos

1. Mantener `maven_full_reactor` y `gradle_green` como checks obligatorios de cierre.
2. Ejecutar smoke de runtime tras cambios grandes (o confiar en CI + quien despliegue en **DrakesCraft**).
3. Reducir deuda de compatibilidad donde un bridge local ya pueda convertirse en API compartida.
4. Triaje de Issues con lo que salga en survival (comunidad + Chagui).

## Criterio de cierre

Un modulo se considera cerrado cuando:

- compila en el job correspondiente de [`ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml),
- no rompe el pipeline global,
- y tiene validacion minima de runtime si aplica.

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronización: **2026-04-24** (build Maven + Gradle); README/docs **2026-04-25** (mensaje “qué queda” alineado con comunidad + DrakesCraft).
> Baseline técnico: **Paper 1.21.x + Java 21** en rama **`main`**.
> CI: **Monorepo 1.21** (reactor completo + Gradle). Smoke y **release multi-JAR** opcionales documentados en README / `github-maintenance.md`.
> Lo que sigue: **gameplay y pulido en servidor** (no es lista cerrada del laboratorio).
<!-- DRAKES-STATUS:END -->
