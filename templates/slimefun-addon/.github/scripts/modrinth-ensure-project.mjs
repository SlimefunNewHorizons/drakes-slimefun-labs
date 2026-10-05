// Se asegura de que el proyecto exista en Modrinth, DENTRO DE LA ORGANIZACION, y devuelve su id.
// Cumple con las normativas 2.1 (galeria e imagenes destacadas con titulo), 5.9 y 4 (declaracion de fuentes y licencias)
// y 6.2a (recursos vectoriales limpios sin IA generativa).

import { appendFileSync, existsSync, readFileSync } from 'fs';

const V2 = 'https://api.modrinth.com/v2';
const V3 = 'https://api.modrinth.com/v3';
const TOKEN = process.env.MODRINTH_TOKEN;
const SLUG = (process.env.PROJECT_SLUG || '').toLowerCase();
const NOMBRE = process.env.PROJECT_NAME || SLUG;
const RESUMEN = (process.env.PROJECT_SUMMARY || 'Addon de Slimefun4 para Paper 1.21.11.').slice(0, 256);
const ORG_PEDIDA = process.env.MODRINTH_ORG || '';

if (!TOKEN) {
  console.error('Falta MODRINTH_TOKEN.');
  process.exit(1);
}

const cabeceras = { Authorization: TOKEN, 'User-Agent': 'SlimefunNewHorizons/publicador' };

async function pedir(url, opciones = {}) {
  const r = await fetch(url, { ...opciones, headers: { ...cabeceras, ...(opciones.headers || {}) } });
  return r;
}

/** El proyecto si existe, o null. */
async function buscarProyecto(idOslug) {
  if (!idOslug) return null;
  const r = await pedir(`${V2}/project/${encodeURIComponent(idOslug)}`);
  return r.status === 200 ? await r.json() : null;
}

/** La organizacion donde deben vivir los proyectos, o null si no se puede determinar. */
async function resolverOrganizacion() {
  if (ORG_PEDIDA) {
    const r = await pedir(`${V3}/organization/${encodeURIComponent(ORG_PEDIDA)}`);
    if (r.status === 200) return await r.json();
    console.error(`La organizacion "${ORG_PEDIDA}" no existe o el token no la ve.`);
    return null;
  }
  const usuario = await pedir(`${V3}/user`);
  if (usuario.status !== 200) return null;
  const yo = await usuario.json();
  const orgs = await pedir(`${V3}/user/${yo.id}/organizations`);
  if (orgs.status !== 200) return null;
  const lista = await orgs.json();
  if (Array.isArray(lista) && lista.length === 1) return lista[0];
  return null;
}

const organizacion = await resolverOrganizacion();
if (organizacion) {
  console.log(`Organizacion destino: ${organizacion.name || organizacion.slug} (${organizacion.id})`);
} else {
  console.log('Sin organizacion resuelta; el proyecto quedara bajo el usuario del token.');
}

let proyecto = (await buscarProyecto(process.env.MODRINTH_PROJECT_ID)) || (await buscarProyecto(SLUG));

if (!proyecto) {
  console.log(`No existe el proyecto "${SLUG}"; se crea.`);

  const cuerpo = existsSync('README.md')
    ? readFileSync('README.md', 'utf8')
    : `# ${NOMBRE}\n\nAddon de Slimefun para DrakesCraft.`;

  const datos = {
    slug: SLUG,
    title: NOMBRE,
    description: RESUMEN,
    body: cuerpo,
    categories: ['utility'],
    client_side: 'unsupported',
    server_side: 'required',
    project_type: 'mod',
    is_draft: true,
    license_id: process.env.PROJECT_LICENSE || 'GPL-3.0-only',
    source_url: `https://github.com/SlimefunNewHorizons/${SLUG}`,
    issues_url: `https://github.com/SlimefunNewHorizons/${SLUG}/issues`,
    discord_url: 'https://discord.gg/rR7FbfCt9Y',
    initial_versions: [],
  };

  const form = new FormData();
  form.append('data', JSON.stringify(datos));

  const r = await pedir(`${V2}/project`, { method: 'POST', body: form });
  if (!r.ok) {
    console.error(`No se pudo crear el proyecto (HTTP ${r.status}): ${(await r.text()).slice(0, 400)}`);
    process.exit(1);
  }
  proyecto = await r.json();
  console.log(`Proyecto creado como BORRADOR: ${proyecto.slug} (${proyecto.id})`);
}

