# Release and CI Strategy

## Current policy

Per-module readiness is tracked in the generated root [`README.md`](../../README.md) and [`docs/es/PLUGIN_MATRIX.md`](../es/PLUGIN_MATRIX.md) (`python scripts/generate_plugin_matrix.py`). Keep the org [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) aligned using [`docs/PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md).

The repository is operated in an incremental-stability model:

- Keep the main branch (`1.21-latin`) green at all times.
- Keep full Maven + Gradle compile coverage in CI.
- Prefer deterministic dependencies over snapshot-only external sources.

## CI shape

Single workflow [`.github/workflows/ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml) (`CI Monorepo 1.21`):

- **foundation**: Maven baseline (Dough, Slimefun, SefiLib, InfinityLib, commons-lang patch)
- **maven_full_reactor**: full Maven reactor (`mvn -B package -DskipTests -fae`) after publishing the core stack to `.m2` and building the SlimefunTranslation Gradle `shadowJar` (required by UltimateGenerators2).
- **gradle_green**: `compileJava` for all 5 Gradle projects after publishing required Maven artifacts locally
- **ci_summary**: optional umbrella check for branch protection

Concurrency cancels in-progress runs on the same branch to reduce “war zone” noise from stacked pushes.

## Release posture

- Core artifacts are validated continuously through CI.
- Addon-level production readiness still requires runtime smoke checks.
- A passing pipeline is a required condition, not a sufficient production guarantee.

### Monorepo GitHub Release (many JARs, one tag)

The **Release monorepo JARs** workflow (`.github/workflows/release-monorepo-jars.yml`, see [github-maintenance.md](../github-maintenance.md)) builds `mvn package`, runs `scripts/release/collect_monorepo_jars.py`, and publishes **one `.jar` asset per Maven module** plus `manifest.json`. That is a **lab distribution** for selective downloads—not a claim that every addon is equally gameplay-ready.

Addons that depend on **`drakes-labs-autoupdate`** call `DrakesLabsReleaseUpdate.schedule(...)` and compare the running jar to **`releases/latest`** of `SlimefunNewHorizons/drakes-slimefun-labs`, then download **only their matching asset** into the server **`updates/`** folder (unless disabled). Operational notes: [docs/wiki/runtime-drakes-autoupdate.md](../wiki/runtime-drakes-autoupdate.md). Mass injection helper: `scripts/inject_drakes_autoupdate.py` (see [scripts/README.md](../../scripts/README.md)).

## Next milestones

1. Keep `maven_full_reactor` and `gradle_green` stable.
2. Remove legacy dependency bottlenecks.
3. Track runtime validation status per addon group.

<!-- DRAKES-STATUS:BEGIN -->
> Sync: **2026-04-27**. Baseline: **Paper 1.21.x + Java 21** on `main`.
> CI: **CI Monorepo 1.21**; JAR releases: **Release monorepo JARs** + autoupdater docs under `docs/wiki/`.
<!-- DRAKES-STATUS:END -->
