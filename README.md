# Product Catalog API

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red?style=flat&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=flat&logo=docker)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Enabled-326CE5?style=flat&logo=kubernetes)](https://kubernetes.io/)
[![AWS](https://img.shields.io/badge/AWS-EKS%20%7C%20ECR-orange?style=flat&logo=amazon-aws)](https://aws.amazon.com/)

A production-ready REST API for product catalog management, implementing **Clean Architecture**, **Domain-Driven Design (DDD)**, and **CQRS** patterns with Redis distributed caching and automated deployment to AWS EKS.

## Key Features

### Architecture
- **Clean Architecture**: Domain layer completely isolated from frameworks
- **CQRS Pattern**: Separate read and write operations for independent optimization
- **Domain-Driven Design**: Immutable, self-validating entities with business rules in the domain
- **Dependency Inversion**: All dependencies point inward (Domain → Application → Infrastructure)

### Performance
- **Redis Look-Aside Cache**: ~90% latency reduction (5-20ms vs 50-200ms)
- **Optimized Docker Builds**: Multi-stage builds for minimal image size

### Cloud-Native
- **Docker & Docker Compose**: Full containerization for local development
- **Kubernetes Orchestration**: Declarative manifests for app, PostgreSQL, and Redis
- **Automated CI/CD**: GitHub Actions → AWS ECR → EKS

### Observability
- **Spring Actuator**: Health checks, metrics, and system info
- **Structured JSON Logging**: Production-ready log format
- **Prometheus Metrics**: Ready for monitoring integration

## Architecture Overview

```
┌────────────────────────────────────────────────────────────────────┐
│                        API LAYER (Adapters)                        │
│                      ProductController.java                        │
│                    HTTP/REST → Use Cases                           │
└─────────────────────────────┬──────────────────────────────────────┘
                              │
           ┌──────────────────┴───────────────────┐
           │                                      │
           ▼                                      ▼
┌─────────────────────┐              ┌─────────────────────┐
│   COMMAND (Write)   │              │    QUERY (Read)     │
│                     │              │                     │
│ CreateProductCmd    │              │ GetProductById      │
│ Handler             │              │ QueryHandler        │
│                     │              │                     │
│ • Validate DTO      │              │ • Check Cache       │
│ • Create Entity     │              │ • Fallback DB       │
│ • Persist           │              │ • Update Cache      │
└──────────┬──────────┘              └──────────┬──────────┘
           │                                    │
           └────────────┬───────────────────────┘
                        │
                        ▼
         ┌──────────────────────────────────┐
         │         DOMAIN LAYER (Core)      │
         │                                  │
         │  • Product (Aggregate Root)      │
         │    - Immutable (final fields)    │
         │    - Self-validating             │
         │    - Business rules enforced     │
         │                                  │
         │  • ProductRepository Interface   │
         │  • DomainValidationException     │
         │                                  │
         │       Zero external dependencies │
         └────────────┬─────────────────────┘
                      │
                      ▼
         ┌──────────────────────────────────┐
         │    INFRASTRUCTURE LAYER          │
         │                                  │
         │  • JpaProductRepository          │
         │  • Redis Configuration           │
         │  • OpenAPI Configuration         │
         └──────────────────────────────────┘
                      │
         ┌────────────┴────────────┐
         │                         │
         ▼                         ▼
┌─────────────────┐      ┌─────────────────┐
│   PostgreSQL    │      │      Redis      │
│   (Persistent)  │      │   (Cache)       │
└─────────────────┘      └─────────────────┘
```

## Tech Stack

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 21 | Primary language (LTS) |
| **Framework** | Spring Boot | 3.5.7 | Application framework |
| **ORM** | Spring Data JPA / Hibernate | 6.x | Data persistence |
| **Database** | PostgreSQL | 15 | Primary data store |
| **Cache** | Redis | 7 | Distributed caching |
| **Documentation** | SpringDoc OpenAPI | 2.5.0 | API documentation |
| **Build** | Maven | 3.9+ | Dependency management |
| **Containerization** | Docker | 24+ | Container runtime |
| **Orchestration** | Kubernetes | 1.28+ | Container orchestration |
| **Cloud** | AWS EKS / ECR | - | Production deployment |
| **CI/CD** | GitHub Actions | - | Automated pipeline |

## Getting Started

### Prerequisites
- Java 21 (JDK)
- Maven 3.9+
- Docker & Docker Compose

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/eziocdl/product-catalog-api.git
cd product-catalog-api

# Start the full stack (App + PostgreSQL + Redis)
docker-compose up --build

# Application available at http://localhost:8080
```

### Option 2: Local Development

```bash
# Start only dependencies
docker-compose up postgres redis -d

# Run the application
mvn spring-boot:run
```

### Option 3: Kubernetes (Minikube/K3s)

```bash
# Start your local cluster
minikube start

# Apply manifests
kubectl apply -f k8s/

# Check pods
kubectl get pods

# Access the service
minikube service catalog-service --url
```

## API Documentation

### Swagger UI
After starting the application, access the interactive documentation at:
**http://localhost:8080/swagger-ui.html**

### Endpoints

#### Create Product
```http
POST /products
Content-Type: application/json

{
  "name": "Notebook Ultra",
  "price": 4300.00
}
```

**Response (201 Created)**:
```json
{
  "id": "bb3cf35e-8f15-4d33-ae84-222322f1fcd5"
}
```

#### Get Product by ID
```http
GET /products/{id}
```

**Response (200 OK)**:
```json
{
  "id": "bb3cf35e-8f15-4d33-ae84-222322f1fcd5",
  "name": "Notebook Ultra",
  "price": 4300.00
}
```

### Health & Metrics

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

## Caching Strategy

This project implements the **Look-Aside (Lazy Loading)** cache pattern with Redis:

```
Request → Check Redis Cache
              │
        ┌─────┴─────┐
        │           │
      HIT         MISS
        │           │
        │           ▼
        │     Query PostgreSQL
        │           │
        │           ▼
        │     Store in Redis
        │           │
        └─────┬─────┘
              │
              ▼
         Return Data
```

| Scenario | Latency | Database Queries |
|----------|---------|------------------|
| Cache Hit | ~5-20ms | 0 |
| Cache Miss | ~50-200ms | 1 |

## CI/CD Pipeline

The GitHub Actions workflow automates the entire deployment process:

```
git push → Build → Test → Docker Build → Push to ECR → Deploy to EKS
```

**Build Stage**:
1. Checkout code
2. Setup Java 21
3. Maven build and test
4. Package JAR

**Docker Stage**:
1. Build multi-stage Docker image
2. Tag with git SHA and `latest`
3. Push to AWS ECR

**Deploy Stage** (main branch only):
1. Configure kubectl
2. Update EKS deployment
3. Apply Kubernetes manifests

## Project Structure

```
src/
├── main/java/com/github/eziocdl/catalog/
│   ├── api/
│   │   └── controller/         # REST controllers
│   ├── application/
│   │   ├── command/
│   │   │   ├── dto/           # Input DTOs
│   │   │   └── handler/       # Command handlers
│   │   └── query/
│   │       ├── dto/           # Output DTOs
│   │       └── handler/       # Query handlers (cached)
│   ├── domain/
│   │   ├── entity/            # Aggregate roots
│   │   ├── repository/        # Repository interfaces
│   │   └── exception/         # Domain exceptions
│   └── infrastructure/
│       ├── repository/        # JPA implementations
│       └── config/            # Framework configurations
└── test/                      # Unit and integration tests

k8s/                           # Kubernetes manifests
docker-compose.yml             # Local development stack
Dockerfile                     # Multi-stage build
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | H2 in-memory |
| `SPRING_DATASOURCE_USERNAME` | Database username | - |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `SPRING_DATA_REDIS_HOST` | Redis host | localhost |
| `SPRING_DATA_REDIS_PORT` | Redis port | 6379 |

## Author

**Ezio Lima**
Backend Developer | Java Specialist | Cloud Enthusiast

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://www.linkedin.com/in/eziolima)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-black?style=flat&logo=github)](https://github.com/eziocdl)

## License

This project is available for educational purposes and as a technical portfolio demonstration.
