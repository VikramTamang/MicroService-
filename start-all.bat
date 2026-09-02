@echo off
setlocal enabledelayedexpansion
title ApexStore - Startup Manager

echo ==============================================================================
echo                 APEXSTORE MICROSERVICES - ONE-CLICK STARTUP
echo ==============================================================================
echo.

set "ROOT_DIR=%~dp0"
cd /d "%ROOT_DIR%"

:: Check if Eureka (8761) is running
netstat -ano | findstr ":8761 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] Eureka Server is already running on port 8761.
) else (
    echo [STARTING] Eureka Server on port 8761...
    start "[ApexStore] Eureka Server (8761)" cmd /k "cd /d %ROOT_DIR% && color 0B && echo Starting Eureka Server... && mvnw.cmd -pl eureka-server spring-boot:run"
    echo Waiting 8 seconds for Eureka registry initialization...
    timeout /t 8 /nobreak >nul
)

:: Check if User Service (8081) is running
netstat -ano | findstr ":8081 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] User Service is already running on port 8081.
) else (
    echo [STARTING] User Service on port 8081...
    start "[ApexStore] User Service (8081)" cmd /k "cd /d %ROOT_DIR% && color 0A && echo Starting User Service... && mvnw.cmd -pl user-service spring-boot:run"
)

:: Check if Product Service (8082) is running
netstat -ano | findstr ":8082 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] Product Service is already running on port 8082.
) else (
    echo [STARTING] Product Service on port 8082...
    start "[ApexStore] Product Service (8082)" cmd /k "cd /d %ROOT_DIR% && color 0E && echo Starting Product Service... && mvnw.cmd -pl product-service spring-boot:run"
)

:: Check if Order Service (8083) is running
netstat -ano | findstr ":8083 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] Order Service is already running on port 8083.
) else (
    echo [STARTING] Order Service on port 8083...
    start "[ApexStore] Order Service (8083)" cmd /k "cd /d %ROOT_DIR% && color 0D && echo Starting Order Service... && mvnw.cmd -pl order-service spring-boot:run"
)

echo.
echo Waiting 5 seconds before starting API Gateway...
timeout /t 5 /nobreak >nul

:: Check if API Gateway (8080) is running
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] API Gateway is already running on port 8080.
) else (
    echo [STARTING] API Gateway on port 8080...
    start "[ApexStore] API Gateway (8080)" cmd /k "cd /d %ROOT_DIR% && color 0F && echo Starting API Gateway... && mvnw.cmd -pl api-gateway spring-boot:run"
)

:: Check if Frontend (4200) is running
netstat -ano | findstr ":4200 " | findstr "LISTENING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [SKIP] Frontend is already running on port 4200.
) else (
    echo [STARTING] Frontend on port 4200...
    start "[ApexStore] Angular Frontend (4200)" cmd /k "cd /d %ROOT_DIR%frontend && color 03 && echo Starting Frontend... && npm start"
)

echo.
echo ==============================================================================
echo                   ALL SERVICES HAVE BEEN LAUNCHED!
echo ==============================================================================
echo.
echo  Service Endpoints:
echo    - Eureka Dashboard:  http://localhost:8761
echo    - API Gateway:       http://localhost:8080
echo    - User Service:      http://localhost:8081/swagger-ui.html
echo    - Product Service:   http://localhost:8082/swagger-ui.html
echo    - Order Service:     http://localhost:8083/swagger-ui.html
echo    - Angular Frontend:  http://localhost:4200
echo.
echo  To stop all services anytime, run: stop-all.bat
echo ==============================================================================
echo.
pause
