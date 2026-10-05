# 🛡️ DrakesLab CI Architecture & Lifecycle

> **Nota (2026):** la operación real del monorepo está en **un solo workflow** [`.github/workflows/ci-monorepo-121.yml`](.github/workflows/ci-monorepo-121.yml) y en las guías [`docs/es/release-and-ci-strategy.md`](docs/es/release-and-ci-strategy.md) / [`docs/en/release-and-ci-strategy.md`](docs/en/release-and-ci-strategy.md). Lo que sigue describe un modelo antiguo de “Gates” dispersos; conservarlo solo como contexto histórico.

Este documento detalla el funcionamiento del ecosistema de Integración Continua (CI) modularizado para el monorepo de Slimefun.

## 🏗️ Estructura de Gates (Calidad Modular)
Para evitar que un fallo en un addon experimental bloquee todo el proyecto, hemos dividido la compilación en 4 "Gates" (Puertas de Calidad) independientes:

1.  **Gate: Foundation** 🧱: El núcleo. Compila `slimefun-core`, `dough-core`, `sefilib-core` e `infinitylib-core`. Si esto falla, nada más funciona.
2.  **Gate: Standard Addons** 📦: Addons oficiales y estables (ExoticGarden, DynaTech, etc.).
3.  **Gate: Expansion Addons** 🌌: Addons de gran envergadura (SlimeTinker, TranscEndence).
4.  **Gate: Community Addons** 👥: El archivo de la comunidad. Es el área más inestable y propensa a errores.

## 🏷️ Convención de Nombres (Identidad Única)
Para evitar conflictos (Errores 422) con el registro de GitHub Packages, todos los componentes del núcleo utilizan el sufijo `-core`:
- `dev.drake:slimefun-core`
- `dev.drake.dough:dough-core`
- `dev.sefiraat:sefilib-core`
- `io.github.mooy1:infinitylib-core`

## 📈 Versiones y SNAPSHOTs
Actualmente operamos en la **v7-SNAPSHOT**. 
- **Escape de Corrupción**: Subimos a la v7 para limpiar metadatos corruptos de versiones anteriores en el registro.
- **Despliegue Directo**: Cada Gate exitoso publica sus artefactos automáticamente en [GitHub Packages](https://github.com/orgs/SlimefunNewHorizons/packages).

## 🚀 Uso del CI
### Disparo Manual (Modo Silencioso)
Para evitar spam de correos, los disparos automáticos por `push` están **desactivados** temporalmente.
Para ejecutar un Gate:
1. Ve a la pestaña **Actions**.
2. Selecciona el Gate deseado (ej. `Gate: Foundation`).
3. Haz clic en **Run workflow**.

### Re-activar Disparos Automáticos
Para volver al modo automático, edita los archivos en `.github/workflows/` y des comenta las líneas:
```yaml
on:
  push:
    branches: [1.21-latin]
```

## 🛠️ Cómo usar como Dependencia
Añade el repositorio de DrakesLab a tu `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/SlimefunNewHorizons/drakes-slimefun-labs</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.drake</groupId>
        <artifactId>slimefun-core</artifactId>
        <version>7.0-Drake-1.21.11-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---
*Documentación de CI del laboratorio DrakesCraft-Labs.*

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-24**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> CI principal en `main`: **Gates 1-5 en verde**.
> Nota: el monorepo completo sigue en migracion incremental por lotes.
<!-- DRAKES-STATUS:END -->
