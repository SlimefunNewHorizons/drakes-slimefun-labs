<div align="center">

  <img src="https://raw.githubusercontent.com/SlimefunNewHorizons/drakes-slimefun-labs/main/labs_addons_banner.svg" alt="Drakes Slimefun Labs" width="920" />

# Drakes Slimefun Labs — Legacy Archive

**This monorepo is a legacy archive, not the source of truth for migrated addon development, versions, builds, or releases.**

</div>

## Current model

Each migrated addon lives in its own repository under
[DrakesCraft Labs](https://github.com/SlimefunNewHorizons). Each standalone repository owns:

- its Maven/Gradle project version;
- its dependencies and Java 21 toolchain;
- its tests and CI;
- its release artifacts and changelog;
- its production fixes.

The source trees retained here are historical snapshots only. Do not start new
work here and do not deploy JARs built from this repository. The migration
manifest distinguishes migrated addons from the remaining archive inventory;
unmigrated entries must be extracted deliberately before they become active
standalone sources.

## Finding the canonical repository

- Human-readable map: [docs/CANONICAL_SOURCES.md](docs/CANONICAL_SOURCES.md)
- Machine-readable migration manifest: [migration/addons.json](migration/addons.json)

The root Maven project deliberately has no modules and deployment is disabled.
The root Gradle project also has no included addon projects. This prevents an
accidental global version from overriding an addon's independent version.

## Historical validation

    mvn validate
    ./gradlew legacyStatus

These commands validate the archive marker; they do not compile or publish
addons. Clone the canonical repository listed in the migration manifest to work
on an addon.

## Production

Production continues to use individually reviewed JARs. Repository extraction
does not deploy plugins, modify player data, or restart DrakesCraft.

---

Maintained by [JackStar6677-1](https://github.com/JackStar6677-1) and
[DrakesCraft Labs](https://github.com/SlimefunNewHorizons).

---

## 📄 License & Upstream Attribution

This project is a sovereign fork maintained by [**JackStar6677-1**](https://github.com/JackStar6677-1) under [**DrakesCraft Labs**](https://github.com/SlimefunNewHorizons).

- **Original Project:** Created by the upstream authors and the open-source community.
- **DrakesCraft Optimizations:** Modernized for Paper/Purpur 1.21.11+, Java 21, high concurrency, asynchronous safety, and exploit/duplication prevention.
- **License:** Distributed under the original **GNU General Public License v3.0 (GPLv3)** (or original upstream license). See the [LICENSE](LICENSE) file for complete terms.
