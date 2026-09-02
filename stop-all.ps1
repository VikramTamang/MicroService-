# ApexStore - Stop All Services (PowerShell)
$ports = @(8761, 8080, 8081, 8082, 8083, 4200)

Write-Host "==============================================================================" -ForegroundColor Red
Write-Host "                 APEXSTORE MICROSERVICES - SHUTDOWN MANAGER                   " -ForegroundColor Red
Write-Host "==============================================================================" -ForegroundColor Red
Write-Host ""

$stopped = 0
foreach ($port in $ports) {
    $connections = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($connections) {
        foreach ($conn in $connections) {
            $pidToKill = $conn.OwningProcess
            if ($pidToKill -and $pidToKill -ne 0) {
                try {
                    $proc = Get-Process -Id $pidToKill -ErrorAction SilentlyContinue
                    $procName = if ($proc) { $proc.ProcessName } else { "PID $pidToKill" }
                    Write-Host " [STOPPING] Port $port -> Killing $procName (PID $pidToKill)..." -ForegroundColor Yellow
                    Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
                    $stopped++
                } catch {
                    # Ignore if already stopped
                }
            }
        }
    }
}

if ($stopped -eq 0) {
    Write-Host " No running ApexStore services found on standard ports (8761, 8080-8083, 4200)." -ForegroundColor Gray
} else {
    Write-Host " Successfully terminated $stopped process(es)." -ForegroundColor Green
}

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Red
