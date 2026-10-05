# Guía de trabajo en el monorepo

## Contexto base (léelo antes de tocar código)

- **Qué es este repo:** monorepo **Maven + Gradle** (Java **21**), **un solo árbol de fuentes**, no un plugin suelto. Incluye **Slimefun 4 Drake**, **dough-core**, parches internos y **decenas de addons** bajo `sources/` (Maven) más proyectos **Gradle** (Galactifun, Bump, etc.) declarados en `settings.gradle.kts`.
- **Rama estable 1.21.x:** **`main`**. Baseline de compilación: **Paper 1.21.1-R0.1-SNAPSHOT** en el `pom.xml` raíz; smoke con Paper **1.21.1** y **1.21.11** según perfil (`scripts/smoke/smoke-profiles.json`).
- **Rama experimental 26.x:** **`26.X-ToTheStars`** — porte hacia **Paper API 26.x** (perfil `paper-26-preview`). No mezclar trabajo masivo de 26.x en PRs contra `1.21-latin` sin acuerdo. **Calendencia:** el equipo puede acotar ~**un mes** de ventana antes de retomar 26.x a ritmo de sprint (el cierre 1.21.x fue muy intenso); mientras, prioridad **fixes y estabilidad en 1.21-latin**.
- **Validación en mundo real:** survival **[DrakesCraft](https://drakescraft.cl)** (Chile; IPs típicas `play.drakescraft.cl` / `mc.drakescraft.cl`). Pulido addon por addon: **comunidad**, **Chagui**, Issues.

## Reglas de trabajo

1. Leer **`README.md`** raíz y **`docs/es/PLUGIN_MATRIX.md`** (o regenerar: `python scripts/generate_plugin_matrix.py` si cambiaron módulos o CI).
2. Compilar **acotado**: `mvn -pl ruta/modulo -am -DskipTests package` (no arranques el reactor completo salvo que el cambio lo exija o sea smoke/release).
3. Separar **fallo de POM / resolución** vs **fallo de API Paper / Slimefun** antes de parchear a ciegas.
4. **Slimefun sombreado:** tras `package` de Slimefun, Dough queda bajo `com.github.drakescraft_labs.slimefun4.libraries.dough.protection.*`. Los addons que llaman `Slimefun.getProtectionManager()` deben importar esos tipos relocados en el classpath del addon, no siempre `dev.drake.dough.protection.*` crudo. Script de ayuda: `python scripts/fix_dough_compilation_imports.py` (excluye core).
5. **Smoke:** scripts en `scripts/smoke/` (`run-smoke-server.ps1`, `smoke_orchestrate.py`); **ProtocolLib** se intenta vía Python y fallback PowerShell. Perfiles `*-paper-12111` para Paper 1.21.11.
6. **Releases:** workflow manual **Release monorepo JARs** → un `.jar` por addon como assets del mismo release; ver `docs/github-maintenance.md`.
7. Si cambia el **estado** de un módulo respecto al tablero, actualizar matriz y alinear [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) según `docs/PROJECT_BOARD_SYNC.md`.

## Comando de referencia

```bash
mvn -B -pl sources/community-addons/Ejemplo -am -DskipTests package
```

## Criterio de investigación

- Si el addon ya usa coordenadas Drake pero falla: revisar **parent POM**, dependencia **`slimefun-core`**, y si el fallo es en **compile** aislado vs **`package`** en el reactor (orden de módulos).
- **Gradle addons:** a veces hace falta `mvn install` de jars Drake en `mavenLocal()` (p. ej. InfinityExpansion) antes de `gradlew compileJava`; ver `docs/es/pending-modules.md`.

## Criterio de cierre

No dejar PRs a medio mezclar. Si algo no compila o no arranca en smoke, documentar **exactamente** el error y la ruta mínima reproducible.

## Enlaces relacionados

- [Resumen rápido del repositorio](contexto-rapido-repo.md)
- [Checklist de migración](migration-checklist.md)
- [Roadmap de estabilización](stabilization-roadmap.md)
- [Módulos pendientes](pending-modules.md)
- [README raíz](../../README.md)

<!-- DRAKES-STATUS:BEGIN -->
> **2026-04-25** — Guía ampliada (smoke, Dough reloc, 26.x, DrakesCraft, calendencia ~1 mes).
> Baseline: **Paper 1.21.x + Java 21** en **`main`**. CI: **Monorepo 1.21**.
> **26.x:** rama `26.X-ToTheStars`, foco pausado ~1 mes antes de sprint fuerte.
<!-- DRAKES-STATUS:END -->
