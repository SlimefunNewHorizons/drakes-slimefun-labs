# Sincronizar GitHub Projects con la matriz

**Tablero:** [DrakesCraft-Labs / Project 1](https://github.com/orgs/SlimefunNewHorizons/projects/1)

**Datos auditados:** [docs/es/PLUGIN_MATRIX.md](es/PLUGIN_MATRIX.md) (generado; no editar a mano). Regenerar README y matriz:

```bash
python scripts/generate_plugin_matrix.py
```

## CLI `gh` y permisos

Para leer o mutar Projects v2 hace falta refrescar scopes:

```bash
gh auth refresh -h github.com -s read:project,project
```

Sin eso, los subcomandos `gh project` fallan por permisos.

## Proceso recomendado

1. Regenerar la matriz (comando anterior).
2. Abrir el Project 1 en el navegador.
3. Por cada módulo del monorepo: alinear la columna de estado con la columna **Estado** de `PLUGIN_MATRIX.md`, revisar **Updater GH** (dependencia `drakes-labs-autoupdate`) y copiar o resumir **Observaciones**.
4. Añadir tarjetas para módulos nuevos en `sources/...` que aún no estén en el tablero.

## Sugerencia de mapeo

| Estado en matriz | Columna típica en el tablero |
|------------------|------------------------------|
| Listo (CI) | Done / Ready |
| Listo (local) | In progress (falta validar en CI) |
| En curso | Todo o In progress |
| Bloqueado (build) | Blocked |

Ajusta los nombres a las columnas reales del Project 1.
