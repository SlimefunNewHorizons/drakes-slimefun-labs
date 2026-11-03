# Script to populate the GitHub Project with pending modules (v2)
$projectNumber = 1
$owner = "SlimefunNewHorizons"

# IDs for fields and options
$migrationStageField = "PVTSSF_lADOD6G3h84BVfiKzhQ7HQA"
$stagePorIntegrar = "5c9b9dde"

$statusField = "PVTSSF_lADOD6G3h84BVfiKzhQ6-Ho"
$statusTodo = "f75ad846"

$priorityField = "PVTSSF_lADOD6G3h84BVfiKzhQ7HQY"
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

foreach ($module in $pendingModules) {
    Write-Host "Adding $module..."
    # Create item
    $item = gh project item-create $projectNumber --owner $owner --title $module --format json | ConvertFrom-Json
    $itemId = $item.id
    $projectId = $item.project.id
    
    # Set Status
    gh project item-edit --id $itemId --field-id $statusField --project-id $projectId --single-select-option-id $statusTodo
    
    # Set Migration Stage
    gh project item-edit --id $itemId --field-id $migrationStageField --project-id $projectId --single-select-option-id $stagePorIntegrar

    # Set Priority (Default Media)
    gh project item-edit --id $itemId --field-id $priorityField --project-id $projectId --single-select-option-id $priorityMedia
}
