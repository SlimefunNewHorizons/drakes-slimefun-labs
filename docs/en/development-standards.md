# Development Standards

## Operating Principles

- prefer small validated changes
- do not compile the full reactor unless there is a real reason
- diagnose `pom.xml` and dependencies before deep API porting
- document every state change in the root `README.md` (regenerate the embedded table with `python scripts/generate_plugin_matrix.py`), keep `docs/es/PLUGIN_MATRIX.md` in sync, update the [org Project board](https://github.com/orgs/SlimefunNewHorizons/projects/1) when tracking changes, and update the wiki if you maintain one

## Build Standard

```powershell
mvn -pl path/to/module -am -DskipTests package
```

It is also valid to use `compile` when only a quick compile validation is needed.

## Documentation Standard

When an addon changes state:

1. update the generator inputs if needed (`scripts/generate_plugin_matrix.py` gate lists / local evidence)
2. run `python scripts/generate_plugin_matrix.py` to refresh `README.md` + `docs/es/PLUGIN_MATRIX.md`
3. align the org [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) cards (see `docs/PROJECT_BOARD_SYNC.md`)
4. leave observations if it compiles but still has runtime risk

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-24**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> CI principal en `main`: **CI Monorepo 1.21** cubre reactor Maven completo + 5 Gradle.
> Nota: quedan pendientes smoke tests de runtime y estrategia de releases; no hay bloqueos de compilacion en el corte actual.
<!-- DRAKES-STATUS:END -->
