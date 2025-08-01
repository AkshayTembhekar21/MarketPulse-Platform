# MarketPulse Services Startup Script
# This script helps start all microservices for development

Write-Host "Starting MarketPulse Trading Platform Services..." -ForegroundColor Green

# Function to check if a port is in use
function Test-Port {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $connection -ne $null
}

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [int]$Port
    )
    
    Write-Host "Starting $ServiceName..." -ForegroundColor Yellow
    
    if (Test-Port $Port) {
        Write-Host "WARNING: Port $Port is already in use. Skipping $ServiceName" -ForegroundColor Red
        return
    }
    
    if (Test-Path $ServicePath) {
        Set-Location $ServicePath
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvn spring-boot:run" -WindowStyle Normal
        Write-Host "SUCCESS: $ServiceName started on port $Port" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Service path not found: $ServicePath" -ForegroundColor Red
    }
}

# Start infrastructure first
Write-Host "Starting infrastructure (Kafka, Zookeeper)..." -ForegroundColor Cyan
if (Test-Path "MarketPulse-config") {
    Set-Location "MarketPulse-config"
    docker-compose up -d
    Set-Location ".."
    Write-Host "SUCCESS: Infrastructure started" -ForegroundColor Green
} else {
    Write-Host "ERROR: MarketPulse-config directory not found" -ForegroundColor Red
}

# Wait a bit for infrastructure to start
Write-Host "Waiting for infrastructure to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Start services
Start-Service "UI Desk Service" "MarketPulse-ui-desk" 8080
Start-Sleep -Seconds 2

Start-Service "Market Data Service" "MarketPulse-market-data" 8081
Start-Sleep -Seconds 2

Start-Service "Processor Service" "MarketPulse-processor" 8082
Start-Sleep -Seconds 2

Start-Service "P2P Trading Service" "marketpulse-p2p-trading" 8083
Start-Sleep -Seconds 2

Start-Service "Gateway Service" "marketpulse-gateway" 8084

Write-Host ""
Write-Host "SUCCESS: All services started!" -ForegroundColor Green
Write-Host ""
Write-Host "Access your services at:" -ForegroundColor Cyan
Write-Host "   UI Dashboard: http://localhost:8080" -ForegroundColor White
Write-Host "   Gateway API: http://localhost:8084" -ForegroundColor White
Write-Host "   Kafka UI: http://localhost:9000" -ForegroundColor White
Write-Host "   Market Data API: http://localhost:8081" -ForegroundColor White
Write-Host "   Processor API: http://localhost:8082" -ForegroundColor White
Write-Host "   P2P Trading API: http://localhost:8083" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 