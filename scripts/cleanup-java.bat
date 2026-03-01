@echo off
:: cleanup-java.bat — Kill all Java processes safely
::
:: Use this to clean up leftover Java processes from previous sessions.
:: Usage: scripts\cleanup-java.bat [--force]

@echo off
setlocal

echo [cleanup] Checking for Java processes...

:: List current Java processes
tasklist /FI "IMAGENAME eq java.exe" /FO TABLE 2>nul | find "java.exe" >nul
if errorlevel 1 (
    echo [cleanup] No Java processes found.
    exit /b 0
)

echo [cleanup] Found Java processes:
echo.
tasklist /FI "IMAGENAME eq java.exe" /FO TABLE
echo.

:: Check for --force flag
if "%~1"=="--force" (
    echo [cleanup] Force flag set, killing all Java processes...
    goto :do_kill
)

:: Ask for confirmation
choice /C YN /M "Kill all Java processes"
if errorlevel 2 (
    echo [cleanup] Cancelled.
    exit /b 0
)

:do_kill
echo [cleanup] Stopping Java processes...
taskkill /F /IM java.exe 2>nul
if errorlevel 1 (
    echo [cleanup] Some processes may require admin privileges.
    echo [cleanup] Try running as Administrator if needed.
) else (
    echo [cleanup] ✅ Java processes stopped.
)

:: Verify
timeout /t 2 /nobreak >nul
tasklist /FI "IMAGENAME eq java.exe" /FO CSV 2>nul | find "java.exe" >nul
if errorlevel 1 (
    echo [cleanup] ✅ All Java processes cleaned up.
) else (
    echo [cleanup] ⚠️  Some Java processes still running:
    tasklist /FI "IMAGENAME eq java.exe" /FO TABLE | findstr "java"
)

endlocal
