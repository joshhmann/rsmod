# Telemetry Quickstart

This workspace has two opt-in telemetry streams:

- `rsmod.telemetry.rsprot-updates`
  - logs `RspCycle` packet-generation timing (player/npc packet build + flush)
  - includes per-world breakdown in each interval summary
- `rsmod.telemetry.zone-updates`
  - logs `PlayerZoneUpdateProcessor` timing (new-visible + visible-zone update paths)

## 1) Enable telemetry for a server run (PowerShell)

From `Z:\Projects\OSRS-PS-DEV\rsmod`:

```powershell
$env:JAVA_TOOL_OPTIONS = "-Drsmod.telemetry.rsprot-updates=true -Drsmod.telemetry.rsprot-updates.interval-ms=5000 -Drsmod.telemetry.zone-updates=true -Drsmod.telemetry.zone-updates.interval-ms=5000"
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :server:app:run --console=plain --args='--skip-type-verification --allow-type-verification-failures'"
```

## 2) Turn telemetry off

```powershell
Remove-Item Env:JAVA_TOOL_OPTIONS -ErrorAction SilentlyContinue
```

## 3) Typical telemetry lines

- `rsprot-update-telemetry ... perWorld=[w-1:c=...]`
- `ZoneUpdateTelemetry[rsmod.telemetry.zone-updates=true]: ...`

## 4) Build-only verification

```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :api:net:build :api:game-process:build --console=plain"
```
