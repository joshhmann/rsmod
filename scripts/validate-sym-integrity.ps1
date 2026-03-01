param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [switch]$FailOnIssues
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$SymbolsRoot = if (Test-Path (Join-Path $RepoRoot "rsmod\\.data\\symbols")) {
    Join-Path $RepoRoot "rsmod\.data\symbols"
} else {
    Join-Path $RepoRoot ".data\symbols"
}
$LocalRoot = Join-Path $SymbolsRoot ".local"

if (-not (Test-Path $SymbolsRoot)) {
    Write-Error "Symbols directory not found: $SymbolsRoot"
    exit 2
}

$issues = [System.Collections.Generic.List[object]]::new()

function Add-Issue {
    param(
        [string]$File,
        [int]$Line,
        [string]$Kind,
        [string]$Message
    )
    $issues.Add([pscustomobject]@{
        file = $File
        line = $Line
        kind = $Kind
        message = $Message
    })
}

# Domain -> Map<ID, Name>
$rootMaps = @{}

# 1. Load and validate root symbols
$rootFiles = Get-ChildItem -Path $SymbolsRoot -File -Filter *.sym
foreach ($file in $rootFiles) {
    $domain = $file.BaseName
    $idMap = @{} # ID -> Name
    $nameMap = @{} # Name -> ID
    
    $lines = @(Get-Content -Path $file.FullName)
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i] # Keep original whitespace except for the split
        $lineNo = $i + 1
        if ([string]::IsNullOrWhiteSpace($line) -or $line.Trim().StartsWith("#")) { continue }

        $parts = $line -split "`t", 2
        if ($parts.Count -lt 2) {
            Add-Issue -File $file.FullName -Line $lineNo -Kind "malformed-line" -Message "Line is not tab-separated: '$($line.Trim())'"
            continue
        }

        $id = $parts[0].Trim()
        $name = $parts[1].Trim()

        if ($idMap.ContainsKey($id)) {
            Add-Issue -File $file.FullName -Line $lineNo -Kind "duplicate-id" -Message "ID '$id' already defined in this file (previous name: '$($idMap[$id])')"
        } else {
            $idMap[$id] = $name
        }

        if ($nameMap.ContainsKey($name)) {
            Add-Issue -File $file.FullName -Line $lineNo -Kind "duplicate-name" -Message "Name '$name' already defined in this file (previous ID: '$($nameMap[$name])')"
        } else {
            $nameMap[$name] = $id
        }
    }
    $rootMaps[$domain] = @{ ids = $idMap; names = $nameMap }
}

# 2. Load and validate .local overlays
if (Test-Path $LocalRoot) {
    $localFiles = Get-ChildItem -Path $LocalRoot -File -Filter *.sym
    foreach ($file in $localFiles) {
        $domain = $file.BaseName
        $rootData = if ($rootMaps.ContainsKey($domain)) { $rootMaps[$domain] } else { $null }
        
        $localIdMap = @{}
        $localNameMap = @{}

        $lines = @(Get-Content -Path $file.FullName)
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            $lineNo = $i + 1
            if ([string]::IsNullOrWhiteSpace($line) -or $line.Trim().StartsWith("#")) { continue }

            $parts = $line -split "`t", 2
            if ($parts.Count -lt 2) {
                Add-Issue -File $file.FullName -Line $lineNo -Kind "malformed-line" -Message "Line is not tab-separated: '$($line.Trim())'"
                continue
            }

            $id = $parts[0].Trim()
            $name = $parts[1].Trim()

            # Check for internal duplicates in .local file
            if ($localIdMap.ContainsKey($id)) {
                Add-Issue -File $file.FullName -Line $lineNo -Kind "local-duplicate-id" -Message "ID '$id' already defined in this .local file."
            } else {
                $localIdMap[$id] = $name
            }

            if ($localNameMap.ContainsKey($name)) {
                Add-Issue -File $file.FullName -Line $lineNo -Kind "local-duplicate-name" -Message "Name '$name' already defined in this .local file."
            } else {
                $localNameMap[$name] = $id
            }

            # Check against root (Remap Prevention)
            if ($null -ne $rootData) {
                if ($rootData.ids.ContainsKey($id)) {
                    $rootName = $rootData.ids[$id]
                    Add-Issue -File $file.FullName -Line $lineNo -Kind "forbidden-id-remap" -Message "ID '$id' is already in root $domain.sym as '$rootName'. .local must not remap existing IDs."
                }
                if ($rootData.names.ContainsKey($name)) {
                    $rootId = $rootData.names[$name]
                    Add-Issue -File $file.FullName -Line $lineNo -Kind "forbidden-name-remap" -Message "Name '$name' is already in root $domain.sym with ID '$rootId'. .local must not remap existing names."
                }
            }
        }
    }
}

Write-Host "`nSymbol Integrity Validation"
Write-Host "Symbols Root: $SymbolsRoot"
Write-Host "Domains checked: $($rootMaps.Keys.Count)"
Write-Host "Issues found: $($issues.Count)"

if ($issues.Count -gt 0) {
    $issues | ForEach-Object {
        Write-Host ("[{0}] {1}:{2} - {3}" -f $_.kind, $_.file, $_.line, $_.message) -ForegroundColor Red
    }
} else {
    Write-Host "OK: No symbol integrity issues found." -ForegroundColor Green
}

if ($FailOnIssues -and $issues.Count -gt 0) {
    exit 1
}

exit 0
