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

async function buscarProyecto(idOslug) {
  if (!idOslug) return null;
  const r = await pedir(`${V2}/project/${encodeURIComponent(idOslug)}`);
  return r.status === 200 ? await r.json() : null;
}

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

// Trasladar a la organizacion si procede
if (organizacion && proyecto.organization !== organizacion.id) {
  const r = await pedir(`${V3}/organization/${organizacion.id}/projects`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ project_id: proyecto.id }),
  });
  if (r.ok) {
    console.log(`Proyecto trasladado a la organizacion ${organizacion.slug}.`);
  }
}

if (process.env.GITHUB_OUTPUT) {
  appendFileSync(process.env.GITHUB_OUTPUT, `project_id=${proyecto.id}\n`);
  appendFileSync(process.env.GITHUB_OUTPUT, `project_slug=${proyecto.slug}\n`);
}
console.log(`Proyecto en uso: ${proyecto.slug} (${proyecto.id})`);

// --- Icono del proyecto (PNG obligatorio para Modrinth) -------------------------------
try {
  let rutaIcono = null;
  if (existsSync('docs/icon.png')) rutaIcono = 'docs/icon.png';
  else if (existsSync('icon.png')) rutaIcono = 'icon.png';

  if (rutaIcono) {
    const png = readFileSync(rutaIcono);
    const r = await fetch(`${V2}/project/${proyecto.id}/icon?ext=png`, {
      method: 'PATCH',
      headers: { ...cabeceras, 'Content-Type': 'image/png' },
      body: png,
    });
    if (r.ok) {
      console.log(`Icono PNG subido con exito (${rutaIcono}).`);
    } else {
      console.error(`No se pudo subir el icono (HTTP ${r.status}): ${(await r.text()).slice(0, 200)}`);
    }
  }
} catch (e) {
  console.error('Fallo al subir el icono:', e.message);
}

// --- Galeria / Banner (GIF o PNG) ------------------------------------------------------
try {
  let rutaBanner = null;
  let extBanner = 'png';
  if (existsSync('.modrinth/banner.gif')) {
    rutaBanner = '.modrinth/banner.gif';
    extBanner = 'gif';
  } else if (existsSync('docs/banner.png')) {
    rutaBanner = 'docs/banner.png';
    extBanner = 'png';
  }

  if (rutaBanner && (!proyecto.gallery || proyecto.gallery.length === 0)) {
    const bannerBytes = readFileSync(rutaBanner);
    const r = await fetch(`${V2}/project/${proyecto.id}/gallery?ext=${extBanner}&featured=true&title=Banner`, {
      method: 'POST',
      headers: { ...cabeceras, 'Content-Type': extBanner === 'gif' ? 'image/gif' : 'image/png' },
      body: bannerBytes,
    });
    if (r.ok) {
      console.log(`Banner subido a la galeria (${rutaBanner}).`);
    }
  }
} catch (e) {
  console.error('Fallo al subir banner a la galeria:', e.message);
}

// --- Descripcion larga (README saneado) -------------------------------------------------
try {
  if (existsSync('README.md')) {
    let cuerpo = readFileSync('README.md', 'utf8');
    if (cuerpo.trim() && cuerpo !== proyecto.body) {
      const r = await fetch(`${V2}/project/${proyecto.id}`, {
        method: 'PATCH',
        headers: { ...cabeceras, 'Content-Type': 'application/json' },
        body: JSON.stringify({ body: cuerpo }),
      });
      console.log(r.ok ? 'Descripcion sincronizada con el README.' : `Fallo al sincronizar descripcion (HTTP ${r.status}).`);
    }
  }
} catch (e) {
  console.error('Fallo al sincronizar la descripcion:', e.message);
}
