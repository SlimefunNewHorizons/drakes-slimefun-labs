# Runtime: DrakesLabs auto-updater (GitHub Releases)

Muchos addons del monorepo dependen de **`drakes-labs-autoupdate`** y llaman a `DrakesLabsReleaseUpdate.schedule(this, "<artifactId>")` al habilitar el plugin. La lógica vive en `sources/drakes-labs-autoupdate/…/DrakesLabsReleaseUpdate.java`.

## Qué hace

1. Consulta **`GET /repos/SlimefunNewHorizons/drakes-slimefun-labs/releases/latest`**.
2. Busca un **asset `.jar`** cuyo nombre coincida con el `mavenArtifactId` (o el nombre del plugin) del addon en ejecución.
3. Si la versión inferida del asset es **mayor** que la del plugin cargado, descarga **solo ese JAR** a la carpeta **`updates/`** de Bukkit/Paper (la que devuelve `getUpdateFolder()`; por defecto suele ser `updates/` junto al `server.jar`).

Si no hay JAR directo pero sí un ZIP de monorepo (`monorepo-plugins.zip`), puede extraer **una** entrada bajo `monorepo-jars/` (compatibilidad con releases antiguos).

## Desactivar

- Propiedad JVM: `-Ddrakes.lab.autoupdate=false`
- Variable de entorno: `DRAKES_LAB_AUTOUPDATE=0` (o `false` / `off`)
- Propiedad del sistema: `-Ddrakes.lab.autoupdate.disable=true`

## Límite de API

Opcional: variable de entorno **`GITHUB_TOKEN`** (solo lectura de releases suele bastar) para subir el techo de peticiones a la API de GitHub.

## Despliegue manual en survival

Para probar un build **antes** de publicar release, o cuando el servidor **no** lee `updates/` como esperas:

- Copia el JAR sombreado del módulo al nombre que ya usa el servidor dentro de **`plugins/`** (sobrescribiendo el `.jar` anterior) y reinicia **o** usa el flujo de actualización que administres (Pterodactyl, scripts, etc.).
- La carpeta **`plugins/<NombrePlugin>/`** conserva datos (configs, YAML); no la borres al actualizar solo el jar.

El workflow **Release monorepo JARs** documentado en [github-maintenance.md](../github-maintenance.md) es lo que alimenta los assets que ve el updater.

## Inyección masiva en el código

Para añadir dependencia + llamada en addons nuevos: `python scripts/inject_drakes_autoupdate.py` (ver [scripts/README.md](../../scripts/README.md)).
