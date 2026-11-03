# Resumen rápido del repositorio (copiar / pegar)

Bloque de **contexto corto** útil al abrir una sesión de trabajo o para notas internas del equipo.

```text
Repo: SlimefunNewHorizons / drakes-slimefun-labs
Rama estable: 1.21-latin (Paper 1.21.x API, Java 21). NO es un solo plugin: monorepo Maven+Gradle (~86 módulos).
Rama experimental 26.x: 26.X-ToTheStars (perfil Maven paper-26-preview). No mezclar con 1.21 sin acuerdo explícito del equipo.
Prioridad habitual: mantener 1.21-latin + Issues + servidor de referencia DrakesCraft; ventana de ~1 mes de estabilización antes de empujar 26.x a ritmo de sprint fuerte.

Antes de codificar:
- README.md raíz + docs/es/PLUGIN_MATRIX.md
- Fallo Maven vs API Paper/Slimefun
- Slimefun package sombrea Dough → imports protection bajo com.github.drakescraft_labs.slimefun4.libraries.dough... (fix_dough_compilation_imports.py)

Build típico: mvn -B -pl <ruta-modulo> -am -DskipTests package

Smoke: scripts/smoke/ / smoke_orchestrate.py (perfiles foundation, monorepo-all, paper 1.21.11 en JSON).
Release (muchos `.jar` en un solo tag): workflow release-monorepo-jars.yml (Actions manual).
Servidor referencia Chile: https://drakescraft.cl — pulido juego: comunidad + Chagui + Issues.
```

## Detalle extendido (si hace falta más de un párrafo)

| Tema | Dónde |
|------|--------|
| Wiki (mapa y updater) | `docs/wiki/README.md` |
| Qué queda “lab vs juego” | `docs/es/pending-modules.md`, `README.md` |
| Smoke Paper + ProtocolLib | `scripts/smoke/README.md` |
| Releases (assets JAR) | `docs/github-maintenance.md` |
| Guía larga de trabajo en el monorepo | `docs/es/guia-monorepo.md` |
| 26.x qué es / conclusión | rama `26.X-ToTheStars` → `docs/paper-26-base.md` en GitHub |

## Próximo enfoque recomendado (orden sugerido)

1. Mantener **CI Monorepo 1.21** verde (`maven_full_reactor`, `foundation`, `gradle_green`).
2. **Smoke** tras cambios grandes en addons sensibles (`python scripts/smoke/smoke_orchestrate.py full --profile monorepo-all --timeout 420` o mayor).
3. **Issues** triage con lo que salga en **DrakesCraft** / jugadores.
4. **26.x** solo en rama dedicada, sin urgencia hasta pasada la ventana acordada por el equipo si aplica.

## Referencias

- [Guía de trabajo en el monorepo](guia-monorepo.md)
- [Checklist de migración](migration-checklist.md)
- [Módulos pendientes](pending-modules.md)
- [Tomorrow handoff](tomorrow-handoff.md)

<!-- DRAKES-STATUS:BEGIN -->
> **2026-04-27** — Incluye wiki en `docs/wiki/` (updater y despliegue).
<!-- DRAKES-STATUS:END -->
