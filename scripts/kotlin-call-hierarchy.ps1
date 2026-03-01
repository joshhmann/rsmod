param(
    [Parameter(Mandatory = $true)]
    [string]$Symbol,
    [string]$RepoRoot = "Z:\Projects\OSRS-PS-DEV",
    [string]$Scope = "rsmod",
    [ValidateSet("both", "callers", "callees")]
    [string]$Mode = "both",
    [int]$MaxResults = 200
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-RgPath {
    $cmd = Get-Command rg -ErrorAction SilentlyContinue
    if (-not $cmd) {
        throw "ripgrep (rg) is required but was not found on PATH."
    }
    return $cmd.Source
}

function Parse-RgLine {
    param([string]$Line)
    $match = [regex]::Match($Line, "^(?<file>[A-Za-z]:.+?):(?<line>\d+):(?<col>\d+):(?<text>.*)$")
    if (-not $match.Success) {
        return $null
    }

    return [pscustomobject]@{
        File = $match.Groups["file"].Value
        Line = [int]$match.Groups["line"].Value
        Column = [int]$match.Groups["col"].Value
        Text = $match.Groups["text"].Value
    }
}

function Get-KotlinDefinitions {
    param(
        [string]$Rg,
        [string]$Root,
        [string]$EscapedSymbol
    )

    $pattern = "^\s*(?:public|private|internal|protected|open|final|abstract|data|sealed|enum|annotation|value|inline|tailrec|suspend|operator|infix|const|lateinit|override|external|\s)*(?:fun|class|object|interface)\s+${EscapedSymbol}\b"
    $raw = & $Rg --line-number --column --glob "*.kt" --glob "!**/build/**" --glob "!**/.gradle/**" --glob "!**/.git/**" --glob "!**/.idea/**" --color never $pattern $Root
    if (-not $raw) {
        return @()
    }

    $matches = @()
    foreach ($line in $raw) {
        $parsed = Parse-RgLine -Line $line
        if ($parsed) {
            $matches += $parsed
        }
    }
    return $matches
}

function Get-CallerMatches {
    param(
        [string]$Rg,
        [string]$Root,
        [string]$EscapedSymbol
    )

    $pattern = "\b${EscapedSymbol}\s*\("
    $raw = & $Rg --line-number --column --glob "*.kt" --glob "!**/build/**" --glob "!**/.gradle/**" --glob "!**/.git/**" --glob "!**/.idea/**" --color never $pattern $Root
    if (-not $raw) {
        return @()
    }

    $callers = @()
    foreach ($line in $raw) {
        $parsed = Parse-RgLine -Line $line
        if (-not $parsed) {
            continue
        }

        if ($parsed.Text -match "^\s*(?:public|private|internal|protected|open|final|abstract|data|sealed|enum|annotation|value|inline|tailrec|suspend|operator|infix|const|lateinit|override|external|\s)*(?:fun|class|object|interface)\s+${EscapedSymbol}\b") {
            continue
        }
        $callers += $parsed
    }

    return $callers
}

function Get-FunctionBody {
    param(
        [string]$FilePath,
        [int]$StartLine
    )

    $lines = Get-Content -Path $FilePath
    if ($StartLine -lt 1 -or $StartLine -gt $lines.Count) {
        return @()
    }

    $body = [System.Collections.Generic.List[string]]::new()
    $depth = 0
    $seenBrace = $false

    for ($i = $StartLine - 1; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        if (-not $seenBrace -and $line.Contains("{")) {
            $seenBrace = $true
        }

        if (-not $seenBrace) {
            continue
        }

        [void]$body.Add($line)
        foreach ($c in $line.ToCharArray()) {
            if ($c -eq "{") { $depth++ }
            if ($c -eq "}") { $depth-- }
        }

        if ($seenBrace -and $depth -le 0) {
            break
        }
    }

    return $body
}

function Get-CalleesFromBody {
    param([string[]]$BodyLines)

    $keywords = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    foreach ($k in @("if", "for", "while", "when", "return", "throw", "catch", "super", "this", "class", "object", "interface", "fun", "val", "var", "in", "is", "as", "try", "else")) {
        [void]$keywords.Add($k)
    }

    $calleeSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
    $regex = [regex]"\b([A-Za-z_][A-Za-z0-9_]*)\s*\("

    foreach ($line in $BodyLines) {
        foreach ($m in $regex.Matches($line)) {
            $name = $m.Groups[1].Value
            if ($keywords.Contains($name)) {
                continue
            }
            [void]$calleeSet.Add($name)
        }
    }

    return @($calleeSet) | Sort-Object
}

$scanRoot = Join-Path $RepoRoot $Scope
if (-not (Test-Path $scanRoot)) {
    throw "Scope path does not exist: $scanRoot"
}

$rg = Get-RgPath
$escaped = [regex]::Escape($Symbol)
$definitions = Get-KotlinDefinitions -Rg $rg -Root $scanRoot -EscapedSymbol $escaped

Write-Host "Kotlin call hierarchy fallback"
Write-Host "Symbol: $Symbol"
Write-Host "Scope: $scanRoot"
Write-Host "Mode: $Mode"
Write-Host ""

if ($definitions.Count -eq 0) {
    Write-Host "Definitions: none found"
} else {
    Write-Host "Definitions:"
    foreach ($def in $definitions) {
        Write-Host ("- {0}:{1}:{2}" -f $def.File, $def.Line, $def.Column)
    }
}

if ($Mode -in @("both", "callers")) {
    Write-Host ""
    $callers = Get-CallerMatches -Rg $rg -Root $scanRoot -EscapedSymbol $escaped
    $limitedCallers = $callers | Select-Object -First $MaxResults
    Write-Host ("Callers (heuristic, max {0}): {1}" -f $MaxResults, $callers.Count)
    foreach ($caller in $limitedCallers) {
        Write-Host ("- {0}:{1}:{2} | {3}" -f $caller.File, $caller.Line, $caller.Column, $caller.Text.Trim())
    }
}

if ($Mode -in @("both", "callees")) {
    Write-Host ""
    if ($definitions.Count -eq 0) {
        Write-Host "Callees: skipped (no function definition found)."
    } else {
            $functionDefs = @($definitions | Where-Object { $_.Text -match "\bfun\b" })
        if ($functionDefs.Count -eq 0) {
            Write-Host "Callees: skipped (symbol is not a function)."
        } else {
            Write-Host "Callees (heuristic from function body):"
            foreach ($def in $functionDefs) {
                $body = Get-FunctionBody -FilePath $def.File -StartLine $def.Line
                $callees = @(Get-CalleesFromBody -BodyLines $body)
                Write-Host ("- {0}:{1}:{2}" -f $def.File, $def.Line, $def.Column)
                if ($callees.Count -eq 0) {
                    Write-Host "  (none detected)"
                    continue
                }

                $count = 0
                foreach ($callee in $callees) {
                    Write-Host ("  - {0}" -f $callee)
                    $count++
                    if ($count -ge $MaxResults) {
                        Write-Host "  - ... truncated"
                        break
                    }
                }
            }
        }
    }
}
