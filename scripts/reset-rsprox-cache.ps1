[CmdletBinding(SupportsShouldProcess = $true)]
param(
    [switch]$FullReset,
    [switch]$ResetTargets
)

$ErrorActionPreference = "Stop"

$rsproxRoot = Join-Path $env:USERPROFILE ".rsprox"
if (-not (Test-Path $rsproxRoot)) {
    Write-Host "No .rsprox directory found at: $rsproxRoot"
    exit 0
}

function Stop-RsProxProcesses {
    $targets = Get-CimInstance Win32_Process | Where-Object {
        $_.Name -in @("java.exe", "node.exe") -and
            $_.CommandLine -match "rsprox|runelite|injected-client"
    }

    if (-not $targets) {
        Write-Host "No rsprox/runelite java/node processes found."
        return
    }

    foreach ($proc in $targets) {
        if ($PSCmdlet.ShouldProcess("PID $($proc.ProcessId)", "Stop process")) {
            Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
        }
    }
    Write-Host "Stopped $($targets.Count) rsprox/runelite process(es)."
}

function Remove-PathContents([string]$path) {
    if (-not (Test-Path $path)) {
        return
    }
    $items = Get-ChildItem -Force $path -ErrorAction SilentlyContinue
    foreach ($item in $items) {
        if ($PSCmdlet.ShouldProcess($item.FullName, "Remove")) {
            Remove-Item $item.FullName -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
}

Stop-RsProxProcesses

$pathsToClear = @(
    (Join-Path $rsproxRoot "caches"),
    (Join-Path $rsproxRoot "binary")
)

if ($FullReset) {
    $pathsToClear += @(
        (Join-Path $rsproxRoot "logs"),
        (Join-Path $rsproxRoot "sockets")
    )

    # RuneLite keeps its own JS5 caches under ~/.runelite. If these drift across revisions,
    # the injected client can crash with "Mismatch in overlaid cache archive hash".
    # Only clear on explicit FullReset since this is larger/more disruptive.
    $pathsToClear += @(
        (Join-Path $env:USERPROFILE ".runelite\\cache"),
        (Join-Path $env:USERPROFILE ".runelite\\jagexcache"),
        (Join-Path $env:USERPROFILE ".runelite\\repository2"),
        (Join-Path $env:USERPROFILE ".runelite\\logs"),
        # RSProx patches the injected client to use a different home dir to avoid clobbering
        # your main RuneLite profile; clear that too.
        (Join-Path $env:USERPROFILE ".rlcustom\\cache"),
        (Join-Path $env:USERPROFILE ".rlcustom\\jagexcache"),
        (Join-Path $env:USERPROFILE ".rlcustom\\repository2"),
        (Join-Path $env:USERPROFILE ".rlcustom\\logs")
    )
}

foreach ($path in $pathsToClear) {
    Remove-PathContents $path
}

if ($ResetTargets) {
    $targetsFile = Join-Path $rsproxRoot "proxy-targets.yaml"
    if (Test-Path $targetsFile -and $PSCmdlet.ShouldProcess($targetsFile, "Remove")) {
        Remove-Item $targetsFile -Force -ErrorAction SilentlyContinue
        Write-Host "Removed proxy targets file."
    }
}

Write-Host "rsprox cache reset complete."
Write-Host "Restart order: RSMod server -> rsprox -> client."
