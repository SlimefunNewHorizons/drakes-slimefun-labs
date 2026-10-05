// Envia a la cola de revision de Modrinth un proyecto que ya tiene al menos una version.
//
// Modrinth no deja crear un proyecto directamente "en revision": rechaza con
// "Project submitted for review with no initial versions". El orden obligatorio es crear el
// borrador, subirle una version y solo entonces enviarlo. Este paso hace lo ultimo.
//
// Si MODRINTH_DRAFT=true se salta, para poder dejar algo en borrador a proposito.

const V2 = 'https://api.modrinth.com/v2';
const TOKEN = process.env.MODRINTH_TOKEN;
const ID = process.env.MODRINTH_PROJECT_ID;

if (!TOKEN || !ID) {
  console.error('Falta MODRINTH_TOKEN o el id del proyecto.');
  process.exit(0);
}
if (process.env.MODRINTH_DRAFT === 'true') {
  console.log('MODRINTH_DRAFT=true: se deja en borrador a proposito.');
  process.exit(0);
}

const cab = { Authorization: TOKEN, 'User-Agent': 'SlimefunNewHorizons/publicador' };

const actual = await fetch(`${V2}/project/${ID}`, { headers: cab });
if (actual.status !== 200) {
  console.error(`No se pudo leer el proyecto (HTTP ${actual.status}).`);
  process.exit(0);
}
const p = await actual.json();

if (!p.versions || p.versions.length === 0) {
  console.error('El proyecto no tiene ninguna version; no se envia a revision.');
  process.exit(0);
}
if (p.status !== 'draft') {
  console.log(`El proyecto ya esta en estado "${p.status}"; no hace falta enviarlo.`);
  process.exit(0);
}

const r = await fetch(`${V2}/project/${ID}`, {
  method: 'PATCH',
  headers: { ...cab, 'Content-Type': 'application/json' },
  body: JSON.stringify({ status: 'processing' }),
});

if (r.ok) {
  console.log(`Proyecto ${p.slug} enviado a revision de Modrinth (${p.versions.length} version(es)).`);
} else {
  console.error(`No se pudo enviar a revision (HTTP ${r.status}): ${(await r.text()).slice(0, 300)}`);
}