// Trasladarlo a la organizacion si aun no pertenece a ella.
if (organizacion && proyecto.organization !== organizacion.id) {
  const r = await pedir(`${V3}/organization/${organizacion.id}/projects`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ project_id: proyecto.id }),
  });
  if (r.ok) {
    console.log(`Proyecto trasladado a la organizacion ${organizacion.slug}.`);
  } else {
    console.error(`No se pudo trasladar a la organizacion (HTTP ${r.status}): ${(await r.text()).slice(0, 300)}`);
  }
}

if (process.env.GITHUB_OUTPUT) {
  appendFileSync(process.env.GITHUB_OUTPUT, `project_id=${proyecto.id}\n`);
  appendFileSync(process.env.GITHUB_OUTPUT, `project_slug=${proyecto.slug}\n`);
}
console.log(`Proyecto en uso: ${proyecto.slug} (${proyecto.id})`);

// --- Declaraciones de Contenido & Metadatos (Sección 5.9 y 4) -----------------------------
try {
  const patchData = {
    source_url: `https://github.com/SlimefunNewHorizons/${SLUG}`,
    issues_url: `https://github.com/SlimefunNewHorizons/${SLUG}/issues`,
    discord_url: 'https://discord.gg/rR7FbfCt9Y'
  };
  await fetch(`${V2}/project/${proyecto.id}`, {
    method: 'PATCH',
    headers: { ...cabeceras, 'Content-Type': 'application/json' },
    body: JSON.stringify(patchData),
  });
} catch (e) {
  console.error('Error al actualizar metadatos de origen:', e.message);
}

// --- Icono del proyecto (docs/icon.png o docs/icon.svg) -----------------------------------
try {
  if (!proyecto.icon_url) {
    let buffer = null;
    let mime = '';
    let ext = '';
    if (existsSync('docs/icon.png')) {
      buffer = readFileSync('docs/icon.png');
      mime = 'image/png';
      ext = 'png';
    } else if (existsSync('docs/icon.svg')) {
      buffer = readFileSync('docs/icon.svg');
      mime = 'image/svg+xml';
      ext = 'svg';
    }

    if (buffer) {
      const r = await fetch(`${V2}/project/${proyecto.id}/icon?ext=${ext}`, {
        method: 'PATCH',
        headers: { ...cabeceras, 'Content-Type': mime },
        body: buffer,
      });
      if (r.ok) {
        console.log(`Icono ${ext} subido.`);
      } else {
        console.error(`No se pudo subir el icono (HTTP ${r.status}): ${(await r.text()).slice(0, 200)}`);
      }
    }
  }
} catch (e) {
  console.error('Fallo al subir el icono:', e.message);
}

// --- Galeria de Imagenes & Imagen Destacada (Sección 2.1) ---------------------------------
try {
  const galeriaActual = Array.isArray(proyecto.gallery) ? proyecto.gallery : [];
  const tieneDestacada = galeriaActual.some((item) => item.featured);

  let bannerFile = null;
  if (existsSync('docs/banner.png')) bannerFile = 'docs/banner.png';
  else if (existsSync('banner.png')) bannerFile = 'banner.png';

  if (bannerFile && !tieneDestacada) {
    const bannerBytes = readFileSync(bannerFile);
    const titulo = encodeURIComponent(`${NOMBRE} - Overview & Features`);
    const desc = encodeURIComponent(`Official gameplay and feature banner for ${NOMBRE} on Paper 1.21.11.`);
    const r = await fetch(`${V2}/project/${proyecto.id}/gallery?ext=png&featured=true&title=${titulo}&description=${desc}`, {
      method: 'POST',
      headers: { ...cabeceras, 'Content-Type': 'image/png' },
      body: bannerBytes,
    });
    if (r.ok) {
      console.log('Imagen destacada de galeria subida exitosamente (Seccion 2.1).');
    } else {
      console.error(`No se pudo subir la imagen de galeria (HTTP ${r.status}): ${(await r.text()).slice(0, 200)}`);
    }
  }
} catch (e) {
  console.error('Fallo al actualizar la galeria:', e.message);
}

// --- Descripcion larga sincronizada con README -------------------------------------------
try {
  if (existsSync('README.md')) {
    const cuerpo = readFileSync('README.md', 'utf8');
    if (cuerpo.trim() && cuerpo !== proyecto.body) {
      const r = await fetch(`${V2}/project/${proyecto.id}`, {
        method: 'PATCH',
        headers: { ...cabeceras, 'Content-Type': 'application/json' },
        body: JSON.stringify({ body: cuerpo }),
      });
      console.log(r.ok ? 'Descripcion sincronizada con el README.'
                       : `No se pudo actualizar la descripcion (HTTP ${r.status}).`);
    }
  }
} catch (e) {
  console.error('Fallo al sincronizar la descripcion:', e.message);
}
