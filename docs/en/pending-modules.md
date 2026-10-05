# Pending modules

This document tracks the **technical** monorepo state after CI stabilization. It is **not** a list of every possible survival bug: gameplay issues land in **Issues**, and polish is absorbed by the community, **Chagui**, and the **[DrakesCraft](https://drakescraft.cl)** server (Chile) over time.

## What we intentionally do *not* track here

- Per-addon “fine polish” in a live economy/PvE stack.
- 1:1 upstream parity unless it blocks compile or agreed smoke startup.
- **Paper 26.x** work — use branch **`26.X-ToTheStars`**.

## Single source of truth (inventory)

- Per-module status (CI-ready / local-only / in progress / build-blocked, currently 0 blocked in the local cut) and notes: [`docs/es/PLUGIN_MATRIX.md`](../es/PLUGIN_MATRIX.md) (generated; run `python scripts/generate_plugin_matrix.py`).
- The same table is embedded in the root [`README.md`](../../README.md).
- Org board: [DrakesCraft-Labs / Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) — see [`docs/PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md) to align cards with the matrix.

## Current state

- **CI Monorepo 1.21** (`ci-monorepo-121.yml`): covers the full Maven reactor (`maven_full_reactor`) and all 5 Gradle projects (`gradle_green`).
- **Full local compile**: the full Maven reactor and all 5 declared Gradle projects compile on the `2026-04-24` cut.
- **Runtime smoke**: profiles exist under `scripts/smoke/`; run them on large changes (or rely on whoever deploys to **DrakesCraft**).
- **Next friction is expected in gameplay**, not in “does it compile”.

## Recent technical audit (Gradle batch, root reactor)

Cut date: `2026-04-24`.

### Count summary (local evidence on this date)

| Tool | OK | FAIL | Notes |
|------|---:|-----:|-------|
| Maven (`mvn -B -DskipTests compile -fae`) | 81 reactor modules | 0 | `BUILD SUCCESS` |
| Gradle (`compileJava` per declared project) | 5 Gradle projects | 0 | Galactifun, Bump, CustomItemGenerators, FastMachines, and SlimefunTranslation compile |

### Full Maven batch

- Full Maven reactor: **BUILD SUCCESS** with `mvn -B -DskipTests compile -fae`.
- Previously blocking modules (`GeneticChickengineering-Reborn`, `EMCTech`, `UltimateGenerators2`, `VillagerTrade`, `SlimeHUD`, `SfBetterChests`, and others) now compile against the Drake baseline.
- Paper/Bukkit legacy API warnings remain but no longer block compilation.

### Gradle slice (same cut)

- `sources:batch-2-expansion:Galactifun`: **BUILD SUCCESSFUL**.
- `sources:community-addons:Bump`: **BUILD SUCCESSFUL**.
- `sources:community-addons:CustomItemGenerators`: **BUILD SUCCESSFUL** after removing `sf4k`, using direct `JavaPlugin`, and registering through a `SlimefunAddon` adapter.
- `sources:community-addons:FastMachines`: **BUILD SUCCESSFUL** after aligning Drake dependencies, installing `InfinityExpansion-drake` locally, and adding local Kotlin bridges.
- `sources:community-addons:SlimefunTranslation`: **BUILD SUCCESSFUL** after Paper 1.21 API adjustment (`EntityType.ITEM`).

## Tools and bridges added

- `scripts/port_paper_121.py`: conservative batch patcher for Paper 1.21.1 and package moves.
- BusyBiscuit package bridges in Slimefun core (`io.github.thebusybiscuit.slimefun4.*`) for dependencies compiled against upstream Slimefun packages.
- Local Kotlin bridges for Gradle addons: `MenuBlock`, `TickingMenuBlock`, and `DrakeItemBuilderCompat`.
- Local build recipe for FastMachines: install `InfinityExpansion-drake` with `mvn -B -DskipTests install -pl sources/repos-to-port/InfinityExpansion -am` before Gradle resolution if `mavenLocal()` is empty.

## Automated porting (batch patches)

Tool: `scripts/port_paper_121.py` (conservative textual rules for Paper 1.21.1). Includes the `libraries-paperlib` rule (`com.github.drakescraft_labs.slimefun4.libraries.paperlib` → `io.papermc.lib`).

```bash
python scripts/port_paper_121.py --list-rules
python scripts/port_paper_121.py --dry-run --path sources/community-addons/MyAddon
python scripts/port_paper_121.py --apply --path sources/community-addons/MyAddon --rules libraries-dough,libraries-commons
```

Always run `--dry-run` first, review the diff, then `--apply`. With `--backup`, a `.portbak` is written per file (do not commit backups).

## Suggested work blocks

1. Keep `maven_full_reactor` and `gradle_green` as required closure checks.
2. Run smoke after big merges (or trust CI + production deploy on **DrakesCraft**).
3. Reduce compatibility debt where a local bridge can become shared API.
4. Triage Issues from real survival reports (community + Chagui).

## Definition of done (per module)

A module is considered closed when:

- it compiles in the matching CI job in [`ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml) (or a dedicated workflow),
- it does not break the global pipeline,
- and it has minimal runtime validation when gameplay risk is high.

<!-- DRAKES-STATUS:BEGIN -->
> Sync cut: **2026-04-24** (Maven + Gradle build); README/docs **2026-04-25** (“what’s left” aligned with community + DrakesCraft).
> Baseline: **Paper 1.21.x + Java 21** on **`main`**.
> CI: **Monorepo 1.21** (full reactor + Gradle). Optional smoke + **multi-JAR release** documented in README / `github-maintenance.md`.
> What’s next: **gameplay polish on servers** (open-ended, not a finite lab checklist).
<!-- DRAKES-STATUS:END -->
