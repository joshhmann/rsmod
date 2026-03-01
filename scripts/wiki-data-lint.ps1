param(
    [string]$Root = "Z:\Projects\OSRS-PS-DEV",
    [switch]$Strict
)

$ErrorActionPreference = "Stop"

$wikiData = Join-Path $Root "wiki-data"
if (-not (Test-Path $wikiData)) {
    Write-Error "wiki-data directory not found: $wikiData"
    exit 2
}

$issues = New-Object System.Collections.Generic.List[string]
$warnings = New-Object System.Collections.Generic.List[string]

Write-Output "Lint target: $wikiData"

# 1) Malformed JSON scan
$jsonFiles = Get-ChildItem -Recurse -File $wikiData -Filter *.json
$badJson = New-Object System.Collections.Generic.List[string]
foreach ($f in $jsonFiles) {
    try {
        Get-Content $f.FullName -Raw | ConvertFrom-Json | Out-Null
    } catch {
        $badJson.Add($f.FullName)
    }
}
if ($badJson.Count -gt 0) {
    $issues.Add("Malformed JSON files: $($badJson.Count)")
}

# 2) Duplicate filename scan
$dups = Get-ChildItem -Recurse -File $wikiData | Group-Object Name | Where-Object { $_.Count -gt 1 }
if ($dups.Count -gt 0) {
    $issues.Add("Duplicate filenames detected: $($dups.Count) groups")
}

# 3) Root artifact directory scan
$artifactDirs = Get-ChildItem -Force $Root -Directory | Where-Object { $_.Name -like "Z*wiki-data*" }
if ($artifactDirs.Count -gt 0) {
    $issues.Add("Malformed artifact directories in repo root: $($artifactDirs.Count)")
}

# 4) Index drift checks
$indexPath = Join-Path $wikiData "MASTER-INDEX.json"
$allFiles = Get-ChildItem -Recurse -File $wikiData
$actualTotal = $allFiles.Count
$actualJson = ($allFiles | Where-Object Extension -eq ".json").Count
$actualMd = ($allFiles | Where-Object Extension -eq ".md").Count
$actualSize = ($allFiles | Measure-Object Length -Sum).Sum

if (-not (Test-Path $indexPath)) {
    $issues.Add("MASTER-INDEX.json missing")
} else {
    try {
        $idx = Get-Content $indexPath -Raw | ConvertFrom-Json
        if ($null -eq $idx.summary) {
            $issues.Add("MASTER-INDEX.json has no summary section")
        } else {
            if ([int]$idx.summary.total_files -ne $actualTotal) {
                $warnings.Add("Index drift: total_files index=$($idx.summary.total_files) actual=$actualTotal")
            }
            if ([int]$idx.summary.total_json -ne $actualJson) {
                $warnings.Add("Index drift: total_json index=$($idx.summary.total_json) actual=$actualJson")
            }
            if ([int]$idx.summary.total_md -ne $actualMd) {
                $warnings.Add("Index drift: total_md index=$($idx.summary.total_md) actual=$actualMd")
            }
            $sizeDelta = [math]::Abs([double]$idx.summary.total_size_bytes - [double]$actualSize)
            # MASTER-INDEX.json rewrites can cause tiny self-referential size drift; ignore small deltas.
            if ($sizeDelta -gt 1024) {
                $warnings.Add("Index drift: total_size_bytes index=$($idx.summary.total_size_bytes) actual=$actualSize delta=$sizeDelta")
            }
        }
    } catch {
        $issues.Add("MASTER-INDEX.json is malformed")
    }
}

Write-Output ""
Write-Output "Summary:"
Write-Output "  Files: $actualTotal (json=$actualJson, md=$actualMd)"
Write-Output "  Size bytes: $actualSize"
Write-Output "  Malformed json: $($badJson.Count)"
Write-Output "  Duplicate filename groups: $($dups.Count)"
Write-Output "  Artifact dirs: $($artifactDirs.Count)"

if ($badJson.Count -gt 0) {
    Write-Output ""
    Write-Output "Malformed JSON files:"
    $badJson | ForEach-Object { Write-Output "  - $_" }
}

if ($dups.Count -gt 0) {
    Write-Output ""
    Write-Output "Duplicate filename groups:"
    foreach ($g in $dups) {
        Write-Output "  - $($g.Name) ($($g.Count))"
        $g.Group | ForEach-Object { Write-Output "      $($_.FullName)" }
    }
}

if ($artifactDirs.Count -gt 0) {
    Write-Output ""
    Write-Output "Artifact directories:"
    $artifactDirs | ForEach-Object { Write-Output "  - $($_.FullName)" }
}

if ($warnings.Count -gt 0) {
    Write-Output ""
    Write-Output "Warnings:"
    $warnings | ForEach-Object { Write-Output "  - $_" }
}

Write-Output ""
if ($issues.Count -eq 0 -and (-not $Strict -or $warnings.Count -eq 0)) {
    Write-Output "wiki-data lint: PASS"
    exit 0
}

Write-Output "wiki-data lint: FAIL"
if ($issues.Count -gt 0) {
    Write-Output "Errors:"
    $issues | ForEach-Object { Write-Output "  - $_" }
}
if ($Strict -and $warnings.Count -gt 0) {
    Write-Output "Strict mode treats warnings as errors."
}
exit 1
