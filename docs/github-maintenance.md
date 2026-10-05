# Mantenimiento en GitHub (Actions, PRs, seguridad)

Guía operativa para mantener el repositorio **drakes-slimefun-labs** ordenado en GitHub. Los permisos requieren rol de mantenedor en la org o el repo.

## Ramas largas (solo dos en el remoto)

Política vigente: en **`SlimefunNewHorizons/drakes-slimefun-labs`** deben quedar **únicamente** las ramas **`main`** (estable Paper 1.21.x) y **`26.X-ToTheStars`** (experimento 26.x). Cualquier `feat/*`, `feature/*` u otra rama de trabajo debe **integrarse por PR y borrarse** al terminar.

**Dependabot** crea ramas `dependabot/...` mientras haya PRs abiertos; cerrar o fusionar el PR (con borrado de rama) las elimina. Si reaparecen, es el ciclo normal de bumps.

**No mezclar** `1.21-latin` ↔ `26.X-ToTheStars` (merge/rebase cruzado); es política explícita del laboratorio.

## Historial largo de workflow runs

GitHub **no** ofrece un botón “borrar todo el historial” de ejecuciones. Opciones reales:

1. **Retención automática** (recomendado): en el repo, *Settings → Actions → General → Artifact and log retention* (y políticas de la org). Reduce ruido sin scripts.
2. **Borrar ejecuciones con la CLI** (por lotes): con token que incluya `workflow`:
  ```bash
   gh run list --repo SlimefunNewHorizons/drakes-slimefun-labs --limit 200 --json databaseId -q '.[].databaseId' | xargs -n1 gh run delete --repo SlimefunNewHorizons/drakes-slimefun-labs
  ```
   En Windows PowerShell puedes iterar con un bucle corto sobre `gh run list`. Respeta los límites de tasa de la API; pausa entre lotes si hay cientos de entradas.
3. **Archivar el repo** o duplicar historial: medida extrema; no suele ser necesaria solo por “limpieza visual”.

No confundas borrar *runs* con borrar *logs de artifact*; son ajustes distintos en Settings.

## Pull requests

1. `gh pr list --repo SlimefunNewHorizons/drakes-slimefun-labs --state open`
2. Para cada PR: revisar CI, conflicto con `main`, y si el cambio sigue la política del monorepo.
3. **Merge** cuando CI esté verde y el alcance sea claro; **cerrar** con comentario si está obsoleta o duplica trabajo ya integrado.

**Ramas divergentes:** no abras ni fusiones PRs que intenten unir `**1.21-latin`** con `**26.X-ToTheStars`** (ni al revés). Es política explícita del repo; ver README raíz (“Línea roja”) y la misma norma documentada para el equipo en la raíz del repositorio (reglas de ramas del laboratorio).

Los merges los debe hacer alguien con contexto del porte; esta guía no sustituye revisión humana.

## Bloquear merge entre `1.21-latin` y `26.X-ToTheStars` (GitHub / `gh`)

GitHub **no** incluye una regla nativa del tipo “si la base es A y la cabeza es B, bloquear”. **Rulesets** y **branch protection** exigen reviews, firmas, checks genéricos, etc., pero **no** pueden declarar solo esa pareja de ramas.

Lo habitual es:

1. **Workflow de Actions** que falle cuando `github.base_ref` y `github.head_ref` sean exactamente esa pareja prohibida.
2. Marcar ese job como **status check obligatorio** en las ramas afectadas (ruleset o branch protection).

En este repo el workflow es `[.github/workflows/policy-no-cross-line-merge.yml](../.github/workflows/policy-no-cross-line-merge.yml)`. El job se llama `**cross-merge-guard`** (nombre que verás en el cuadro de checks del PR).

### Activar el bloqueo (recomendado: Rulesets en la UI)

1. *Settings → Rules → Rulesets → New branch ruleset*.
2. **Name:** por ejemplo `require-cross-merge-guard`.
3. **Target branches:** incluir `1.21-latin` y `26.X-ToTheStars` (dos entradas o patrón que las cubra).
4. Activar **Require status checks to pass** (o el equivalente en rulesets) y elegir el check `**cross-merge-guard`** (tras abrir un PR de prueba contra esas bases para que GitHub lo descubra, si no aparece en la lista).
5. Opcional: modo **Evaluate** primero para validar sin bloquear aún.

### Qué puede hacer `gh` aquí

