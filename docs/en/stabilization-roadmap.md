# Stabilization Roadmap

## Goal

This page translates the root [`README.md`](../../README.md) and the generated [`docs/es/PLUGIN_MATRIX.md`](../es/PLUGIN_MATRIX.md) inventory into an operational closure sequence. The goal is not only to "make it compile", but to close the real backlog without mixing old variants, bloating the reactor, or leaving ambiguous states behind.

## Base state (audited)

- Unified reactor inventory: **86 entries** (Maven + Gradle; see the generated matrix).
- **CI-ready**: [`ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml) covers 100% of the inventory: `maven_full_reactor` + `gradle_green`.
- **Local-only green**: kept as historical evidence from the `2026-04-24` cut, but it is no longer the primary status.
- **In progress / Build-blocked**: no active compile blockers in the local cut; remaining risk moves to runtime smoke testing and release discipline.
- Org board: [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) — keep aligned with the matrix ([`docs/PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md)).

## Recommended Order

### Phase 1: keep full CI green

This is highest priority because the gate now covers the whole reactor.

1. Keep `maven_full_reactor` as the 81-module Maven proof.
2. Keep `gradle_green` as the 5-project Gradle proof.
3. Keep `mvn -B -DskipTests compile -fae` as the local preflight for large changes.

### Phase 2: runtime smoke tests

These are reasonable first runtime targets because they now compile but have mechanics or integrations worth validating:

1. `FastMachines`
2. `CustomItemGenerators`
3. `UltimateGenerators2`
4. `GeneticChickengineering-Reborn`
5. `SlimeHUD`

### Phase 3: reduce compatibility debt

Review whether local bridges should stay per addon or move into shared utilities:

1. BusyBiscuit bridges in Slimefun core (`io.github.thebusybiscuit.slimefun4.*`).
2. `MenuBlock` / `TickingMenuBlock` in Kotlin addons.
3. `DrakeItemBuilderCompat` and replacements for GuizhanLib Kotlin extensions.

### Phase 4: triage first, port second

Do not enter these blindly without reviewing structure, build system, and external dependencies first.

- `Galactifun`
- `Bump`
- `CustomItemGenerators`
- `FastMachines`
- `SlimefunTranslation`
- `UltimateGenerators2`
- `Netheopoiesis`
- `SlimeFrame`
- `SlimefunAdvancements`

### Phase 5: historical or ambiguous variants

These should not be mixed with active variants without an explicit decision.

- `Cultivation`
- `Networks`
- `EMC2`
- `SlimefunWarfare`

## Per-Addon Working Criteria

Before touching code:

1. confirm whether the problem is `pom.xml`/dependency-related or API/code-related
2. check whether it already uses `dev.drake.dough.*`
3. confirm whether it inherits from the reactor parent
4. compile only that module with `-pl` and `-am`

Base command:

```powershell
mvn -pl path/to/module -am -DskipTests package
```

## Ready Criteria

An addon can be marked as ready for `1.21.11` when:

- it builds consistently either inside the reactor or through isolated module build
- it no longer depends on old coordinates such as `dev.drake:Slimefun:5.0-Drake-1.21.11`
- it no longer has known active API failures
- its status is reflected in the root `README.md` / generated `docs/es/PLUGIN_MATRIX.md` and tracking pages

## Final Project Closure

The project is not considered closed only because it reaches `BUILD SUCCESS`.

At the end of the backlog, the team should still do:

1. smoke testing on Paper/Purpur `1.21.11`
2. basic runtime validation for sensitive addons
3. cleanup of temporary untracked logs
4. final review of the `README` and wiki inventory

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-24**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> Main CI on `main`: **CI Monorepo 1.21** covers the full Maven reactor + 5 Gradle projects.
> Note: runtime smoke tests and release strategy remain; there are no compile blockers in the current cut.
<!-- DRAKES-STATUS:END -->
