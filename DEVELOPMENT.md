# Development Guide

## 🛠️ Development Setup

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- Git

### Quick Start
1. Clone the repository
2. Run `./start-services.sh` (Linux/Mac) or `.\start-services.ps1` (Windows)
3. Access the services at the URLs shown in the output

## 📁 Service Structure

Each service is completely independent and can be developed separately:

### Market Data Service (`MarketPulse-market-data/`)
- **Port**: 8081
- **Purpose**: Fetches real-time market data
- **Key Components**:
  - `FinnhubStreamService` - WebSocket client for market data
  - `StreamController` - REST endpoints
  - `KafkaProducerConfig` - Kafka configuration

### Processor Service (`MarketPulse-processor/`)
- **Port**: 8082
- **Purpose**: Processes data and manages trades
- **Key Components**:
  - `MarketDataConsumer` - Kafka consumer
  - `TradeService` - Business logic
  - `TradeRepository` - Data persistence
  - `PricePublisher` - Real-time updates

### UI Desk Service (`MarketPulse-ui-desk/`)
- **Port**: 8080
- **Purpose**: Web interface and WebSocket communication
- **Key Components**:
  - `TradeWebSocketHandler` - WebSocket handling
  - `TradeUpdateConsumer` - Kafka consumer
  - `UIController` - REST endpoints
  - Static web interface

### Infrastructure (`MarketPulse-config/`)
- **Purpose**: Development environment
- **Components**:
  - Kafka broker
  - Zookeeper
  - Kafka-UI (port 9000)

## 🔧 Development Workflow

### 1. Independent Development
Each service can be developed independently:
```bash
# Work on market data service
cd MarketPulse-market-data
mvn spring-boot:run

# Work on processor service
cd MarketPulse-processor
mvn spring-boot:run

# Work on UI desk service
cd MarketPulse-ui-desk
mvn spring-boot:run
```

### 2. Testing
```bash
# Test individual services
cd MarketPulse-market-data && mvn test
cd MarketPulse-processor && mvn test
cd MarketPulse-ui-desk && mvn test
```

### 3. Building
```bash
# Build individual services
cd MarketPulse-market-data && mvn clean package
cd MarketPulse-processor && mvn clean package
cd MarketPulse-ui-desk && mvn clean package
```

## 🐳 Docker Development

Each service has its own Dockerfile:
```bash
# Build and run market data service
cd MarketPulse-market-data
docker build -t market-data .
docker run -p 8081:8081 market-data

# Build and run processor service
cd MarketPulse-processor
docker build -t processor .
docker run -p 8082:8082 processor

# Build and run UI desk service
cd MarketPulse-ui-desk
docker build -t ui-desk .
docker run -p 8080:8080 ui-desk
```

## ☸️ Kubernetes Development

Use the provided Kubernetes manifests:
```bash
# Deploy to Kubernetes
kubectl apply -f market-data-deployment.yaml
kubectl apply -f processor-deployment.yaml
kubectl apply -f ui-desk-deployment.yaml
kubectl apply -f ingress.yaml
```

## 🔄 Inter-Service Communication

Services communicate via Kafka topics:
- Market data → Processor: Raw market data
- Processor → UI Desk: Processed trade updates
- All services use WebSocket for real-time updates

## 📝 Adding New Features

1. **New Service**: Create a new directory with Spring Boot structure
2. **New Endpoint**: Add to the appropriate service's controller
3. **New Kafka Topic**: Update producer/consumer configurations
4. **New WebSocket Event**: Update WebSocket handlers

## 🐛 Debugging

### Logs
- Each service logs to its own console
- Use `docker-compose logs` for infrastructure logs
- Check individual service logs in their directories

### Port Conflicts
- Market Data: 8081
- Processor: 8082
- UI Desk: 8080
- Kafka-UI: 9000
- Kafka: 9092
- Zookeeper: 2181

### Common Issues
1. **Port already in use**: Kill existing processes or change ports
2. **Kafka connection failed**: Ensure infrastructure is running
3. **WebSocket connection failed**: Check service is running and port is correct

## 📚 Best Practices

1. **Keep services independent** - No shared dependencies
2. **Use Kafka for communication** - Avoid direct HTTP calls between services
3. **Follow Spring Boot conventions** - Use standard package structure
4. **Test independently** - Each service should have its own tests
5. **Document changes** - Update README.md when adding new services

## 🔍 Monitoring

- **Kafka-UI**: http://localhost:9000 - Monitor Kafka topics and messages
- **Service Health**: Each service has actuator endpoints
- **Logs**: Monitor individual service logs for debugging 