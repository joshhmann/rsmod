param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [switch]$FailOnIssues
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Invoke-SymbolRefCanon {
    param(
        [string]$RepoRoot,
        [switch]$FailOnIssues
    )

    $tool = Join-Path $RepoRoot "tools\\symbol_ref_canon.py"
    if (-not (Test-Path $tool)) {
        Write-Host "[symbol-ref-canon] Skipped: tools\\symbol_ref_canon.py not found"
        return
    }

    Write-Host "[symbol-ref-canon] Checking for NEW unknown symbol-name refs vs baseline..."
    & python $tool check --repo-root $RepoRoot
    $exit = $LASTEXITCODE
    if ($exit -ne 0) {
        if ($FailOnIssues) { exit $exit }
        Write-Host "[symbol-ref-canon] WARNING: Unknown refs detected (see output)."
    }
}

function Get-BaseRefNames {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        return @{}
    }

    $names = [System.Collections.Generic.HashSet[string]]::new()
    foreach ($line in Get-Content -Path $Path) {
        if ($line -match "val\s+([A-Za-z0-9_]+)\s*:") {
            [void]$names.Add($Matches[1])
        }
    }
    return $names
}

function Add-Issue {
    param(
        [System.Collections.Generic.List[object]]$Issues,
        [string]$File,
        [int]$Line,
        [string]$Kind,
        [string]$Message
    )

    $Issues.Add([pscustomobject]@{
            file = $File
            line = $Line
            kind = $Kind
            message = $Message
        })
}

$rsmodRoot = if (Test-Path (Join-Path $RepoRoot "rsmod\\gradlew.bat")) {
    Join-Path $RepoRoot "rsmod"
} else {
    $RepoRoot
}
$baseObjFile = Join-Path $rsmodRoot "api\config\src\main\kotlin\org\rsmod\api\config\refs\BaseObjs.kt"
$baseNpcFile = Join-Path $rsmodRoot "api\config\src\main\kotlin\org\rsmod\api\config\refs\BaseNpcs.kt"
$baseLocFile = Join-Path $rsmodRoot "api\config\src\main\kotlin\org\rsmod\api\config\refs\BaseLocs.kt"

$baseObjs = Get-BaseRefNames -Path $baseObjFile
$baseNpcs = Get-BaseRefNames -Path $baseNpcFile
$baseLocs = Get-BaseRefNames -Path $baseLocFile

$issues = [System.Collections.Generic.List[object]]::new()
$kotlinFiles =
Get-ChildItem -Path $rsmodRoot -Recurse -Filter *.kt -File |
    Where-Object {
        $_.FullName -notmatch "\\build\\" -and
            $_.FullName -notmatch "\\.tmp\\"
    }

# Symbol-name drift gate (packCache strict blocker class)
Invoke-SymbolRefCanon -RepoRoot $RepoRoot -FailOnIssues:$FailOnIssues

