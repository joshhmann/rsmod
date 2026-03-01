param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$Module,
    [switch]$FailOnIssues
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Resolve-ScanRoot {
    param(
        [string]$RepoRoot,
        [string]$Module
    )

    $rsmodRoot = if (Test-Path (Join-Path $RepoRoot "rsmod\\gradlew.bat")) {
        Join-Path $RepoRoot "rsmod"
    } else {
        $RepoRoot
    }

    if ([string]::IsNullOrWhiteSpace($Module)) {
        return $rsmodRoot
    }

    $normalized = $Module.Trim().Trim(':') -replace ':', '\\'
    if ($normalized.StartsWith("rsmod\\")) {
        $scanRoot = Join-Path $RepoRoot $normalized
    } else {
        $scanRoot = Join-Path $rsmodRoot $normalized
    }

    if (-not (Test-Path $scanRoot)) {
        throw "Module path does not exist: $scanRoot (from -Module '$Module')"
    }

    return $scanRoot
}

function Load-SymbolNames {
    param([string]$SymbolsRoot)

    $set = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)

    if (-not (Test-Path $SymbolsRoot)) {
        throw "Symbols directory not found: $SymbolsRoot"
    }

    $symFiles = Get-ChildItem -Path $SymbolsRoot -File -Filter *.sym
    foreach ($symFile in $symFiles) {
        foreach ($line in Get-Content -Path $symFile.FullName) {
            if ([string]::IsNullOrWhiteSpace($line)) {
                continue
            }

            $parts = $line -split "`t"
            if ($parts.Count -lt 2) {
                continue
            }

            $name = $parts[1].Trim()
            if (-not [string]::IsNullOrWhiteSpace($name)) {
                [void]$set.Add($name)
            }
        }
    }

    return $set
}

function Add-Issue {
    param(
        [System.Collections.Generic.List[object]]$Issues,
        [string]$File,
        [int]$Line,
        [string]$Kind,
        [string]$Symbol,
        [string]$Message
    )

    $Issues.Add([pscustomobject]@{
        file = $File
        line = $Line
        kind = $Kind
        symbol = $Symbol
        message = $Message
    })
}

$scanRoot = Resolve-ScanRoot -RepoRoot $RepoRoot -Module $Module
$symbolsRoot = if (Test-Path (Join-Path $RepoRoot "rsmod\\.data\\symbols")) {
    Join-Path $RepoRoot "rsmod\.data\symbols"
} else {
    Join-Path $RepoRoot ".data\symbols"
}
$knownSymbols = Load-SymbolNames -SymbolsRoot $symbolsRoot

$kotlinFiles = Get-ChildItem -Path $scanRoot -Recurse -Filter *.kt -File |
    Where-Object {
        $_.FullName -notmatch "\\build\\" -and
        $_.FullName -notmatch "\\.gradle\\" -and
        $_.FullName -notmatch "\\.tmp\\"
    }

$issues = [System.Collections.Generic.List[object]]::new()
$findRegex = [regex]'find\(\s*"([^"]+)"'

foreach ($file in $kotlinFiles) {
    $lines = Get-Content -Path $file.FullName

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        $lineNo = $i + 1

        foreach ($match in $findRegex.Matches($line)) {
            $symbol = $match.Groups[1].Value.Trim()

            if ([string]::IsNullOrWhiteSpace($symbol)) {
                continue
            }

            if ($symbol.Contains('$')) {
                Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "dynamic-find-symbol" -Symbol $symbol -Message "Dynamic find() symbol; cannot statically verify."
                continue
            }

            if (-not $knownSymbols.Contains($symbol)) {
                Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "unknown-find-symbol" -Symbol $symbol -Message "find() symbol not present in rev233 .sym tables."
            }
        }

        if ($line -match 'find\([^)]*,-1\)') {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "find-minus-one" -Symbol "" -Message "find(..., -1) detected; verify symbol mapping and fallback behavior."
        }
    }
}

Write-Host "Symbol Verification"
Write-Host "RepoRoot: $RepoRoot"
Write-Host "ScanRoot: $scanRoot"
Write-Host "Kotlin files scanned: $($kotlinFiles.Count)"
Write-Host "Known symbols loaded: $($knownSymbols.Count)"
Write-Host "Issues found: $($issues.Count)"

if ($issues.Count -gt 0) {
    $issues |
        Sort-Object file, line |
        ForEach-Object {
            Write-Host ("[{0}] {1}:{2} symbol='{3}' - {4}" -f $_.kind, $_.file, $_.line, $_.symbol, $_.message)
        }

    $filesWithIssues = $issues | Group-Object file | Sort-Object Count -Descending
    Write-Host "`nFiles with potential symbol issues:"
    foreach ($entry in $filesWithIssues) {
        Write-Host ("- {0} ({1})" -f $entry.Name, $entry.Count)
    }
}

if ($FailOnIssues -and $issues.Count -gt 0) {
    exit 1
}

exit 0
