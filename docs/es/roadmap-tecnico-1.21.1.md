# ROADMAP: Refactorización Masiva Slimefun (1.20.6 -> 1.21.11)
**Proyecto:** DrakesLab | **Branch:** `1.21-latin` | **Responsable técnico:** [DrakesCraft-Labs](https://github.com/SlimefunNewHorizons)

Este documento detalla la estrategia de ingeniería para portar más de 35 addons de Slimefun a Minecraft 1.21.11, enfocándose en la transición de NBT (PersistentDataContainer) a Data Components mediante una abstracción total y la consolidación del repositorio en un modelo Maven Multi-módulo.

---

## 1. Fase de Infraestructura: Shared Version Bridge

El pilar técnico será la eliminación de la dependencia directa de `dough` (`PersistentDataAPI`) y su reemplazo por un sistema de abstracción modular.

### Módulos del Bridge
- **`v-bridge-api`**: Contiene la interfaz `VersionBridge`. No tiene dependencias de Paper, solo Java base.
- **`v-bridge-1-20-6`**: Implementación para el servidor actual. Usa `PersistentDataContainer` estándar.
- **`v-bridge-1-21-11`**: Implementación nativa para 1.21. Usa los nuevos **Data Components** de Paper para acceso directo y eficiente, manejando la inmutabilidad de los stacks mediante `ItemStack.editMeta()`.

> [!IMPORTANT]
> **Abstracción Total**: Ningún addon deberá llamar a `item.getItemMeta()` para persistencia. Todas las llamadas serán `Bridge.get().setMetadata(item, key, value)`.

---

## 2. Fase de Consolidación: Maven Multi-módulo

Transformaremos el ecosistema actual (actualmente múltiples repos/carpetas independientes) en un solo proyecto gestionado por un **Parent POM**.

### Ventajas de la Consolidación
- **Dependency Management**: Centralizamos la versión de Slimefun (RC-37), Paper (1.20.6/1.21.1) y librerías de Sefiraat.
- **Compilación Unificada**: Un solo `mvn clean package` generará los 35+ JARs en la carpeta de despliegue.
- **Relocalización Dinámica**: Uso del `maven-shade-plugin` global para asegurar que todos los addons usen la misma versión del Bridge y relocalicen dependencias legacy de `dough`.

---

## 3. Protocolo de Migración: NBT a Data Components

La migración se realizará mediante un proceso semiautomatizado (Scripts de Refactorización):

### Protocolo de Reemplazo
1. **Detección**: Identificar patrones de `PersistentDataAPI.set(...)` y `meta.getPersistentDataContainer()`.
2. **Sustitución**: Reemplazar por llamadas estáticas al Bridge.
3. **Inyección en SlimefunItem**:
   - Modificar la clase base de los addons para recibir el Bridge en el constructor o inyectarlo mediante un `BridgeProvider` estático durante la inicialización del Plugin.
   - Asegurar que los `ItemStack` de recetas y drops se inicialicen con los metadatos correctos vía Bridge.

---

## 4. Foco Especial: Androides y Máquinas Industriales

Los Androides (`Androids`) y las máquinas de escaneo son los componentes más sensibles debido a su alta interacción con el inventario y el mundo.

### Desafíos en 1.21
- **Block Scanning**: La lógica de escaneo de bloques debe ser verificada para asegurar que los "filtros" almacenados en el inventario del Androide (que usan metadatos para distinguir ítems) sean leídos correctamente por el nuevo Bridge.
- **Hologramas y Displays**: Muchas máquinas industriales usan entidades para mostrar estados. En 1.21, los `Display Entities` son el estándar sugerido frente a los hologramas de ArmorStands. El Roadmap incluye la transición a `SefiLib-DisplayGroups`.
- **Inventarios Virtuales**: La migración de metadatos en ítems "dentro" de máquinas debe ser atómica para evitar pérdida de datos del jugador durante el upgrade de versión (Data Component Conversion).

---

## 5. Estrategia de Localización (Branch `1.21-latin`)

Implementaremos un motor de traducción sobre el branch de migración.

1. **Extracción**: Script encargado de recorrer las clases que extienden `SlimefunItem` y extraer los argumentos del constructor `ItemStack(Material, String Name, String... Lore)`.
2. **Mapping**: Almacenamiento en archivos JSON de referencia por addon.
3. **Traducción**: Aplicación de la base de datos de traducciones de SlimeChem al resto del ecosistema.
4. **Build-time Replacement**: Las traducciones se aplicarán durante el empaquetado o se inyectarán en caliente vía el Bridge en el método `onEnable`.

---

## Próximos Pasos Proyectados
1. Inicializar el **Parent POM** y el módulo `v-bridge`.
2. Migrar **SefiLib** y **InfinityLib** como las primeras dependencias del Parent.
3. Seleccionar **SlimeChem** como el proyecto "Piloto" para validar el Bridge en 1.21.

<!-- DRAKES-STATUS:BEGIN -->
> Estado de sincronizacion: **2026-04-24**.
> Baseline tecnico vigente: **Paper 1.21.1 + Java 21**.
> CI principal en `main`: **CI Monorepo 1.21** cubre reactor Maven completo + 5 Gradle.
> Nota: quedan pendientes smoke tests de runtime y estrategia de releases; no hay bloqueos de compilacion en el corte actual.
<!-- DRAKES-STATUS:END -->
