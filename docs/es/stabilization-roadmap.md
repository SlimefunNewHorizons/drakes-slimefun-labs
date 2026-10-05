# Roadmap de Estabilización

## Objetivo

Esta página traduce el inventario del [`README.md`](../../README.md) y la [`PLUGIN_MATRIX.md`](PLUGIN_MATRIX.md) (generada) a una secuencia operativa. La meta no es solo "hacer que compile", sino cerrar el backlog real sin mezclar variantes viejas, sin inflar el reactor y sin dejar estados ambiguos.

## Estado base (auditable)

- Inventario unificado en el reactor: **86 entradas** (Maven + Gradle; ver matriz generada).
- **Listo (CI)**: [`ci-monorepo-121.yml`](../../.github/workflows/ci-monorepo-121.yml) cubre el 100% del inventario: `maven_full_reactor` + `gradle_green`.
- **Listo (local)**: queda como evidencia historica del corte `2026-04-24`, pero ya no es el estado principal.
- **En curso / Bloqueado**: sin bloqueos de compilacion activos en el corte local; el riesgo pendiente se mueve a CI ampliado y runtime smoke.
- Tablero org: [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) — mantener alineado con la matriz ([`PROJECT_BOARD_SYNC.md`](../PROJECT_BOARD_SYNC.md)).

## 🛡️ Fortificación y Seguridad (Especial 2026)

A partir de la intervención de abril 2026, el reactor ha elevado sus requisitos de seguridad:

1. **Shadow Patching**: Implementado para eliminar vulnerabilidades en librerías EOL (Commons Lang v2). ✅
2. **Estándares 2026**: Todas las dependencias críticas (Spring, Guava) deben estar en sus versiones de seguridad de 2026. ✅
3. **Manager Sentinel**: Auditoría activa obligatoria para detectar inyecciones de versiones obsoletas. ✅

## Orden Recomendado

### Fase 1: mantener CI completo verde

Esto tiene prioridad maxima porque el gate ahora cubre todo el reactor.

1. Mantener `maven_full_reactor` como prueba de los 81 modulos Maven.
2. Mantener `gradle_green` como prueba de los 5 proyectos Gradle.
3. Usar el pase local `mvn -B -DskipTests compile -fae` como verificacion previa a cambios grandes.

### Fase 2: smoke tests de runtime

Estos son candidatos razonables para validar primero porque ya compilan pero tienen mecanicas o integraciones sensibles:

1. `FastMachines`
2. `CustomItemGenerators`
3. `UltimateGenerators2`
4. `GeneticChickengineering-Reborn`
5. `SlimeHUD`

### Fase 3: reducir deuda de compatibilidad

Revisar si los bridges locales deben quedarse por addon o moverse a una utilidad compartida:

1. Bridges BusyBiscuit en Slimefun core (`io.github.thebusybiscuit.slimefun4.*`).
2. `MenuBlock` / `TickingMenuBlock` en addons Kotlin.
3. `DrakeItemBuilderCompat` y reemplazos de extensiones GuizhanLib Kotlin.

### Fase 4: triage primero, port después

No conviene entrar aquí sin revisar antes estructura, build y dependencia externa.

- `Galactifun`
- `Bump`
- `CustomItemGenerators`
- `FastMachines`
- `SlimefunTranslation`
- `UltimateGenerators2`
- `Netheopoiesis`
- `SlimeFrame`
- `SlimefunAdvancements`

### Fase 5: variantes históricas o ambiguas

Estos casos no deben mezclarse con las variantes activas sin una decisión explícita.

- `Cultivation`
- `Networks`
- `EMC2`
- `SlimefunWarfare`

## Criterio de Trabajo por Addon

Antes de tocar código:

1. Confirmar si el problema es de `pom.xml`/dependencias o de API/código.
2. Revisar si ya usa `dev.drake.dough.*`.
3. Validar si hereda del parent del reactor.
4. **Ejecutar `python scripts/manager.py security`** para auditar vulnerabilidades. 🛡️
5. Compilar solo el módulo con `-pl` y `-am`.

Comando base:

```powershell
mvn -pl ruta/del/modulo -am -DskipTests package
```

## Criterio de "Listo"

Un addon puede marcarse como listo para `1.21.1` cuando:

- Compila dentro del reactor o en build aislado de forma consistente.
- Ya no depende de coordenadas viejas como `dev.drake:Slimefun:5.0-Drake-1.21.1`.
- **Supera la auditoría de seguridad del Módulo Sentinel**. ✅
- No deja errores activos de API conocidos.
- Su estado quedó reflejado en `README.md` y páginas de seguimiento.

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-05-20**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> CI principal en `main`: **CI Monorepo 1.21** cubre reactor Maven completo + modulos Gradle.
> Nota: quedan pendientes smoke tests de runtime y estrategia de releases; no hay bloqueos de compilacion en el corte actual.
<!-- DRAKES-STATUS:END -->
