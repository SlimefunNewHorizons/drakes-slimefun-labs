# Repository session brief (copy / paste)

**Short context** block for a new work session or internal team notes.

```text
Repo: DrakesCraft-Labs / drakes-slimefun-labs
Stable branch: main (Paper 1.21.x API, Java 21). NOT a single plugin: Maven+Gradle monorepo (~86 modules).
Networks addon: standalone repo SlimefunNewHorizons/NetworksV6-drake on main (1.21-latin obsoleto).
Experimental 26.x: 26.X-ToTheStars (Maven profile paper-26-preview). Do not mix into 1.21 without explicit maintainer agreement.
Typical priority: keep main green + Issues + DrakesCraft reference server; expect roughly a one-month stabilization window before pushing 26.x at full sprint pace.

Before coding:
- Root README.md + docs/es/PLUGIN_MATRIX.md
- Maven failure vs Paper/Slimefun API failure
- Slimefun shaded jar relocates Dough → use com.github.drakescraft_labs.slimefun4.libraries.dough.protection.* for ProtectionManager paths when compiling addons (fix_dough_compilation_imports.py)

Typical build: mvn -B -pl <module-path> -am -DskipTests package

Smoke: scripts/smoke/ + smoke_orchestrate.py (profiles include Paper 1.21.11 variants).
Multi-JAR release: release-monorepo-jars.yml (manual Actions).
Reference survival server (Chile): https://drakescraft.cl — gameplay polish: community + Chagui + Issues.
```

## More detail (if one paragraph is not enough)

| Topic | Where |
|-------|--------|
| Lab wiki (updater and deploy) | `docs/wiki/README.md` |
| Lab vs gameplay backlog | `docs/en/pending-modules.md`, root `README.md` |
| Paper smoke + ProtocolLib | `scripts/smoke/README.md` |
| Multi-JAR releases | `docs/github-maintenance.md` |
| Long-form monorepo work guide | `docs/en/monorepo-work-guide.md` |
| What 26.x is | branch `26.X-ToTheStars` → `docs/paper-26-base.md` on GitHub |

## Suggested focus order

1. Keep **CI Monorepo 1.21** green.
2. Run **smoke** after large addon changes.
3. Triage **Issues** from **DrakesCraft** / players.
4. Keep **26.x** work on its branch without forcing timeline during the agreed recovery window.

## References

- [Monorepo work guide](monorepo-work-guide.md)
- [Migration checklist](migration-checklist.md)
- [Pending modules](pending-modules.md)
- [Tomorrow handoff](tomorrow-handoff.md)

<!-- DRAKES-STATUS:BEGIN -->
> **2026-04-25** — Expanded brief (26.x cadence, DrakesCraft, smoke, release).
<!-- DRAKES-STATUS:END -->
