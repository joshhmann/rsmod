param(
    [Parameter(Mandatory = $true)]
    [string]$Agent,
    [Parameter(Mandatory = $true)]
    [string]$Message
)

$root = Split-Path -Parent $PSScriptRoot
$notesDir = Join-Path $root "docs/agent-notes"
$logPath = Join-Path $root "docs/agent-pings.md"
$agentFile = Join-Path $notesDir ("{0}.md" -f $Agent)

if (-not (Test-Path $notesDir)) {
    New-Item -ItemType Directory -Path $notesDir | Out-Null
}

$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$entry = "[$timestamp] [ping] $Message"

Add-Content -Path $agentFile -Value $entry
Add-Content -Path $logPath -Value ("- {0} -> {1}" -f $Agent, $entry)

Write-Host "Ping logged for $Agent in $agentFile"