foreach ($file in $kotlinFiles) {
    $content = Get-Content -Path $file.FullName
    $usesBaseObjs = $false
    $usesBaseNpcs = $false
    $usesBaseLocs = $false
    $inObject = $false
    $braceLevel = 0
    $ignoreLevel = -1

    foreach ($line in $content) {
        if ($line -match "BaseObjs\.objs") { $usesBaseObjs = $true }
        if ($line -match "BaseNpcs\.npcs") { $usesBaseNpcs = $true }
        if ($line -match "BaseLocs\.locs") { $usesBaseLocs = $true }
    }

    for ($i = 0; $i -lt $content.Count; $i++) {
        $line = $content[$i]
        $lineNo = $i + 1

        # Track brace levels
        if ($line -match "\{") { 
            $braceLevel++ 
        }
        
        if ($line -match "\bobject\s+[A-Za-z0-9_]+") { 
            $inObject = $true 
        }

        # Start ignoring vars if we enter a function or a common scoping block
        if ($line -match "\b(private|internal|public|protected)?\s*fun\b" -or 
            $line -match "\b(apply|run|let|also|with|repeat|init)\s*\{" -or
            $line -match "\bconstructor\b") {
            if ($ignoreLevel -eq -1) {
                $ignoreLevel = $braceLevel
            }
        }

        if ($inObject -and $ignoreLevel -eq -1 -and $line -match "^\s*var\s+[A-Za-z0-9_]+") {
            # Only flag if we are in an object and NOT inside a function/block
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "global-mutable-state" -Message "BANNED: Do not use mutable 'var' properties inside singleton objects. Store state on the Player entity."
        }

        if ($line -match "\}") { 
            if ($braceLevel -eq $ignoreLevel) {
                $ignoreLevel = -1
            }
            $braceLevel-- 
            if ($braceLevel -eq 0) { 
                $inObject = $false 
            }
        }

        if ($line -match "\bThread\.sleep\(") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "illegal-thread-sleep" -Message "BANNED: Do not use Thread.sleep() as it blocks the engine. Use RSMod suspend functions like delay(ticks)."
        }

        if ($line -match "\bprintln\(") {
            # Only warn if it's in content or api, allow it in tests or build scripts
            if ($file.FullName -match "rsmod\\content" -or $file.FullName -match "rsmod\\api") {
                Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "illegal-println" -Message "BANNED: Do not use raw println(). Use RSMod's logger (private val logger = InlineLogger())."
            }
        }

        if ($line -match "\binvAdd\(" -and $line -notmatch "\.success" -and $line -notmatch "val " -and $line -notmatch "return" -and $line -notmatch "fun\s+([A-Za-z0-9_]+\.)?invAdd" -and $line -notmatch "import ") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "unsafe-invadd" -Message "WARNING: invAdd() fails silently if full. Always check .success or use invAddOrDrop()."
        }

        if ($line -match "find\([^)]*,-1\)") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "find-minus-one" -Message "Avoid find(..., -1); verify symbol and use explicit ref."
        }

        if ($line -match "\bobjs\.grimy_guam_leaf\b") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "known-bad-obj-ref" -Message "Known bad symbol candidate. Prefer rev233 symbol names (example: grimy_guam, raw_tuna)."
        }

        if ($line -match "private\s+object\s+[A-Za-z0-9_]+\s*:\s*[A-Za-z0-9_]*References") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "private-references" -Message "TypeReferences subclasses must be internal or public, never private."
        }
        
        if ($line -notmatch "^\s*//" -and $line -match 'find\("blankobject"\)') {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "blankobject-mapping" -Message "BANNED: Do not map symbols to 'blankobject'. This causes Duplicate Key crashes during boot."
        }

        if ($line -notmatch "^\s*//" -and $line -match 'find\([^)]+,\s*-?\d+\)') {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "hardcoded-find-id" -Message "BANNED: Do not use hardcoded integer IDs in find() calls. This causes Hash Mismatch crashes."
        }

        if ($line -match "bindScript<") {
            Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "illegal-bindscript" -Message "Do not use bindScript<T>(). Scripts are auto-discovered."
        }

        if ($line -match "(const\s+)?val\s+[A-Za-z0-9_]+\s*=\s*`"[A-Za-z0-9_]+`"") {
            # Basic heuristic to catch things like `val maple_shortbow = "maple_shortbow"` inside scripts/configs.
            # We don't want to ban all strings, but this pattern is highly suspicious for placeholder types.
            # Skip if this line or the previous line has a HYGIENE suppression comment
            $hasSuppression = $line -match "HYGIENE:"
            if (-not $hasSuppression -and $i -gt 0) {
                $prevLine = $content[$i - 1]
                $hasSuppression = $prevLine -match "HYGIENE:"
            }
            if (-not $hasSuppression) {
                Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "suspicious-string-placeholder" -Message "Suspicious string literal assignment. Ensure you are not using Strings as placeholders for ObjType/NpcType."
            }
        }

        if ($usesBaseObjs) {
            foreach ($m in [regex]::Matches($line, "\bobjs\.([A-Za-z0-9_]+)\b")) {
                $name = $m.Groups[1].Value
                if (-not $baseObjs.Contains($name)) {
                    Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "unknown-base-obj-ref" -Message "BaseObjs does not contain '$name'."
                }
            }
        }
        if ($usesBaseNpcs) {
            foreach ($m in [regex]::Matches($line, "\bnpcs\.([A-Za-z0-9_]+)\b")) {
                $name = $m.Groups[1].Value
                if (-not $baseNpcs.Contains($name)) {
                    Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "unknown-base-npc-ref" -Message "BaseNpcs does not contain '$name'."
                }
            }
        }
        if ($usesBaseLocs) {
            foreach ($m in [regex]::Matches($line, "\blocs\.([A-Za-z0-9_]+)\b")) {
                $name = $m.Groups[1].Value
                if (-not $baseLocs.Contains($name)) {
                    Add-Issue -Issues $issues -File $file.FullName -Line $lineNo -Kind "unknown-base-loc-ref" -Message "BaseLocs does not contain '$name'."
                }
            }
        }
    }
}

Write-Host "Preflight Ref Hygiene"
Write-Host "RepoRoot: $RepoRoot"
Write-Host "Scanned Kotlin files: $($kotlinFiles.Count)"
Write-Host "Issues found: $($issues.Count)"

$warningKinds = @(
    "unsafe-invadd"
)
$fatalIssues = $issues | Where-Object { $warningKinds -notcontains $_.kind }
$fatalCount = @($fatalIssues).Count
Write-Host "Fatal issues found: $fatalCount"

if ($issues.Count -gt 0) {
    foreach ($issue in $issues) {
        Write-Host ("[{0}] {1}:{2} - {3}" -f $issue.kind, $issue.file, $issue.line, $issue.message)
    }
}

if ($FailOnIssues -and $fatalCount -gt 0) {
    exit 1
}

exit 0
