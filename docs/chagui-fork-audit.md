# Auditoria del fork de Chagui

Fecha: 2026-05-20

Fuente auditada: `Chagui68/Slimefun6-Drakes-Fusion`, rama `Mixted`.
Base nueva: `main` en `SlimefunNewHorizons/drakes-slimefun-labs`.

## Decision de ramas

- `main` pasa a ser la base activa para nuevos cambios.
- `1.21-latin` queda deprecada como baseline historico y punto de comparacion.
- Los cambios integrados se trajeron sin los merge commits intermedios para dejar una historia mas legible.

## Integrado

- Eliminacion funcional del auto-updater legacy de Dough/Slimefun mediante stubs no operativos para `BlobBuildUpdater` y `GitHubBuildsUpdater`.
- Parches de persistencia y shutdown para `Networks_Better_Compatibility`.
- Ajustes de concurrencia y tickers de Networks para reducir corrupcion de inventarios.
- `Networks-Experimental` se incorpora al reactor como modulo auditado experimental, con nombre y metadata diferenciados de produccion.

## Normalizado

- `Networks_Better_Compatibility` produce `NetworksV6-Drake-v11-SNAPSHOT.jar`, alineado con el jar activo del servidor.
- `plugin.yml` de Networks apunta a la organizacion `DrakesCraft-Labs` y declara autores completos.
- `Networks-Experimental` queda como `NetworksExperimental-Drake`, para evitar colision de nombre/comando con el plugin de produccion durante auditorias.

## No integrado por ahora

- `Slimefun-Disc` del commit `498aad00`.
- Motivo: el modulo compila conceptualmente como addon separado, pero trae archivos `.nbs` nombrados como canciones comerciales. Antes de entrar a la organizacion requiere revision explicita de licencias o reemplazar esos assets por canciones propias/libres.

## Smoke test esperado

- Compilar `Networks_Better_Compatibility` con sus dependencias reactor.
- Compilar `Networks-Experimental` por separado.
- Validar que el jar de produccion no cambie el comando `/networks` ni el `main` class.
- No desplegar en `Y:\` sin backup local y ventana de prueba, porque el servidor esta en produccion.
