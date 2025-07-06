#!/bin/bash

# MarketPulse Services Startup Script
# This script helps start all microservices for development

echo "🚀 Starting MarketPulse Trading Platform Services..."

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        return 0
    else
        return 1
    fi
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    
    echo "Starting $service_name..."
    
    if check_port $port; then
        echo "⚠️  Port $port is already in use. Skipping $service_name"
        return
    fi
    
    if [ -d "$service_path" ]; then
        cd "$service_path"
        nohup mvn spring-boot:run > ../${service_name// /_}.log 2>&1 &
        echo "✅ $service_name started on port $port"
        cd ..
    else
        echo "❌ Service path not found: $service_path"
    fi
}

# Start infrastructure first
echo "📦 Starting infrastructure (Kafka, Zookeeper)..."
if [ -d "MarketPulse-config" ]; then
    cd MarketPulse-config
    docker-compose up -d
    cd ..
    echo "✅ Infrastructure started"
else
    echo "❌ MarketPulse-config directory not found"
fi

# Wait a bit for infrastructure to start
echo "⏳ Waiting for infrastructure to be ready..."
sleep 10

# Start services
start_service "Market Data Service" "MarketPulse-market-data" 8081
sleep 2

start_service "Processor Service" "MarketPulse-processor" 8082
sleep 2

start_service "UI Desk Service" "MarketPulse-ui-desk" 8080
sleep 2

start_service "P2P Trading Service" "marketpulse-p2p-trading" 8083

echo ""
echo "🎉 All services started!"
echo ""
echo "📊 Access your services at:"
echo "   UI Dashboard: http://localhost:8080"
echo "   Kafka UI: http://localhost:9000"
echo "   Market Data API: http://localhost:8081"
echo "   Processor API: http://localhost:8082"
echo "   P2P Trading API: http://localhost:8083"
echo ""
echo "To stop all services, run: pkill -f 'spring-boot:run'" 