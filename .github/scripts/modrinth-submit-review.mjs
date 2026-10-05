const V2 = 'https://api.modrinth.com/v2';
const TOKEN = process.env.MODRINTH_TOKEN;
const PROJECT_ID = process.env.MODRINTH_PROJECT_ID;
const FORZAR_DRAFT = (process.env.MODRINTH_DRAFT || '').toLowerCase() === 'true';

if (!TOKEN || !PROJECT_ID) {
  console.log('No hay token o ID de proyecto; se omite pase a revision.');
  process.exit(0);
}

if (FORZAR_DRAFT) {
  console.log('MODRINTH_DRAFT=true; se conserva como borrador.');
  process.exit(0);
}

const cabeceras = {
  Authorization: TOKEN,
  'Content-Type': 'application/json',
  'User-Agent': 'SlimefunNewHorizons/publicador'
};

try {
  const rCheck = await fetch(`${V2}/project/${PROJECT_ID}`, { headers: cabeceras });
  if (rCheck.ok) {
    const proj = await rCheck.json();
    if (proj.status === 'draft') {
      const r = await fetch(`${V2}/project/${PROJECT_ID}`, {
        method: 'PATCH',
        headers: cabeceras,
        body: JSON.stringify({ is_draft: false })
      });
      if (r.ok) {
        console.log(`Proyecto ${PROJECT_ID} publicado / enviado a revision con exito.`);
      } else {
        console.log(`Respuesta al actualizar estado: HTTP ${r.status}`);
      }
    } else {
      console.log(`Proyecto ya en estado "${proj.status}".`);
    }
  }
} catch (e) {
  console.log('Aviso al enviar a revision:', e.message);
}
