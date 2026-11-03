# Script to SYNC the GitHub Project (v2)
$projectNumber = 1
$owner = "SlimefunNewHorizons"
$projectId = "PVT_kwDOD6G3h84BVfiK"

# Field IDs
$statusField = "PVTSSF_lADOD6G3h84BVfiKzhQ6-Ho"
$migrationStageField = "PVTSSF_lADOD6G3h84BVfiKzhQ7HQA"

# Option IDs
$statusTodo = "f75ad846"
$statusDone = "98236657"
$stagePorIntegrar = "5c9b9dde"
$stageEnReactor = "d0b72a9a"

$readyModules = @(
    "dough-core", "Slimefun4-src", "ColoredEnderChests", "DyedBackpacks", "DynaTech",
    "EcoPower", "ElectricSpawners", "ExoticGarden", "ExtraGear", "ExtraHeads",
    "ExtraUtils", "FluffyMachines", "GlobalWarming", "HardcoreSlimefun", "HotbarPets",
    "InfinityExpansion", "luckyblocks-sf", "MobCapturer", "PrivateStorage", "SFCalc",
    "SFMobDrops", "SimpleUtils", "SlimeChem", "SlimefunOreChunks", "SlimyRepair",
    "SlimyTreeTaps", "SoulJars", "SoundMuffler", "SefiLib", "InfinityLib",
    "Cultivation_Updated", "LiteXpansion", "Networks_Better_Compatibility",
    "SlimeTinker", "SMG", "Supreme", "TranscEndence", "AlchimiaVitae",
    "CrystamaeHistoria", "DankTech2", "DyeBench", "Element-Manipulation",
    "ExtraTools", "FlowerPower", "FN-FAL-s-Amplifications", "FoxyMachines",
    "HeadLimiter", "GeneticChickengineering-Reborn", "Liquid", "Magic-8-Ball",
    "MapJammers", "MiniBlocks", "MissileWarfare", "PotionExpansion",
    "RykenSlimeCustomizer-EN", "SfChunkInfo", "Simple-Storage",
    "SlimeCustomizer", "UltimateGenerators2", "VillagerUtil"
)

$rawJson = gh project item-list $projectNumber --owner $owner --format json --limit 100
$items = $rawJson | ConvertFrom-Json

foreach ($item in $items) {
    $title = $item.title
    if ([string]::IsNullOrWhiteSpace($title)) {
        continue
    }
    $id = $item.id
    
    if ($readyModules -contains $title) {
        Write-Host "Updating $title to DONE/EN REACTOR..."
        gh project item-edit --id "$id" --project-id "$projectId" --field-id "$statusField" --single-select-option-id "$statusDone"
        gh project item-edit --id "$id" --project-id "$projectId" --field-id "$migrationStageField" --single-select-option-id "$stageEnReactor"
    } else {
        Write-Host "Syncing $title as TODO/POR INTEGRAR..."
        gh project item-edit --id "$id" --project-id "$projectId" --field-id "$statusField" --single-select-option-id "$statusTodo"
        gh project item-edit --id "$id" --project-id "$projectId" --field-id "$migrationStageField" --single-select-option-id "$stagePorIntegrar"
    }
}
