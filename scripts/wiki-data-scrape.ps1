param(
    [string]$Root = "Z:\Projects\OSRS-PS-DEV",
    [ValidateSet("f2p-monsters", "monsters", "all-skills", "skills")]
    [string]$Mode = "f2p-monsters",
    [string[]]$Monsters = @(),
    [string[]]$Skills = @(),
    [double]$Delay = 0.5,
    [switch]$AllowLegacySources,
    [switch]$NoPostCheck
)

$ErrorActionPreference = "Stop"

$scraperDir = Join-Path $Root "OSRSWikiScraper"
$wikiData = Join-Path $Root "wiki-data"
if (-not (Test-Path $scraperDir)) {
    Write-Error "OSRSWikiScraper directory not found: $scraperDir"
    exit 2
}
if (-not (Test-Path $wikiData)) {
    Write-Error "wiki-data directory not found: $wikiData"
    exit 2
}

function Resolve-Python {
    if (Get-Command python -ErrorAction SilentlyContinue) { return "python" }
    if (Get-Command py -ErrorAction SilentlyContinue) { return "py -3" }
    throw "Python not found in PATH."
}

function Invoke-Python([string]$CommandLine) {
    Write-Output ">> $CommandLine"
    $proc = Start-Process -FilePath "powershell" -ArgumentList "-NoProfile", "-Command", $CommandLine -PassThru -Wait -NoNewWindow
    if ($proc.ExitCode -ne 0) {
        throw "Command failed with exit code $($proc.ExitCode): $CommandLine"
    }
}

$python = Resolve-Python
$scraperDirEscaped = $scraperDir.Replace("'", "''")
$rev233Exporter = Join-Path $scraperDir "export_for_rsmod_rev233.py"
if (-not (Test-Path $rev233Exporter)) {
    Write-Error "Rev233 exporter not found: $rev233Exporter"
    exit 2
}

Write-Output "Revision lock: rev233 historical sources only."
if ($AllowLegacySources) {
    Write-Warning "Legacy (non-rev233-guaranteed) sources are enabled by explicit override."
}

switch ($Mode) {
    "f2p-monsters" {
        $cmd = "Set-Location '$scraperDirEscaped'; $python export_for_rsmod_rev233.py --f2p-monsters --output-dir ..\wiki-data\monsters\ --delay $Delay"
        Invoke-Python $cmd
    }
    "monsters" {
        if ($Monsters.Count -eq 0) {
            throw "Mode=monsters requires -Monsters <name1,name2,...>"
        }
        $monsterArgs = ($Monsters | ForEach-Object { '"' + $_ + '"' }) -join " "
        $cmd = "Set-Location '$scraperDirEscaped'; $python export_for_rsmod_rev233.py --monsters $monsterArgs --output-dir ..\wiki-data\monsters\ --delay $Delay"
        Invoke-Python $cmd
    }
    "all-skills" {
        if (-not $AllowLegacySources) {
            throw "Mode=all-skills is blocked in rev233-only mode. Pass -AllowLegacySources to use legacy scraper sources."
        }
        $cmd = "Set-Location '$scraperDirEscaped'; $python export_for_rsmod.py --all-skills --output-dir ..\wiki-data\skills\"
        Invoke-Python $cmd
    }
    "skills" {
        if ($Skills.Count -eq 0) {
            throw "Mode=skills requires -Skills <skill1,skill2,...>"
        }
        if (-not $AllowLegacySources) {
            throw "Mode=skills is blocked in rev233-only mode. Pass -AllowLegacySources to use legacy scraper sources."
        }
        foreach ($skill in $Skills) {
            $safeName = ($skill.ToLower() -replace "[^a-z0-9\-_]", "-")
            $outPath = "..\wiki-data\skills\$safeName.json"
            $cmd = "Set-Location '$scraperDirEscaped'; $python scraper_v2.py -s `"$skill`" --delay $Delay -o `"$outPath`""
            Invoke-Python $cmd
        }
    }
}

if (-not $NoPostCheck) {
    $reindex = Join-Path $Root "scripts\wiki-data-reindex.ps1"
    $lint = Join-Path $Root "scripts\wiki-data-lint.ps1"

    if (Test-Path $reindex) {
        & $reindex -Root $Root
        if ($LASTEXITCODE -ne 0) {
            throw "Reindex failed."
        }
    } else {
        Write-Warning "Reindex script not found: $reindex"
    }

    if (Test-Path $lint) {
        & $lint -Root $Root
        if ($LASTEXITCODE -ne 0) {
            throw "Lint failed."
        }
    } else {
        Write-Warning "Lint script not found: $lint"
    }
}

Write-Output "Scrape workflow complete."
exit 0
