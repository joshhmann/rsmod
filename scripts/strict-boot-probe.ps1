[CmdletBinding()]
param(
    [int]$TimeoutSeconds = 120
)

$ErrorActionPreference = "Stop"

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$rsmodRoot = if (Test-Path (Join-Path $projectRoot "rsmod\\gradlew.bat")) {
    Join-Path $projectRoot "rsmod"
} else {
    $projectRoot
}
$logPath = Join-Path $projectRoot "tmp_strict_probe.out.log"

if (Test-Path $logPath) {
    Remove-Item -Force $logPath -ErrorAction SilentlyContinue
}

Write-Host "[strict-probe] Starting strict server boot probe (timeout=${TimeoutSeconds}s)..."
$null = New-Item -ItemType File -Path $logPath -Force

$job = Start-Job -ScriptBlock {
    param($root, $log)
    Set-Location $root
    # Persist output to disk so timeouts still leave useful traces.
    & ".\gradlew.bat" :server:app:run --console=plain --args="--strict-type-verification" *>&1 |
        Out-File -FilePath $log -Append -Encoding utf8
} -ArgumentList $rsmodRoot, $logPath

$successPatterns = @(
    "\\[MainGameProcess\\] World is live",
    "Bound to ports: 43594"
)

$success = $false
$timedOut = $false
$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
while ($true) {
    # Stop early if the server reached a stable "live enough" point.
    if (Test-Path $logPath) {
        if (Select-String -Path $logPath -Pattern $successPatterns -Quiet) {
            $success = $true
            break
        }
        # Fast-fail if Gradle already reported a hard failure.
        if (Select-String -Path $logPath -Pattern "BUILD FAILED|\\bFAILURE:\\b" -Quiet) {
            break
        }
    }

    $state = (Get-Job -Id $job.Id -ErrorAction SilentlyContinue).State
    if ($state -ne "Running") {
        break
    }

    if ((Get-Date) -ge $deadline) {
        $timedOut = $true
        break
    }

    Start-Sleep -Seconds 1
}

if ($timedOut) {
    Write-Warning "[strict-probe] Timed out after ${TimeoutSeconds}s. Stopping probe job."
}

if ($success -or $timedOut) {
    Stop-Job -Id $job.Id -ErrorAction SilentlyContinue
    Remove-Job -Id $job.Id -Force -ErrorAction SilentlyContinue

    if (Test-Path $logPath) {
        Write-Host "[strict-probe] Tail of log: $logPath"
        Get-Content -Path $logPath -Tail 200
    }
} else {
    Receive-Job -Id $job.Id
    Remove-Job -Id $job.Id -Force -ErrorAction SilentlyContinue

    if (Test-Path $logPath) {
        Write-Host "[strict-probe] Tail of log: $logPath"
        Get-Content -Path $logPath -Tail 200
    }
}

Write-Host "[strict-probe] Running cleanup..."
Set-Location $rsmodRoot
& ".\gradlew.bat" --stop | Out-Null

$targets =
    Get-CimInstance Win32_Process |
    Where-Object {
        $_.Name -eq "java.exe" -and
            ($_.CommandLine -match "org\.rsmod|:server:app:run|gradle-wrapper\.jar")
    }

foreach ($proc in $targets) {
    Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
}

Write-Host "[strict-probe] Cleanup complete."

if ($success) {
    exit 0
}

if ($timedOut) {
    exit 1
}
