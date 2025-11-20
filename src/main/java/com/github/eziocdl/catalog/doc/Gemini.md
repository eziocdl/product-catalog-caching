# Gemini.md
## Project: Product Catalog with Cache (MVP)

### Main Objective

Build a product catalog system based on DDD, CQRS, and Clean Architecture patterns, with Redis caching, automated testing, and modern DevOps deployment (Docker, Kubernetes, AWS).

---

## 1. Overview

- System to **manage product creation and lookup** (`id`, `name`, `price`).
- Strict adoption of patterns: Clean Architecture, Domain Driven Design (DDD), and Command Query Responsibility Segregation (CQRS).
- DevOps: **Dockerization**, orchestration with **Kubernetes**, deployment to AWS EKS.
- **Distributed cache using Redis** for high performance reads.
- Integrated TDD and automatic API documentation (Swagger/OpenAPI).
- Observability: structured logs (JSON) + health & metrics exposed via Spring Boot Actuator.

---

## 2. Project Structure

src/main/java/com/yourcompany/catalog ├── api # REST controllers (entry point) │ └── controller │ └── ProductController ├── application # Use cases/application services (CQRS) │ ├── command │ │ ├── handler │ │ └── dto │ │ └── CreateProductCommand │ └── query │ ├── handler │ └── dto │ └── ProductDetailQuery ├── domain # Core business logic (framework independent) │ ├── entity │ │ └── Product │ ├── repository │ │ └── ProductRepository │ ├── event │ │ └── ProductCreatedEvent │ └── exception │ └── DomainValidationException ├── infrastructure # Technology adapters: DB, Redis, Messaging, Config │ ├── repository │ │ └── JpaProductRepository │ ├── cache │ ├── messaging │ ├── config └── CatalogApplication.java

text
---

## 3. Domain & Modeling

### Main Entity: Product

- **Attributes**: `id` (UUID), `name` (string), `price` (decimal or float).
- **Mandatory validations**:
    - Name cannot be null or empty.
    - Price **must be greater than zero**.
- Immutability: all fields are `final`, set only via constructor.

### Domain Exceptions

- Use `DomainValidationException` for any rule violation.

---

## 4. Patterns & Architecture

### Clean Architecture (Ports & Adapters)

- The `domain` layer is completely independent.
- The `application` layer implements use cases (Commands and Queries).
- Infrastructure connects persistence (JPA), cache (Redis), messaging (future), and config.
- **All dependencies point inward.**

### CQRS

- Commands: Product creation (`POST /products`).
- Queries: Product lookup by ID (`GET /products/{id}`).
- Exposed via REST Controller (`ProductController`).

### DDD

- `Product` is the aggregate root.
- All mutations handled via use case handlers.

---

## 5. Main Flows (Prompt Instructions for Gemini/AI)

**Product Creation Flow:**
User → POST /products
* Payload: { "name": "Mouse", "price": 250.0 } Controller → CreateProductCommandHandler
* Validates payload
* Creates Product (via constructor, rule validation)
* Persists via ProductRepository
* Evicts Redis cache for the product (if exists)
* Returns 201 Created

text
**Product Lookup Flow:**
User → GET /products/{id} Controller → GetProductByIdQueryHandler
* First looks up in Redis (cache)
* If HIT, returns cached result
* If MISS, fetches from repository, populates cache, returns result

text
---

## 6. Use Cases (API)

| Endpoint              | Method | Description                        | Payload/Response                                     |
|-----------------------|--------|------------------------------------|------------------------------------------------------|
| /products             | POST   | Create a product                   | { "name": "string", "price": number } => 201 Created |
| /products/{id}        | GET    | Lookup product by ID               | => { "id", "name", "price" }                         |
| /health, /metrics, /info | GET | Observability endpoints            | Actuator reports                                     |

---

## 7. Technology Stack

- **Backend**: Java, Spring Boot, Spring Data JPA, PostgreSQL
- **Cache**: Redis
- **Messaging**: RabbitMQ/Kafka (future)
- **API Documentation**: SpringDoc OpenAPI/Swagger
- **Infrastructure**: Docker, docker-compose, Kubernetes (manifests), AWS EKS, ECR
- **Observability**: Spring Boot Actuator, structured JSON logs

---

## 8. DevOps & Observability

- Multi-stage Dockerfile (Java base to build, slim JRE for runtime).
- docker-compose to orchestrate local environment (app + db + redis).
- Kubernetes manifests for app, Redis, and services.
- Automated deployment: build/push image → ECR → apply manifests → EKS.
- Endpoints: `/health`, `/metrics`, structured logging.

---

## 9. Rules & Acceptance Criteria for AI

- **Business rules are mandatory**: Always validate name and price when creating a `Product`.
- **Cache process**: Read from cache first, update/evict on changes.
- **Unit testing**: Domain must be testable with no external dependencies.
- **Any structural change must preserve API contracts.**
- Always update Swagger/OpenAPI when new operations are introduced.

---

## 10. Example Expected Responses (for LLM/Agent or Plugin)

**Valid product created:**
{ "id": "bb3cf35e-8f15-4d33-ae84-222322f1fcd5", "name": "Notebook Ultra", "price": 4300.00 }

text
**Invalid product attempt:**
{ "error": "DomainValidationException", "message": "Price must be greater than zero" }

text
---

## 11. Repository & Branches / CI/CD Requirements

- Main branch: always stable, ready for deploy.
- Feature branches: isolated changes, PRs with scope description.
- CI: Each push runs automated tests, build and coverage, generates new image if tests are green.

---

## 12. Glossary for AI

- **Handler**: Class responsible for a use case (Command or Query).
- **DTO (Data Transfer Object)**: Objects for data transfer between layers.
- **Repository**: Abstraction for storage access.
- **Cache Evict**: Action to remove particular cache entry.
- **K8s**: Kubernetes abbreviation.
- **EKS/ECR**: AWS services for container orchestration and Docker image registry.

---

### Instructions for Gemini/AI

Always follow this guide when suggesting code, reviewing PRs or answering questions about this project!
1. Prioritize business and data security rules.
2. Maintain strict Clean Architecture compliance.
3. Do not neglect observability or containerization.
4. Ensure domain validation is enforced at the entity/model level, not only in the API or controller.
5. For API doubts, always refer to the "Use Cases (API)" and review examples.

