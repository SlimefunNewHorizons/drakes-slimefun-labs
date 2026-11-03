# Final Script to populate the GitHub Project
$projectNumber = 1
$owner = "SlimefunNewHorizons"
$projectId = "PVT_kwDOD6G3h84BVfiK"

# Field IDs
$statusField = "PVTSSF_lADOD6G3h84BVfiKzhQ6-Ho"
$migrationStageField = "PVTSSF_lADOD6G3h84BVfiKzhQ7HQA"
$priorityField = "PVTSSF_lADOD6G3h84BVfiKzhQ7HQY"

# Option IDs
$statusTodo = "f75ad846"
$statusDone = "98236657"
$stagePorIntegrar = "5c9b9dde"
$stageEnReactor = "d0b72a9a"
$priorityAlta = "684e7c0a"
$priorityMedia = "291f643c"

$pendingModules = @(
    "Cultivation", "EMC2", "Galactifun", "Networks", "AdvancedTech",
    "Better-Nuclear-Generator", "Bump", "CompressionCraft", "CustomItemGenerators",
    "EMCTech", "FastMachines", "Gastronomicon", "Geyser-Slimefun-Heads",
    "MoreResearches", "Netheopoiesis", "Quaptics", "RelicsOfCthonia",
    "SaneCrafting", "SfBetterChests", "SlimeFrame", "SlimefunAdvancements",
    "SlimefunTranslation", "SlimefunWarfare", "SlimeHUD", "SmallSpace",
    "SpiritsUnchained", "VillagerTrade", "Wildernether", "WorldEditSlimefun"
)

$activeModules = @(
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

# Function to add and setup item
function Add-Module($name, $status, $stage, $priority) {
    Write-Host "Processing $name..."
    $item = gh project item-create $projectNumber --owner $owner --title $name --format json | ConvertFrom-Json
    $itemId = $item.id
    
    gh project item-edit --id $itemId --project-id $projectId --field-id $statusField --single-select-option-id $status
    gh project item-edit --id $itemId --project-id $projectId --field-id $migrationStageField --single-select-option-id $stage
    gh project item-edit --id $itemId --project-id $projectId --field-id $priorityField --single-select-option-id $priority
}

# Process Pending
foreach ($m in $pendingModules) {
    Add-Module $m $statusTodo $stagePorIntegrar $priorityMedia
}

# Process Active
foreach ($m in $activeModules) {
    Add-Module $m $statusDone $stageEnReactor $priorityAlta
}
