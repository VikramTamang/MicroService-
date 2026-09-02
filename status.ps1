# ApexStore - Service Status Checker (PowerShell)
$services = @(
    @{ Name = "Eureka Server";   Port = 8761; Url = "http://localhost:8761" },
    @{ Name = "API Gateway";     Port = 8080; Url = "http://localhost:8080/actuator/health" },
    @{ Name = "User Service";    Port = 8081; Url = "http://localhost:8081/swagger-ui.html" },
    @{ Name = "Product Service"; Port = 8082; Url = "http://localhost:8082/swagger-ui.html" },
    @{ Name = "Order Service";   Port = 8083; Url = "http://localhost:8083/swagger-ui.html" },
    @{ Name = "Frontend (UI)";   Port = 4200; Url = "http://localhost:4200" }
)

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "                      APEXSTORE SERVICE STATUS DASHBOARD                      " -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

foreach ($svc in $services) {
    $conn = Get-NetTCPConnection -LocalPort $svc.Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn) {
        $pidNum = $conn.OwningProcess
        Write-Host "  " -NoNewline
        Write-Host "[RUNNING]" -ForegroundColor Green -NoNewline
        Write-Host " $($svc.Name.PadRight(18)) " -ForegroundColor White -NoNewline
        Write-Host "Port: $($svc.Port.ToString().PadRight(6)) " -ForegroundColor DarkGray -NoNewline
        Write-Host "PID: $($pidNum.ToString().PadRight(8)) " -ForegroundColor DarkGray -NoNewline
        Write-Host "$($svc.Url)" -ForegroundColor Cyan
    } else {
        Write-Host "  " -NoNewline
        Write-Host "[DOWN]   " -ForegroundColor Red -NoNewline
        Write-Host " $($svc.Name.PadRight(18)) " -ForegroundColor Gray -NoNewline
        Write-Host "Port: $($svc.Port.ToString().PadRight(6)) " -ForegroundColor DarkGray -NoNewline
        Write-Host "PID: --       " -ForegroundColor DarkGray -NoNewline
        Write-Host "$($svc.Url)" -ForegroundColor DarkGray
    }
}

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Cyan