- **Solo lectura / comprobación** (oficial en CLI): `gh ruleset list`, `gh ruleset view`, `gh ruleset check <rama> -R org/repo`.
- **Crear o actualizar rulesets** no trae subcomando dedicado; se hace con la **API REST** que `gh` puede llamar:
  ```bash
  gh api --method POST repos/SlimefunNewHorizons/drakes-slimefun-labs/rulesets --input ruleset.json
  ```
  El cuerpo `ruleset.json` debe seguir [Create a repository ruleset](https://docs.github.com/en/rest/repos/rules?apiVersion=2022-11-28#create-a-repository-ruleset) (campos `name`, `target`, `enforcement`, `conditions.ref_name.include`, `rules` con `type: "required_status_checks"` y el `context` exacto del check). Hace falta token con permisos de administración sobre el repo.
- **Branch protection clásica** (alternativa): `PUT /repos/{owner}/{repo}/branches/{branch}/protection` con `required_status_checks`; también vía `gh api` con JSON bien formado. Suele ser más verboso que un ruleset en la UI.

### Limitación del workflow

Solo bloquea cuando la **cabeza del PR** es exactamente `26.X-ToTheStars` o `1.21-latin`. Un PR desde una rama intermedia (`sync/bad`) con contenido mezclado **no** queda cubierto; ahí hacen falta revisión humana y convención de equipo.

## Dependabot y vulnerabilidades

### Dónde mirar

- **Dependabot alerts**: *Security → Dependabot alerts* en el repo (o API REST).
- **Dependabot version updates**: `.github/dependabot.yml` (Actions + Maven raíz). Política actual: **intervalo mensual**; **máx. 1 PR** para Actions y **máx. 1 PR** para Maven (en serie: o el grupo de producción o el de desarrollo, no dos a la vez). Los grupos Maven separan **producción** (solo *patch* y *minor*) y **desarrollo** (*patch*/*minor*/*major* en plugins de build). **Ignorados** para bump automático: `io.papermc.paper:paper-api`, `net.md-5:bungeecord-chat`, y *major* de `spring-*` seleccionados. **`dough-core`** declara explícitamente `commons-lang-drake-patched` para que PRs de deps no rompan `org.apache.commons.lang` al cambiar transitivos de Paper. Tras tocar el YAML, **cierra** PRs viejos o **`@dependabot recreate`**.
- **GitHub “Dependabot security updates”** y el grafo **Dependency review** pueden mostrar el mismo CVE en varios manifiestos; no es obligatorio tener *cero* filas duplicadas si el estado global es **fixed** o **dismissed** con motivo documentado.
- **Code scanning**: solo aparece si hay análisis configurado (CodeQL u otro); si la API devuelve 404, no hay alertas de código que listar.

### Revisión rápida por CLI (mantenedor)

Alertas Dependabot abiertas:

```bash
gh api "repos/SlimefunNewHorizons/drakes-slimefun-labs/dependabot/alerts?state=open&per_page=100" --jq 'length'
```

Resumen de alertas de dependencias (incluye histórico *fixed* / *dismissed*):

```bash
gh api graphql -f query='query($o:String!,$n:String!){repository(owner:$o,name:$n){vulnerabilityAlerts(first:50){nodes{state securityAdvisory{summary severity}}}}}' -f o=DrakesCraft-Labs -f n=drakes-slimefun-labs
```

### Cómo mitiga este monorepo

En el `**pom.xml` raíz**, `dependencyManagement` fija versiones seguras de uso frecuente (commons-lang3, Guava, Spring context, Plexus Utils, Commons IO, Jackson, Log4j2, etc.). Los submódulos heredan el BOM al resolver transitivos.

En `**build.gradle.kts`** del reactor Gradle, `resolutionStrategy` fuerza las mismas líneas críticas (commons-io, commons-lang3, jackson-core, log4j-api / log4j-core) para no diverger del árbol Maven.

**Commons Lang 2.x**: el código vulnerable upstream no recibe parche en Maven Central; aquí se usa `**commons-lang-drake-patched`** (reemplazo interno). Las alertas sobre `commons-lang:commons-lang` suelen **cerrarse como “dismissed”** o equivalente con la nota de que el runtime usa el artefacto parcheado.

Tras subir versiones: `mvn -B -DskipTests package -fae` (o el subconjunto que toque) y fusionar PRs de Dependabot con revisión humana.

### Alertas “del bot” (PRs / comentarios)

- **Dependabot**: PRs con etiquetas `dependencies`, `java`, `maven` o `github-actions`; revisar CI y conflictos con `main`.
- **Otras integraciones en PRs**: mismo criterio que un PR humano: CI verde y alcance claro.
- **Issues**: si un bot abre un issue de seguridad, enlazar el GHSA y el commit o PR que lo mitiga.

## CI Maven: fundación vs reactor completo

El workflow **CI Monorepo 1.21** (`.github/workflows/ci-monorepo-121.yml`) define dos comportamientos distintos:

1. **Maven · fundación** ejecuta `mvn clean compile` solo sobre el stack base (Dough, Slimefun core, SefiLib, InfinityLib, commons-lang parcheado). **No** se ejecuta el *shade* de Slimefun, por tanto **no existen** paquetes `com.github.drakescraft_labs.slimefun4.libraries.dough.*` en el classpath. **SefiLib** e **InfinityLib** deben importar `**dev.drake.dough.protection.*`**. El script `scripts/fix_dough_compilation_imports.py` **excluye** esos árboles para no sustituir imports por los tipos sombreados.
2. **Maven · reactor completo** ejecuta `mvn package -Dmaven.test.skip=true` sobre todo el reactor. Ahí Slimefun **sí** empaqueta con shade antes de los addons que dependen de él, así que los addons pueden usar `**com.github.drakescraft_labs.slimefun4.libraries.dough.protection.*`** (ver script anterior y comentarios en `docs/README.md`). Se usa `maven.test.skip=true` y no solo `skipTests` porque algunos módulos del laboratorio arrastran tests legacy que hoy no forman parte del objetivo del port y rompen `testCompile`.

El workflow **Release monorepo JARs** también usa `package -Dmaven.test.skip=true`, coherente con el reactor completo.

## GraalVM (RykenSlimeCustomizer) y CI

- En Maven, `**org.graalvm.js:js`** (tipo POM “enterprise”) arrastra `**truffle-enterprise`**, problemático en Maven público y en GitHub Actions. Debe usarse `**org.graalvm.js:js-community**` (mismo rango de versión, p. ej. `24.1.2`). El script `**scripts/fix_graalvm_js_community_poms.py**` (`--audit`, `--dry-run`) y `**scripts/ci_hygiene_fixes.py**` documentan y automatizan el barrido; `**scripts/manager.py repair**` incluye la misma regla en transformaciones POM.
- En **Libby** (carga en runtime), no se debe pedir el artefacto `**truffle-enterprise`**; el stack community usa `**truffle-runtime`** junto con `js-language`, `truffle-api`, etc.

## Que todo quede “en verde”

1. Rama objetivo: `1.21-latin` (o la que defina el equipo).
2. Comprobar el último run de **CI Monorepo 1.21** y del smoke manual si aplica.
3. Si un job falla por infraestructura (caché, red), *Re-run jobs*; si es código, arreglar y empujar.

## Projects (tablero org)

Ver [PROJECT_BOARD_SYNC.md](PROJECT_BOARD_SYNC.md). La CLI necesita scopes `read:project` y `project` tras `gh auth refresh`.

## Release de todos los JAR del monorepo

El workflow **Release monorepo JARs** (`release-monorepo-jars.yml`) puede lanzarse **a mano** (*Actions → Release monorepo JARs → Run workflow*) o **automáticamente al hacer push de un tag `v*`**. Ejecuta `mvn -B -Dmaven.test.skip=true package` en el reactor, recopila un jar por módulo con `scripts/release/collect_monorepo_jars.py` y crea un **GitHub Release** adjuntando **cada `.jar` por separado** (más `manifest.json`) como assets del mismo tag. Así el auto-updater del laboratorio solo descarga el jar del addon que toca, sin un ZIP monolítico.

Además, el workflow **CI Monorepo 1.21** ahora sube un artifact `monorepo-jars-<sha>` en cada **push exitoso a `1.21-latin`** (no en PRs), usando la misma recolección de jars. Eso permite descargar builds rápidos sin tener que abrir un release formal.

- **Tag**: obligatorio y único (por ejemplo `v11-plugins-2026-04-25`). Si el tag ya existe, el paso de release fallará hasta que elijas otro.
- **Draft / Prerelease**: útil la primera vez para revisar notas y adjuntos antes de publicar.
- Los módulos sin `target/*.jar` (no compilados) aparecen en `manifest.json` con la lista `missing_modules`; conviene revisar ese archivo si faltan plugins en el release.

Tras publicar, el despliegue típico en survival sigue siendo manual (por ejemplo en **[DrakesCraft](https://drakescraft.cl)**); con muchos assets en un solo release puedes descargar solo los jars que necesites o automatizar con el updater por nombre de artefacto.

**Despliegue directo en `plugins/`:** si tu panel o convención no usa la carpeta `updates/`, puedes copiar el `.jar` publicado (o el compilado en `sources/.../target/`) **sobre el archivo existente** en `plugins/`, manteniendo la carpeta `plugins/<NombreDelPlugin>/` de datos. Guía breve: [wiki/runtime-drakes-autoupdate.md](wiki/runtime-drakes-autoupdate.md).

## Fallos de Actions ya cubiertos en `1.21-latin` (referencia)


| Síntoma                                                                                                         | Causa                                                                                                     | Mitigación en repo                                                           |
| --------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| **Maven · fundación** + `package ...libraries.dough.protection does not exist` en **SefiLib** / **InfinityLib** | Esos módulos compilan con `compile` sin shade de Slimefun; los imports deben ser `**dev.drake.dough.*`**. | Imports corregidos; `fix_dough_compilation_imports.py` excluye esos árboles. |
| **Maven · reactor completo** + mismos errores en addons                                                         | El job usaba solo `**mvn compile`**; sin shade no existen tipos `libraries.dough.*`.                      | Workflow pasa a `**mvn package -DskipTests -fae`**.                          |
| **Release monorepo JARs** + `truffle-enterprise` / JAR corrupto                                                 | `org.graalvm.js:js` enterprise en POM y carga Libby de **truffle-enterprise**.                            | POM `**js-community`**; Libby sin enterprise.                                |
| **Dependabot** propone **Spring 7** desde 6.2.x                                                                 | Salto **semver-major** no deseado en el port 1.21.                                                        | `dependabot.yml` **ignore** en `spring-context` para major.                  |


Las acciones **release** y **smoke** usan `**actions/upload-artifact@v7`** y el release `**softprops/action-gh-release@v3`**, alineadas con los PRs de Dependabot que pasaron CI.
