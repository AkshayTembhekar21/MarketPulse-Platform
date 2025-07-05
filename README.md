# MarketPulse Trading Platform

A real-time trading platform built with microservices architecture using Spring Boot, Kafka, and WebSocket technologies.

## 🏗️ Architecture

This project consists of 4 independent microservices:

### 📊 Market Data Service (`MarketPulse-market-data/`)
- **Purpose**: Fetches real-time market data from external APIs
- **Technology**: Spring Boot, WebSocket, Kafka
- **Features**: 
  - Real-time market data streaming
  - WebSocket client for external data sources
  - Kafka producer for data distribution

### 🔄 Processor Service (`MarketPulse-processor/`)
- **Purpose**: Processes market data and manages trade persistence
- **Technology**: Spring Boot, Kafka, JPA, H2 Database
- **Features**:
  - Market data processing and analysis
  - Trade persistence and management
  - Kafka consumer/producer for data flow
  - Real-time price publishing

### 🖥️ UI Desk Service (`MarketPulse-ui-desk/`)
- **Purpose**: Web-based trading interface and real-time data visualization
- **Technology**: Spring Boot, WebSocket, Kafka
- **Features**:
  - Real-time trading dashboard
  - WebSocket communication for live updates
  - Kafka consumer for trade updates
  - Static web interface

### ⚙️ Infrastructure Config (`MarketPulse-config/`)
- **Purpose**: Infrastructure and development environment setup
- **Technology**: Docker Compose, Kafka, Zookeeper
- **Features**:
  - Local development environment
  - Kafka and Zookeeper setup
  - Kafka-UI for monitoring

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven
- Docker & Docker Compose

### 1. Start Infrastructure
```bash
cd MarketPulse-config
docker-compose up -d
```

### 2. Start Services (in separate terminals)
```bash
# Market Data Service
cd MarketPulse-market-data
mvn spring-boot:run

# Processor Service
cd MarketPulse-processor
mvn spring-boot:run

# UI Desk Service
cd MarketPulse-ui-desk
mvn spring-boot:run
```

### 3. Access Services
- **UI Dashboard**: http://localhost:8080 (ui-desk service)
- **Kafka UI**: http://localhost:9000
- **Market Data API**: http://localhost:8081 (market-data service)
- **Processor API**: http://localhost:8082 (processor service)

## 📁 Project Structure

```
MarketPulse/
├── MarketPulse-market-data/     # Market data streaming service
├── MarketPulse-processor/       # Data processing and trade management
├── MarketPulse-ui-desk/         # Web-based trading interface
├── MarketPulse-config/          # Infrastructure and development setup
├── kafka-values.yaml            # Kubernetes Kafka configuration
├── ingress.yaml                 # Kubernetes ingress configuration
└── create cluster.txt           # Cluster setup instructions
```

## 🔧 Configuration

Each service has its own configuration files:
- `application.properties` - Default configuration
- `application-local.properties` - Local development settings
- `application-prod.properties` - Production settings

## 🐳 Docker Deployment

Each service includes a Dockerfile for containerized deployment:
- `MarketPulse-market-data/Dockerfile`
- `MarketPulse-processor/Dockerfile`
- `MarketPulse-ui-desk/Dockerfile`

## ☸️ Kubernetes Deployment

Kubernetes deployment files are provided:
- `market-data-deployment.yaml`
- `processor-deployment.yaml`
- `ui-desk-deployment.yaml`
- `ingress.yaml`
- `kafka-values.yaml`

## 📝 Development

### Adding New Features
1. Each service is independent - modify the relevant service
2. Use Kafka topics for inter-service communication
3. Follow the existing patterns for consistency

### Testing
```bash
# Run tests for each service
cd MarketPulse-market-data && mvn test
cd MarketPulse-processor && mvn test
cd MarketPulse-ui-desk && mvn test
```

## 🤝 Contributing

1. Each service can be developed independently
2. Follow Spring Boot best practices
3. Ensure proper error handling and logging
4. Update this README when adding new services

## 📄 License

This project is proprietary and confidential. 