param(
    [switch]$DryRun,
    [switch]$KillAllJava
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$rsmodDir = if (Test-Path (Join-Path $root "rsmod\\gradlew.bat")) {
    Join-Path $root "rsmod"
} else {
    $root
}

function Invoke-GradleStop {
    param([string]$WorkDir)

    if (-not (Test-Path $WorkDir)) {
        return
    }

    $wrapper = Join-Path $WorkDir "gradlew.bat"
    if (-not (Test-Path $wrapper)) {
        return
    }

    if ($DryRun) {
        Write-Host "[dry-run] Would run: $wrapper --stop"
        return
    }

    Write-Host "Stopping Gradle daemons..."
    Push-Location $WorkDir
    try {
        & $wrapper --stop | Out-Host
    } finally {
        Pop-Location
    }
}

function Get-TargetJavaProcesses {
    param([bool]$KillEverything)

    $javaProcs = Get-CimInstance Win32_Process |
        Where-Object { $_.Name -match '^(java|javaw)\.exe$' }

    if ($KillEverything) {
        return $javaProcs
    }

    $pattern = '(?i)gradle|gradledaemon|kotlin[- ]?daemon|kotlincompiler|org\.gradle\.launcher\.daemon'
    return $javaProcs | Where-Object { $_.CommandLine -match $pattern }
}

Invoke-GradleStop -WorkDir $rsmodDir

$targets = @(Get-TargetJavaProcesses -KillEverything:$KillAllJava)

if ($targets.Count -eq 0) {
    Write-Host "No matching Java build processes found."
    exit 0
}

Write-Host ("Found {0} Java process(es) to stop." -f $targets.Count)

$stopped = 0
foreach ($proc in $targets) {
    $pid = [int]$proc.ProcessId
    $name = $proc.Name
    $cmd = $proc.CommandLine

    if ($DryRun) {
        Write-Host ("[dry-run] Would stop PID {0} ({1})" -f $pid, $name)
        if ($cmd) {
            Write-Host ("          {0}" -f $cmd)
        }
        continue
    }

    try {
        Stop-Process -Id $pid -Force -ErrorAction Stop
        $stopped++
        Write-Host ("Stopped PID {0} ({1})" -f $pid, $name)
    } catch {
        Write-Warning ("Failed to stop PID {0}: {1}" -f $pid, $_.Exception.Message)
    }
}

if (-not $DryRun) {
    $remaining = Get-Process -Name java,javaw -ErrorAction SilentlyContinue
    if ($remaining) {
        Write-Host "Remaining java/javaw processes:"
        $remaining | Select-Object Id, ProcessName | Format-Table -AutoSize
    } else {
        Write-Host "No java/javaw processes remain."
    }
    Write-Host ("Stopped {0} process(es)." -f $stopped)
}
