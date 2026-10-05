# Canonical addon repositories and migration inventory

Updated: 2026-08-30

## Operational rule

This monorepo is a legacy source archive. No migrated addon version, build,
release, or production fix is canonical here. Work only in the standalone
repository under the DrakesCraft-Labs organization.

The retained directories under `sources/` preserve history and are not deleted
because they remain useful for archaeology and regression comparison. The root
Maven and Gradle projects intentionally include zero addon modules.

## Standalone repositories registered so far

| Archived module | Canonical repository |
| --- | --- |
| dough-core | dough-core |
| drakes-labs-autoupdate | drakes-labs-autoupdate |
| DyedBackpacks | DyedBackpacks-drake |
| EcoPower | EcoPower-drake |
| ExoticGarden | ExoticGarden-drake |
| ExtraUtils | ExtraUtils-drake |
| FluffyMachines | FluffyMachines-drake |
| InfinityExpansion | InfinityExpansion-drake |
| SFCalc | SFCalc-drake |
| SFMobDrops | SFMobDrops-drake |
| SoulJars | SoulJars-drake |
| SoundMuffler | SoundMuffler-drake |
| SMG | SMG-drake |
| TranscEndence | TranscEndence-drake |
| Slimefun-Disc | Slimefun-Disc-drake |
| Element-Manipulation | Element-Manipulation-drake |
| ExtraTools | ExtraTools-drake |
| FN-FAL-s-Amplifications | FN-FAL-s-Amplifications-drake |
| Liquid | Liquid-drake |
| RykenSlimeCustomizer-EN | RykenSlimeCustomizer-EN-drake |
| Coronalis | Coronalis-drake |
| AdvancedTech | AdvancedTech-drake |
| Better-Nuclear-Generator | Better-Nuclear-Generator-drake |
| Geyser-Slimefun-Heads | Geyser-Slimefun-Heads-drake |
| SlimeHUD | SlimeHUD-drake |
| SmallSpace | SmallSpace-drake |
| WorldEditSlimefun | WorldEditSlimefun-drake |

All repository names in this table resolve below:

`https://github.com/SlimefunNewHorizons/<repository>`

## Remaining archive inventory

There are **52 source modules still awaiting deliberate extraction**. They are
recorded as `pending` in [migration/addons.json](../migration/addons.json),
instead of being presented as already migrated. A pending module must receive:

1. its own repository and local versioning;
2. a build that works without the legacy parent;
3. CI and a concise README;
4. source-history import or a documented clean baseline; and
5. a validated artifact before it can replace a production build.

Until then, pending modules are archive-only. This is intentional: it prevents
the old reactor from silently applying a shared version or publishing a JAR.

## Repositories repaired in this pass

These repositories were previously listed as canonical but contained only
documentation and automation. Their source trees have now been migrated and
pushed:

- MobCapturer-drake
- SimpleUtils-drake
- LiteXpansion-drake
- FlowerPower-drake
- SlimeFrame-drake

## Previously extracted repositories

ChestTerminal-drake, ColoredEnderChests-drake, DynaTech-drake,
ElectricSpawners-drake, ExtraGear-drake, ExtraHeads-drake, GlobalWarming,
HardcoreSlimefun, HotbarPets-drake, KinematicCore-drake,
luckyblocks-sf-drake, PrivateStorage, SensibleToolbox-drake,
SlimeChem-drake, SlimefunOreChunks, SlimyRepair-drake and
SlimyTreeTaps-drake remain canonical in their standalone repositories.

## Versioning

Each standalone repository declares and advances its own project version. The
legacy root version `2026.08` identifies only the archive marker and must never
be inherited by an addon. Deployment artifacts must be traceable to a commit in
the corresponding standalone repository.
