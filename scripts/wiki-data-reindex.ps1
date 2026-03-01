param(
    [string]$Root = "Z:\Projects\OSRS-PS-DEV"
)

$ErrorActionPreference = "Stop"

$wiki = Join-Path $Root "wiki-data"
if (-not (Test-Path $wiki)) {
    Write-Error "wiki-data directory not found: $wiki"
    exit 2
}

$files = Get-ChildItem -Recurse -File $wiki
$jsonFiles = $files | Where-Object Extension -eq ".json"
$mdFiles = $files | Where-Object Extension -eq ".md"

$bad = New-Object System.Collections.Generic.List[string]
foreach ($f in $jsonFiles) {
    try {
        Get-Content $f.FullName -Raw | ConvertFrom-Json | Out-Null
    } catch {
        $bad.Add($f.FullName)
    }
}

$dirStats = Get-ChildItem -Directory $wiki | ForEach-Object {
    $d = $_
    $df = Get-ChildItem -Recurse -File $d.FullName -ErrorAction SilentlyContinue
    [PSCustomObject]@{
        name = $d.Name
        files = $df.Count
        json = ($df | Where-Object Extension -eq ".json").Count
        md = ($df | Where-Object Extension -eq ".md").Count
        size_bytes = ($df | Measure-Object Length -Sum).Sum
    }
} | Sort-Object name

$index = [ordered]@{
    project = "RSMod v2 OSRS Rev 233 Data Extraction"
    generated_at = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ssK")
    root = "wiki-data/"
    summary = [ordered]@{
        total_files = $files.Count
        total_json = $jsonFiles.Count
        total_md = $mdFiles.Count
        total_size_bytes = ($files | Measure-Object Length -Sum).Sum
        total_size_mb = [math]::Round((($files | Measure-Object Length -Sum).Sum / 1MB), 3)
    }
    quality = [ordered]@{
        malformed_json_count = $bad.Count
        malformed_json_files = $bad
    }
    directories = $dirStats
    notes = @(
        "Run scripts/wiki-data-lint.ps1 after new scrapes.",
        "Keep canonical datasets under category directories (skills/, monsters/, mechanics/, etc.).",
        "Use _archive for historical snapshots and _quarantine for malformed payloads."
    )
}

$indexPath = Join-Path $wiki "MASTER-INDEX.json"
$index | ConvertTo-Json -Depth 8 | Set-Content -Path $indexPath -Encoding UTF8

Write-Output "Reindexed: $indexPath"
Write-Output "Files=$($files.Count) Json=$($jsonFiles.Count) Md=$($mdFiles.Count) BadJson=$($bad.Count)"

if ($bad.Count -gt 0) {
    exit 1
}
exit 0

