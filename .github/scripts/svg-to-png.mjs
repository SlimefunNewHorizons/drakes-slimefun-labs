// Convierte un icono o banner .svg en un .png para Modrinth (100% Vectorial, No IA).
// Modrinth rechaza SVG en iconos y exige PNG/JPEG con titulos y metadata en galeria.

import { existsSync, readFileSync, writeFileSync } from 'fs';

const [, , svgPath, outPng, w = '512', h = '512'] = process.argv;

if (!svgPath || !outPng) {
  console.error('Uso: node svg-to-png.mjs <entrada.svg> <salida.png> [ancho] [alto]');
  process.exit(1);
}

const width = Number(w);
const height = Number(h);

if (!existsSync(svgPath)) {
  console.error(`El archivo SVG de entrada no existe: ${svgPath}`);
  process.exit(0);
}

const svg = readFileSync(svgPath, 'utf8');

try {
  const { Resvg } = await import('@resvg/resvg-js');
  const resvg = new Resvg(svg, {
    fitTo: { mode: 'width', value: width },
  });
  const pngData = resvg.render();
  const pngBuffer = pngData.asPng();
  writeFileSync(outPng, pngBuffer);
  console.log(`✔ PNG generado con resvg-js: ${outPng} (${width}px)`);
  process.exit(0);
} catch (err) {
  try {
    const puppeteer = (await import('puppeteer')).default;
    const browser = await puppeteer.launch({ args: ['--no-sandbox', '--disable-setuid-sandbox'] });
    try {
      const page = await browser.newPage();
      await page.setViewport({ width, height, deviceScaleFactor: 1 });
      await page.setContent(
        `<!doctype html><html><head><style>html,body{margin:0;padding:0;overflow:hidden;background:transparent;}</style></head><body>${svg}</body></html>`,
        { waitUntil: 'networkidle0' }
      );
      await page.screenshot({ path: outPng, omitBackground: true });
      console.log(`✔ PNG generado con puppeteer: ${outPng}`);
    } finally {
      await browser.close();
    }
  } catch (err2) {
    console.error('No se pudo renderizar SVG a PNG:', err.message, err2.message);
  }
}
