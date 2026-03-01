@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
for %%A in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fA"
set "RSMOD=%PROJECT_ROOT%"
if exist "%PROJECT_ROOT%\rsmod\gradlew.bat" set "RSMOD=%PROJECT_ROOT%\rsmod"
set "DATA=%RSMOD%\.data"
set PASS=0
set FAIL=0

echo.
echo ========================================
echo  RSMod v2 Environment Diagnostic
echo ========================================
echo Project root: %PROJECT_ROOT%
echo.

echo [1] Java 21...
java -version 2> "%TEMP%\java_ver.txt"
findstr /C:"21" "%TEMP%\java_ver.txt" >nul
if errorlevel 1 (
    echo     FAIL - Java 21 not found or wrong version
    echo     FIX:  Install Java 21 ^(Temurin recommended^): https://adoptium.net
    set /a FAIL+=1
) else (
    echo     PASS - Java 21 found
    set /a PASS+=1
)
del "%TEMP%\java_ver.txt" >nul 2>&1

echo [2] Gradle wrapper...
if exist "%RSMOD%\gradlew.bat" (
    echo     PASS - gradlew.bat found
    set /a PASS+=1
) else (
    echo     FAIL - gradlew.bat missing
    echo     FIX:  Re-clone RSMod from the repo
    set /a FAIL+=1
)

echo [3] Game cache (vanilla)...
if exist "%DATA%\cache\vanilla" (
    echo     PASS - .data\cache\vanilla found
    set /a PASS+=1
) else (
    echo     FAIL - Cache not downloaded
    echo     FIX:  cd rsmod ^&^& gradlew.bat downloadCache --console=plain
    set /a FAIL+=1
)

echo [4] Packed cache (enriched)...
if exist "%DATA%\cache\enriched" (
    echo     PASS - .data\cache\enriched found
    set /a PASS+=1
) else (
    echo     FAIL - Cache not packed
    echo     FIX:  cd rsmod ^&^& gradlew.bat packCache --console=plain
    set /a FAIL+=1
)

echo [5] RSA key (game.key)...
if exist "%DATA%\game.key" (
    echo     PASS - game.key found
    set /a PASS+=1
) else (
    echo     FAIL - game.key MISSING ^(most common startup failure^)
    echo     FIX:  cd rsmod ^&^& gradlew.bat generateRsa --console=plain
    set /a FAIL+=1
)

echo [6] Client RSA modulus (client.key)...
if exist "%DATA%\client.key" (
    echo     PASS - client.key found
    set /a PASS+=1
) else (
    echo     FAIL - client.key missing ^(generated alongside game.key^)
    echo     FIX:  cd rsmod ^&^& gradlew.bat generateRsa --console=plain
    set /a FAIL+=1
)

echo [7] Symbol files...
if exist "%DATA%\symbols\npc.sym" (
    echo     PASS - symbols present
    set /a PASS+=1
) else (
    echo     FAIL - symbol files missing
    echo     FIX:  cd rsmod ^&^& gradlew.bat packCache --console=plain
    set /a FAIL+=1
)

echo [8] Compiled build output...
if exist "%RSMOD%\server\app\build\classes" (
    echo     PASS - server\app\build\classes found
    set /a PASS+=1
) else (
    echo     FAIL - Not compiled yet
    echo     FIX:  cd rsmod ^&^& gradlew.bat build --console=plain
    set /a FAIL+=1
)

echo [9] Port 43594 (game server)...
netstat -ano 2>nul | findstr ":43594 " | findstr "LISTENING" >nul
if %errorlevel%==0 (
    echo     WARN - Port 43594 already in use ^(server may already be running^)
) else (
    echo     PASS - Port 43594 free
    set /a PASS+=1
)

echo [10] Port 43595 (AgentBridge)...
netstat -ano 2>nul | findstr ":43595 " | findstr "LISTENING" >nul
if %errorlevel%==0 (
    echo     INFO - AgentBridge already listening ^(server is running and a player is connected^)
) else (
    echo     PASS - Port 43595 free ^(bridge starts after first player login^)
    set /a PASS+=1
)

echo [11] MCP server dependencies...
where bun >nul 2>&1
if %errorlevel%==0 (
    echo     PASS - bun found
    if exist "%PROJECT_ROOT%\mcp\node_modules" (
        echo     PASS - mcp\node_modules installed
        set /a PASS+=1
    ) else (
        if exist "%PROJECT_ROOT%\..\mcp\node_modules" (
            echo     PASS - ..\mcp\node_modules installed
            set /a PASS+=1
        ) else (
            echo     WARN - mcp\node_modules not installed
            echo     FIX:  cd ..\mcp ^&^& bun install
        )
    )
) else (
    echo     WARN - bun not found ^(needed for MCP server only^)
    echo     FIX:  winget install oven-sh.bun  OR  https://bun.sh
)

echo.
echo ========================================
echo  Summary: %PASS% passed, %FAIL% failed
echo ========================================
if %FAIL%==0 (
    echo  All checks passed. Start server with:
    echo    scripts\start-server.bat
) else (
    echo  Fix failures above, then re-run this script.
    echo.
    echo  Quick fix for FRESH SETUP ^(cache already present^):
    echo    cd %RSMOD%
    echo    gradlew.bat generateRsa --console=plain
    echo    gradlew.bat build --console=plain
    echo.
    echo  Quick fix for COMPLETELY FRESH ^(no cache^):
    echo    scripts\install-server.bat
    echo    cd %RSMOD%
    echo    gradlew.bat build --console=plain
)
echo.

endlocal
