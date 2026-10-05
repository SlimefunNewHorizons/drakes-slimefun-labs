# Estándares de Desarrollo

## Principios Operativos

- preferir cambios pequeños y validados
- no compilar el reactor completo salvo necesidad real
- diagnosticar primero `pom.xml` y dependencias antes de entrar a port profundo de API
- documentar cualquier cambio de estado: actualizar entradas en `scripts/generate_plugin_matrix.py` si aplica, ejecutar `python scripts/generate_plugin_matrix.py`, y alinear el [tablero de la org](https://github.com/orgs/SlimefunNewHorizons/projects/1) (`docs/PROJECT_BOARD_SYNC.md`)

## Estándar de Build

```powershell
mvn -pl ruta/del/modulo -am -DskipTests package
```

También es válido usar `compile` cuando solo se necesita validación rápida de compilación.

## Criterio de Documentación

Cuando un addon cambia de estado:

1. ajustar criterios en `scripts/generate_plugin_matrix.py` (gates, evidencia local, bloqueos Gradle) si el hecho tecnico cambio
2. ejecutar `python scripts/generate_plugin_matrix.py` para refrescar `README.md` y `docs/es/PLUGIN_MATRIX.md`
3. actualizar el tablero [Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1) siguiendo `docs/PROJECT_BOARD_SYNC.md`
4. dejar observaciones si compila pero sigue teniendo riesgo de runtime

## Criterio de "No Listo"

Un addon no debe marcarse como listo si:

- sigue fallando en build
- depende de una variante vieja no adoptada
- compila pero tiene una integración crítica no revisada y sin observación documentada

## Integración al Reactor (Flujo DrakesLab)

Antes de dar por integrado un addon, se debe seguir estrictamente este flujo asistido por el **Manager**:

1. **Auditoría Sentinel**: `python scripts/manager.py security` (Obligatorio). No se permiten dependencias vulnerables.
2. **Reparación de Seguridad**: `python scripts/manager.py security-fix` si se detectan vulnerabilidades reparables.
3. **Unificación de Identidad**: `python scripts/manager.py repair` para sincronizar GroupId y Parent.
4. **Validación de Build**: Validar build aislado con `mvn -pl ... -am -DskipTests package`.
5. **Registro en Reactor**: Agregar el módulo al `pom.xml` raíz solo tras superar los pasos anteriores.

## Enlaces Relacionados

- [[Checklist de Migración]]
- [[Dev-Setup]]
- [[Tomorrow-Handoff]]

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-24**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> CI principal en `main`: **CI Monorepo 1.21** cubre reactor Maven completo + 5 Gradle.
> Nota: quedan pendientes smoke tests de runtime y estrategia de releases; no hay bloqueos de compilacion en el corte actual.
<!-- DRAKES-STATUS:END -->
