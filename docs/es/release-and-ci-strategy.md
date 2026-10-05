# Estrategia de Releases y CI

## Objetivo

Definir una política clara para publicación de artefactos y automatización en `drakes-slimefun-labs` sin confundir el laboratorio de migración con una distribución monolítica estable.

## Estado actual

Este repositorio es un `reactor Maven` grande y curado por etapas.

El inventario por modulo (listo CI, listo local, en curso, bloqueado) esta en el [`README.md`](../../README.md) y en [`PLUGIN_MATRIX.md`](PLUGIN_MATRIX.md), generados por `scripts/generate_plugin_matrix.py`. El [Project 1 de la org](https://github.com/orgs/SlimefunNewHorizons/projects/1) debe reflejar esos mismos estados ([`PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md)).

Eso implica que:

- no todos los módulos tienen el mismo nivel de validación runtime
- algunos todavía conservan versiones como `UNOFFICIAL`, `MODIFIED` o similares
- el hecho de que un módulo compile no significa automáticamente que deba publicarse como release estable

## Decisión principal (actualizada)

Un **GitHub Release con muchos `.jar`** (uno por módulo Maven) **no** implica que todo el reactor tenga el mismo nivel de QA en juego: es un **artefacto de laboratorio** para descargas selectivas, CI verde previo y el **auto-updater** embebido (`drakes-labs-autoupdate` → `DrakesLabsReleaseUpdate`), que solo descarga el jar del addon que corresponde.

La estrategia sigue siendo prudente:

1. usar CI para validar el reactor completo antes de etiquetar
2. usar **draft / prerelease** en el primer corte o cuando el equipo quiera revisar notas y `manifest.json`
3. documentar en Issues/Project qué addons ya tuvieron smoke real; no promocionar “todo estable” sin evidencia

Workflow manual: [`.github/workflows/release-monorepo-jars.yml`](../../.github/workflows/release-monorepo-jars.yml) (véase también [github-maintenance.md](../github-maintenance.md)).

## Qué sí conviene publicar

### Releases por stack base

Buenos candidatos:

- `dough-core`
- `Slimefun` Drake core

### Releases por librería o addon bien delimitado

Buenos candidatos:

- `InfinityLib`
- `InfinityExpansion`
- `SimpleUtils`
- `ExoticGarden`

### Releases por lote curado

Solo si se documentan bien:

- un batch de addons validados por build y smoke test
- una familia de módulos con dependencia común

## Qué conviene evitar

- Presentar un tag de monorepo como **“build de producción certificado”** sin matriz de riesgos por addon.
- Publicar releases en **cada push** sin revisión humana del tag y del changelog.
- Ignorar `manifest.json` del release si faltan módulos (`missing_modules`): revisar antes de anunciar el paquete.

## Arquitectura de CI (workflow unificado)

Un solo workflow [`.github/workflows/ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml) (`CI Monorepo 1.21`) con jobs encadenados:

- **foundation**: stack Maven base (Dough, Slimefun, SefiLib, InfinityLib, parche `commons-lang`).
- **maven_full_reactor**: reactor Maven completo (`mvn -B package -DskipTests -fae`) tras instalar núcleo en `.m2` y el `shadowJar` de SlimefunTranslation (coherente con UltimateGenerators2).
- **gradle_green**: `compileJava` de los 5 proyectos Gradle (`Galactifun`, `Bump`, `CustomItemGenerators`, `FastMachines`, `SlimefunTranslation`) tras instalar artefactos Maven requeridos.
- **ci_summary**: comprobacion opcional para branch protection.

`concurrency` con `cancel-in-progress` reduce ruido en Actions cuando llegan muchos `push` seguidos.

El corte local `2026-04-24` probo `mvn -B -DskipTests compile -fae` sobre los módulos Maven del reactor y `compileJava` sobre los proyectos Gradle declarados. Ese pase está reflejado en **CI Monorepo 1.21**; la acción recomendada es mantener el gate verde y validar **runtime** en servidor (p. ej. [DrakesCraft](https://drakescraft.cl)) addon por addon.

## Auto-updater y releases

Los addons que declaran `drakes-labs-autoupdate` consultan el **último** release del repo `SlimefunNewHorizons/drakes-slimefun-labs` y, si el asset `.jar` es más nuevo que la versión en ejecución, copian **solo ese jar** a la carpeta `updates/` del servidor (salvo desactivación por JVM/env). Detalle operativo y despliegue manual en **`docs/wiki/runtime-drakes-autoupdate.md`**. Inyección masiva del hook: `scripts/inject_drakes_autoupdate.py` (ver `scripts/README.md`).

## Política de releases

Una release debe hacerse cuando:

- el módulo o grupo tiene build validado de forma consistente
- su versión y nombre son razonables para consumo público
- no hay bloqueo activo conocido de API o dependencias
- si el módulo lo necesita, ya pasó una validación runtime básica

## Próximos pasos sugeridos

1. mantener `maven_full_reactor` y `gradle_green` verdes
2. revisar naming/versionado de módulos candidatos a release
3. crear draft releases manuales para módulos verdaderamente estables
4. solo después evaluar automatización de releases

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-27**.
> Baseline tecnico vigente: **Paper 1.21.x + Java 21** (rama `1.21-latin`).
> CI principal: **CI Monorepo 1.21**; releases de jars: workflow **Release monorepo JARs** + auto-updater documentado en `docs/wiki/`.
<!-- DRAKES-STATUS:END -->
