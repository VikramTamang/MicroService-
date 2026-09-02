# ApexStore - One-Click Microservices Runner (PowerShell)
param(
    [switch]$SkipFrontend
)

$ErrorActionPreference = "Continue"
$rootDir = $PSScriptRoot
if (-not $rootDir) { $rootDir = Get-Location }

function Write-Badge {
    param([string]$Status, [string]$Name, [string]$Details, [ConsoleColor]$Color = [ConsoleColor]::Green)
    Write-Host " [" -NoNewline
    Write-Host "$Status" -ForegroundColor $Color -NoNewline
    Write-Host "] " -NoNewline
    Write-Host "$Name" -ForegroundColor White -NoNewline
    if ($Details) {
        Write-Host " - $Details" -ForegroundColor Gray
    } else {
        Write-Host ""
    }
}

function Is-PortInUse([int]$port) {
    $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    return ($null -ne $conn)
}

Clear-Host
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "                 APEXSTORE MICROSERVICES - STARTUP MANAGER                   " -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Eureka Server (8761)
if (Is-PortInUse 8761) {
    Write-Badge "SKIP" "Eureka Server" "Already listening on port 8761" -Color Yellow
} else {
    Write-Badge "START" "Eureka Server" "Launching on port 8761..." -Color Cyan
    Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir`" && color 0B && title [ApexStore] Eureka Server (8761) && .\mvnw.cmd -pl eureka-server spring-boot:run"
    Write-Host "   Waiting 8s for Eureka Registry initialization..." -ForegroundColor DarkGray
    Start-Sleep -Seconds 8
}

# 2. User Service (8081)
if (Is-PortInUse 8081) {
    Write-Badge "SKIP" "User Service" "Already listening on port 8081" -Color Yellow
} else {
    Write-Badge "START" "User Service" "Launching on port 8081..." -Color Green
    Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir`" && color 0A && title [ApexStore] User Service (8081) && .\mvnw.cmd -pl user-service spring-boot:run"
}

# 3. Product Service (8082)
if (Is-PortInUse 8082) {
    Write-Badge "SKIP" "Product Service" "Already listening on port 8082" -Color Yellow
} else {
    Write-Badge "START" "Product Service" "Launching on port 8082..." -Color Yellow
    Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir`" && color 0E && title [ApexStore] Product Service (8082) && .\mvnw.cmd -pl product-service spring-boot:run"
}

# 4. Order Service (8083)
if (Is-PortInUse 8083) {
    Write-Badge "SKIP" "Order Service" "Already listening on port 8083" -Color Yellow
} else {
    Write-Badge "START" "Order Service" "Launching on port 8083..." -Color Magenta
    Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir`" && color 0D && title [ApexStore] Order Service (8083) && .\mvnw.cmd -pl order-service spring-boot:run"
}

Write-Host "   Waiting 5s before launching API Gateway..." -ForegroundColor DarkGray
Start-Sleep -Seconds 5

# 5. API Gateway (8080)
if (Is-PortInUse 8080) {
    Write-Badge "SKIP" "API Gateway" "Already listening on port 8080" -Color Yellow
} else {
    Write-Badge "START" "API Gateway" "Launching on port 8080..." -Color White
    Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir`" && color 0F && title [ApexStore] API Gateway (8080) && .\mvnw.cmd -pl api-gateway spring-boot:run"
}

# 6. Frontend (4200)
if (-not $SkipFrontend) {
    if (Is-PortInUse 4200) {
        Write-Badge "SKIP" "Angular Frontend" "Already listening on port 4200" -Color Yellow
    } else {
        Write-Badge "START" "Angular Frontend" "Launching on port 4200..." -Color Cyan
        Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$rootDir\frontend`" && color 03 && title [ApexStore] Frontend (4200) && npm start"
    }
}

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "                      ALL SERVICES LAUNCHED SUCCESSFULLY!                    " -ForegroundColor Green
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host ""
Write-Host " Service Endpoints:" -ForegroundColor White
Write-Host "   • Eureka Registry:   http://localhost:8761" -ForegroundColor Cyan
Write-Host "   • API Gateway:        http://localhost:8080" -ForegroundColor White
Write-Host "   • User Service:       http://localhost:8081/swagger-ui.html" -ForegroundColor Green
Write-Host "   • Product Service:    http://localhost:8082/swagger-ui.html" -ForegroundColor Yellow
Write-Host "   • Order Service:      http://localhost:8083/swagger-ui.html" -ForegroundColor Magenta
Write-Host "   • Angular Frontend:   http://localhost:4200" -ForegroundColor Cyan
Write-Host ""
Write-Host " Utility commands:" -ForegroundColor Gray
Write-Host "   • Check Status:      .\status.ps1" -ForegroundColor Gray
Write-Host "   • Stop All:          .\stop-all.ps1" -ForegroundColor Gray
Write-Host "==============================================================================" -ForegroundColor Green
