@echo off
setlocal enabledelayedexpansion
title ApexStore - Shutdown Manager

echo ==============================================================================
echo                 APEXSTORE MICROSERVICES - SHUTDOWN MANAGER
echo ==============================================================================
echo.

set PORTS=8761 8080 8081 8082 8083 4200

for %%P in (%PORTS%) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%%P " ^| findstr "LISTENING"') do (
        echo [STOPPING] Process on port %%P (PID: %%a)...
        taskkill /F /PID %%a >nul 2>&1
    )
)

echo.
echo ==============================================================================
echo                  ALL APEXSTORE SERVICES HAVE BEEN STOPPED
echo ==============================================================================
echo.
pause
