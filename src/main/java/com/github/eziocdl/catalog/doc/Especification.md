Especificação completa   Plano de Projeto: Catálogo de Produtos com Cache (MVP)
Este plano cobre desde a arquitetura inicial até o deployment na AWS via Kubernetes, garantindo que você aplique DDD, CQRS, Caching e DevOps essenciais.
ÉPICO 1: ��️ Setup de Engenharia e Arquitetura Base 
Objetivo: Estabelecer o ambiente, a estrutura (Clean Arch) e as ferramentas essenciais (Docker, TDD).
1.1. História: Estrutura Base (Clean Architecture/Spring Boot)
● Critério de Aceite: O projeto está iniciado, a estrutura de pacotes está criada e o datasource configurado.
● Passos de Implementação:
● T1.1.1 (Projeto Base): Crie um projeto Spring Boot vazio via Spring Initializr, com as dependências Web, JPA, PostgreSQL Driver, Validation e Testes.
● T1.1.2 (Estrutura): Dentro de src/main/java/com.yourcompany.catalog, crie manualmente os quatro pacotes principais e seus sub-pacotes vazios, seguindo a convenção em inglês: api, application (command, query), domain (entity, repository, exception, event) e infrastructure (repository, config, cache, messaging). (Este é o seu primeiro commit de estrutura).
● T1.1.3 (Configuração DB): Configure o arquivo application.yml para se conectar ao seu PostgreSQL (ou H2 em memória, se preferir começar mais simples), definindo as credenciais básicas.
1.2. História: Qualidade de Código (TDD)
● Critério de Aceite: O teste de validação de Product é escrito e falha (RED).
● Passos de Implementação:
● T1.2.1 (Dependências): Verifique se o JUnit 5 e Mockito estão nas dependências do pom.xml ou build.gradle (deve estar se usou o Initializr).
● T1.2.2 (Escrever o Teste - TDD): Crie o arquivo ProductTest.java (em src/test/java/...) e escreva um teste que garanta que um Product não pode ser criado com o preço zero. O teste deve falhar, pois a classe Product ainda não existe.
1.3. História: Dockerização Local e Documentação
● Critério de Aceite: O projeto é documentado com Swagger e roda localmente em containers (App + DB + Redis).
● Passos de Implementação:
● T1.3.1 (Dockerfile): Crie um Dockerfile na raiz do projeto para criar a imagem do seu JAR do Spring Boot. Utilize um multi-stage build (base JDK para compilar, JRE menor para rodar) como boa prática de otimização.
● T1.3.2 (Orquestração): Crie o arquivo docker-compose.yml para definir e interligar os serviços: seu App, o PostgreSQL e o Redis. Defina portas e variáveis de ambiente para a conexão do App com o DB/Redis.
● T1.3.3 (Swagger): Adicione a dependência SpringDoc OpenAPI e configure-a na camada infrastructure.config.
ÉPICO 2: �� Domínio e Escrita de Dados (DDD & CQRS - Command)
Objetivo: Implementar o coração do sistema (Product) e o fluxo de escrita de dados.
2.1. História: Modelagem de Domínio (Entidade Product)
● Critério de Aceite: A entidade Product é implementada de forma imutável, passando no teste criado em T1.2.2.
● Passos de Implementação:
● T2.1.1 (Exceção de Domínio): Crie a classe DomainValidationException (em domain.exception), estendendo RuntimeException.
● T2.1.2 (Implementação do Domínio): Crie a classe Product.java (em domain.entity). Defina seus atributos como final. Implemente um construtor que use getters/setters privados e que lance a DomainValidationException se o nome for nulo/vazio ou o preço for menor ou igual a zero.
● T2.1.3 (Passar no Teste - TDD): Execute o ProductTest.java (de T1.2.2). O teste deve agora passar (GREEN).
2.2. História: Abstração de Acesso a Dados (Padrão Repository)
● Critério de Aceite: A comunicação de dados é abstraída via Padrão Repository.
● Passos de Implementação:
● T2.2.1 (Contrato): Crie a interface ProductRepository (em domain.repository). Defina métodos puros (sem dependência de Spring Data), como save(Product product) e findById(UUID id).
● T2.2.2 (Implementação JPA): Crie a interface JpaProductRepository (em infrastructure.repository), estendendo JpaRepository do Spring Data. Use anotações JPA (@Entity) na classe Product (isso é um adapter de infraestrutura).
2.3. História: Criação de Produto (Command)
● Critério de Aceite: O endpoint POST /products aceita dados válidos e salva no DB.
● Passos de Implementação:
● T2.3.1 (DTOs): Crie o DTO de entrada CreateProductCommand (em application.command.dto) com as validações de input (@NotNull, @Min).
● T2.3.2 (Handler/Use Case): Crie a classe CreateProductCommandHandler (em application.command.handler). Injete o ProductRepository (interface do domínio) e implemente o método handle() que: 1. Valida o Command. 2. Cria a entidade Product usando seu construtor validado. 3. Chama repository.save().
● T2.3.3 (API Controller): Crie a classe ProductController (em api.controller) com o método POST /products que recebe o Command e chama o CreateProductCommandHandler.
ÉPICO 3: �� Consulta de Dados de Alta Performance (CQRS - Query & Caching)
Objetivo: Implementar o lado da leitura (Query) e o Cache (Redis).
3.1. História: Consulta de Produto por ID (Query)
● Critério de Aceite: O endpoint GET /products/{id} retorna dados com baixa latência (sem cache, ainda).
● Passos de Implementação:
● T3.1.1 (Query DTO): Crie o DTO de saída ProductDetailQuery (em application.query.dto). Este DTO representa apenas os dados que serão exibidos (pode ser diferente da entidade Product).
● T3.1.2 (Query Handler): Crie a classe GetProductByIdQueryHandler (em application.query.handler). Implemente o método handle() que busca no ProductRepository e mapeia o resultado para o DTO ProductDetailQuery.
● T3.1.3 (API Controller): Adicione o método GET /products/{id} ao ProductController para chamar o Query Handler.
3.2. História: Implementação de Caching com Redis
● Critério de Aceite: A consulta (H3.1) usa o Redis como primeira fonte de dados.
● Passos de Implementação:
● T3.2.1 (Configuração Redis): Adicione a dependência Spring Data Redis e configure a conexão Redis no application.yml e no docker-compose.yml.
● T3.2.2 (Cacheable): Aplique a anotação @Cacheable("products") no método de busca do GetProductByIdQueryHandler para cachear o resultado da Query.
● T3.2.3 (Cache Evict): Aplique a anotação @CacheEvict(value = "products", key = "#command.id") no método handle() do CreateProductCommandHandler (ou no Handler de Update) para invalidar o cache após a escrita/atualização.
ÉPICO 4: ☁️ Deploy e Observabilidade (AWS, Docker & K8s)
Objetivo: Mover a aplicação para a nuvem AWS e praticar orquestração com Kubernetes, garantindo que a aplicação seja observável.
4.1. História: Observabilidade e Telemetria
● Critério de Aceite: A aplicação gera logs estruturados e expõe métricas de saúde e performance.
● Tarefas Técnicas:
● T4.1.1: Configurar Spring Boot Actuator para /health, /metrics e /info.
● T4.1.2: Configurar o logger (Ex: Logback) para emitir logs em formato JSON.
4.2. História: Orquestração Local com Kubernetes (K8s)
● Critério de Aceite: A aplicação é executada usando manifestos básicos de Kubernetes localmente (via Minikube/K3s).
● Tarefas Técnicas:
● T4.2.1: Criar o manifesto de Deployment para a aplicação Catálogo.
● T4.2.2: Criar o manifesto de Service para expor a aplicação internamente e externamente.
● T4.2.3: Criar manifestos de Deployment/Service para o Redis (como um serviço externo/interno ao seu Pod).
4.3. História: Deploy Simples na AWS EKS
● Critério de Aceite: A aplicação está rodando na AWS, acessível, e utilizando a imagem do Docker.
● Tarefas Técnicas:
● T4.3.1: Criar um registro de imagens (Ex: AWS ECR) e subir a imagem Docker do projeto.
● T4.3.2: Configurar um cluster AWS EKS (ou similar, dependendo da sua conta).
● T4.3.3: Aplicar os manifestos do Kubernetes (T4.2) no cluster EKS.
 ÉPICO 5: ☁️ Deploy, Observabilidade e K8s (AWS)
Objetivo: Mover a aplicação para a nuvem AWS e praticar orquestração com Kubernetes, garantindo que a aplicação seja observável.
5.1. História: Observabilidade e Telemetria
● Critério de Aceite: A aplicação gera logs estruturados e expõe métricas de saúde e performance.
● Tarefas Técnicas:
● T5.1.1: Configurar Spring Boot Actuator para os endpoints /health, /metrics e /info.
● T5.1.2: Configurar o logger (Ex: Logback) para emitir logs em formato JSON (logs estruturados) para facilitar a leitura por sistemas de monitoramento.
5.2. História: Orquestração Local com Kubernetes (K8s)
● Critério de Aceite: A aplicação é executada usando manifestos básicos de Kubernetes localmente (via Minikube/K3s).
● Tarefas Técnicas:
● T5.2.1: Criar o manifesto de Deployment K8s para a aplicação Catálogo (definição do container, réplicas, etc.).
● T5.2.2: Criar o manifesto de Service K8s para expor a aplicação internamente e externamente.
● T5.2.3: Criar manifestos de Deployment/Service para o Redis e o Broker de Mensageria.
5.3. História: Deploy Simples na AWS EKS (Com CI/CD Flow)
● Critério de Aceite: A aplicação está rodando na AWS, acessível, e utilizando a imagem do Docker.
● Tarefas Técnicas:
● T5.3.1 (CI/CD Placeholder): Crie um arquivo placeholder (Ex: build-and-push-to-ecr.yml ou Jenkinsfile) que defina o fluxo de Build (compilar, rodar testes) e Push (enviar imagem para o ECR), demonstrando o entendimento do pipeline de CI/CD.
● T5.3.2: Criar um registro de imagens (Ex: AWS ECR) e subir a imagem Docker do projeto para a nuvem.
● T5.3.3: Configurar um cluster AWS EKS (ou similar).
● T5.3.4: Aplicar os manifestos do Kubernetes (T5.2) no cluster EKS.
    �� Project Structure (Inglês)
Assumindo o pacote base como com.yourcompany.catalog:
src/main/java/com/yourcompany/catalog
├── api
│   └── controller          # ⬅️ REST Adapters (e.g., @RestController)
│       └── ProductController
├── application
│   ├── command             # ⬅️ Application Services (Write Side - C of CQRS)
│   │   └── handler
│   │   └── dto
│   │       └── CreateProductCommand
│   └── query               # ⬅️ Application Services (Read Side - Q of CQRS)
│       └── handler
│       └── dto
│           └── ProductDetailQuery
├── domain                  # ⬅️ THE CORE (Business Rules, Framework Independent)
│   ├── entity              # Entities and Aggregate Roots
│   │   └── Product
│   ├── repository          # Repository Contracts (Interfaces)
│   │   └── ProductRepository
│   ├── event               # Domain Events (for Messaging)
│   │   └── ProductCreatedEvent
│   └── exception           # Custom Domain Exceptions
│       └── DomainValidationException
├── infrastructure          # ⬅️ Infrastructure Adapters (Technology details)
│   ├── repository          # JPA/Hibernate Implementations
│   │   └── JpaProductRepository
│   ├── cache               # Redis Configuration and Implementation
│   ├── messaging           # RabbitMQ/Kafka Producer/Consumer Implementations
│   ├── config              # General Configurations (Swagger, WebSecurity)
└── CatalogApplication.java
     
  
    
2. ��️ Arquitetura e Boas Práticas: O Mapa do Tesouro
                              Este projeto utiliza uma arquitetura híbrida de alto nível de maturidade, focada em performance, testabilidade e manutenibilidade.
                              2.1. Arquitetura Principal: Clean Architecture (Hexagonal/Ports & Adapters)
                              ● O Que É: É um padrão que define a separação do código em círculos concêntricos, onde a lógica de negócio (Domain) é o centro e é independente das camadas externas (UI, Banco de Dados, Frameworks).
                              ● Referências: Robert C. Martin ("Uncle Bob") e Alistair Cockburn (Hexagonal Architecture).
                              ● Boas Práticas Aplicadas:
                              ● Inversão de Dependência (DIP): As dependências sempre apontam para dentro. A camada de infraestrutura depende da camada de domínio, nunca o contrário.
                              ● Testabilidade: O Domain e o Application (nossos Use Cases/Handlers) podem ser testados com 100% de testes unitários, sem a necessidade de subir o Spring, o banco de dados ou o cache.
                              2.2. Padrão de Design: CQRS (Command Query Responsibility Segregation)
                              ● O Que É: Não é uma arquitetura inteira, mas um padrão que separa o modelo de Escrita (Commands) do modelo de Leitura (Queries).
                              ● Referências: Greg Young.
                              ● Boas Práticas Aplicadas:
                              ● Escalabilidade: O lado da leitura (Query) pode ser otimizado (ex: usando views customizadas ou indo diretamente ao Cache) de forma independente do lado da escrita (Command).
                              ● Performance: No nosso projeto, o lado da Query é onde aplicamos o Caching (Redis) para obter respostas ultra-rápidas.
                              2.3. Modelagem: Domain Driven Design (DDD)
                              ● O Que É: Uma abordagem para desenvolver software complexo, focada em modelar o código para refletir o Domínio de Negócio.
                              ● Referências: Eric Evans.
                              ● Boas Práticas Aplicadas:
                              ● Aggregate Root (Product): A entidade Product é o ponto de entrada para todas as mudanças em seu domínio, garantindo que as regras de negócio sejam sempre respeitadas (ex: Validação de preço no construtor).
                              ● Imutabilidade: Entidades de Domínio devem ser, preferencialmente, imutáveis para evitar estados inválidos.
                              ● Exceptions de Domínio: Criar exceções específicas (DomainValidationException) em vez de usar exceções genéricas (como RuntimeException) para tornar o código mais expressivo.
                              2.4. Princípios Fundamentais (Os Pilares do Código)
                              Princípio	Onde Usamos no Projeto	Impacto na Qualidade
                              SOLID (SRP e DIP)	Cada Handler (CQRS) tem uma única responsabilidade (SRP). O uso de Interfaces de Repository garante o DIP.	Código claro, fácil de manter e substituir tecnologias.
                              KISS (Keep It Simple, Stupid)	Evitar abstrações desnecessárias. Usar Spring Boot para gerenciar a complexidade de configuração.	Reduz a curva de aprendizado e a chance de bugs.
                              TDD (Test Driven Development)	Escrever o teste antes de implementar a lógica de Produto (Tarefa 1.2).	Garante que as regras de negócio funcionem e aumenta a confiança na refatoração.
                              2.5. Tecnologia e DevOps
                              Tecnologia/Padrão	Onde Usamos no Projeto	Valor no Mercado
                              Caching Distribuído	Redis (ÉPICO 3). Usado para consultas de leitura rápidas.	Habilidade crítica para resolver problemas de escala e latência.
                              Event-Driven / Mensageria	RabbitMQ/Kafka (ÉPICO 4). Usado para desacoplar a criação de produto de outros sistemas.	Essencial em arquiteturas de Microserviços.
                              Orquestração	Docker e Kubernetes (K8s) (ÉPICO 5).	O must-have para qualquer Engenheiro Cloud-Native que faz deploy na AWS.
                              Documentação	Swagger/OpenAPI.	Permite que equipes de Frontend consumam sua API com eficiência, mostrando maturidade em APIs RESTful.
 



text
# Catálogo de Produtos com Cache (MVP)
 
## Sumário
- Visão Geral
- Épicos e Histórias
- Estrutura do Projeto
- Arquitetura e Boas Práticas
- Stack Tecnológico
- Critérios de Aceite
- Referências
 
---
 
## Visão Geral
Sistema de catálogo de produtos com arquitetura DDD, CQRS, caching distribuído (Redis) e deployment automatizado em nuvem AWS usando Docker e Kubernetes.
 
---
 
## Épicos e Histórias
 
### Épico 1: Setup de Engenharia e Arquitetura Base ��️
**Objetivo**: Estabelecer ambiente, estrutura Clean Architecture e as ferramentas essenciais.
 
- História 1.1: Estrutura Base  
  - Projeto Spring Boot criado (Web, JPA, PostgreSQL, Validation, Testes).
  - Estrutura de pacotes em inglês criada conforme Clean Architecture.
  - Datasource configurado (`application.yml`).
 
- História 1.2: Qualidade de Código (TDD)  
  - Testes falhando (RED) para validação de Product.
 
- História 1.3: Dockerização Local e Documentação  
  - Dockerfile multi-stage e docker-compose com App + DB + Redis.
  - Swagger configurado.
 
---
 
### Épico 2: Domínio e Escrita de Dados (DDD & CQRS – Command) ��
**Objetivo**: Implementar o domínio (Product) e fluxo de escrita.
 
- História 2.1: Modelagem de Domínio  
  - Entidade Product imutável, validando nome/preço.
  - Exceções de domínio específicas criadas.
 
- História 2.2: Abstração de Dados  
  - Interface ProductRepository e JpaProductRepository.
 
- História 2.3: Criação de Produto (Command)  
  - DTO `CreateProductCommand`.
  - Handler `CreateProductCommandHandler`.
  - Endpoint `POST /products`.
 
---
 
### Épico 3: Consulta de Dados e Caching ��
**Objetivo**: Implementar leitura e cache (CQRS Query + Redis).
 
- História 3.1: Consulta de Produto por ID  
  - DTO `ProductDetailQuery` e handler GetProductByIdQueryHandler.
  - Endpoint `GET /products/{id}`.
 
- História 3.2: Caching com Redis  
  - @Cacheable aplicado ao handler.
  - @CacheEvict nas operações de escrita.
 
---
 
### Épico 4: Deploy e Observabilidade (AWS, Docker, K8s) ☁️
**Objetivo**: Orquestração e monitoramento na AWS.
 
- Observabilidade: Spring Boot Actuator (health, metrics), logs JSON.
- Orquestração local: Manifestos K8s para app, Redis, services.
- AWS Deploy: Imagem Docker no AWS ECR, cluster EKS rodando manifestos.
 
---
 
## Estrutura do Projeto
src/main/java/com/yourcompany/catalog ├── api │ └── controller │ └── ProductController ├── application │ ├── command │ │ └── handler │ │ └── dto │ │ └── CreateProductCommand │ └── query │ └── handler │ └── dto │ └── ProductDetailQuery ├── domain │ ├── entity │ │ └── Product │ ├── repository │ │ └── ProductRepository │ ├── event │ │ └── ProductCreatedEvent │ └── exception │ └── DomainValidationException ├── infrastructure │ ├── repository │ │ └── JpaProductRepository │ ├── cache │ ├── messaging │ ├── config └── CatalogApplication.java
text
 
---
 
## Arquitetura e Boas Práticas
 
- **Clean Architecture**: Independência de framework e tecnologia; domínio no centro.
- **CQRS**: Separação entre comandos (escrita) e queries (leitura), facilitando otimização e escalabilidade.
- **DDD**: Foco em modelagem de entidades de domínio e regras de negócio.
- **SOLID/KISS/TDD**: Manutenibilidade, legibilidade, testes unitários do domínio.
 
---
 
## Stack Tecnológico
 
- **Backend**: Spring Boot, JPA, PostgreSQL
- **Cache**: Redis
- **Mensageria**: RabbitMQ/Kafka (futuro)
- **APIs & Documentação**: SpringDoc OpenAPI/Swagger
- **Infra**: Docker, docker-compose, Kubernetes (manifests), AWS EKS, ECR
- **Observabilidade**: Spring Boot Actuator, logs estruturados JSON
 
---
 
## Critérios de Aceite
 
- Projeto roda localmente containerizado.
- API documentada.
- Testes unitários para domínio.
- Uso de cache (Redis) nas queries.
- Observabilidade e health checks da aplicação.
- Deploy funcional automatizado na AWS via Kubernetes.
---
 
## Referências
 
- Clean Architecture (Uncle Bob)
- Hexagonal Architecture (Cockburn)
- DDD (Eric Evans)
- CQRS (Greg Young)
- Boas práticas DevOps
 



Especificação Técnica (Specification)
1. Objetivo
   Desenvolver um sistema de catálogo de produtos performático, resiliente e escalável, utilizando Clean Architecture, DDD, CQRS, caching Redis e deployment em Kubernetes/AWS.
2. Requisitos Funcionais
   ● RF001: O sistema deve permitir o cadastro de produto com nome e preço obrigatórios.
   ● RF002: Não deve ser possível cadastrar produto com preço menor ou igual a zero.
   ● RF003: O sistema deve prover operação para consulta de produto por ID.
   ● RF004: As operações de escrita e leitura devem ser separadas (CQRS).
   ● RF005: As consultas devem ser otimizadas via cache Redis.
   ● RF006: A API deve ser documentada via OpenAPI/Swagger.
   ● RF007: Sistema deve expor endpoints /health, /metrics e /info.
3. Requisitos Não Funcionais
   ● RNF001: O projeto deve seguir Clean Architecture, com pacotes em inglês e separação clara de camadas.
   ● RNF002: Banco de dados PostgreSQL.
   ● RNF003: Caching Redis obrigatoriamente externo ao app.
   ● RNF004: Containerização (Docker) e orquestração via Kubernetes.
   ● RNF005: Deployment automatizado na AWS EKS, com imagens hospedadas no AWS ECR.
   ● RNF006: Código precisa estar coberto por testes unitários (TDD para domínio).
   ● RNF007: Logs devem estar no padrão JSON.
4. Regras de Negócio
   ● RB001: Produto só pode ser criado se nome não for vazio/nulo e preço > 0.
   ● RB002: Modificações e leitura de entidades são feitas exclusivamente via aggregate root.
   ● RB003: Sempre que um produto for criado/atualizado, o cache Redis correspondente deve ser invalidado.
   ● RB004: Apenas os atributos relevantes devem ser retornados nas queries (DTO desacoplado do domínio).
5. Critérios de Aceite
   ● Estrutura do código conforme o diagrama e pacotes descritos.
   ● Testes unitários cobrindo regras do domínio obrigatório.
   ● API funcional e documentada.
   ● Consulta usando cache Redis.
   ● Observabilidade e métricas/publicação de logs.
   ● Deploy na AWS automatizado, rodando em cluster EKS.
6. Fluxos Principais
   Cadastro de Produto (POST /products)
   1. Valida dados da requisição via DTO.
   2. Handler chama construtor Product validando regras.
   3. Persiste via ProductRepository.
   4. Invalida cache Redis.
   Consulta de Produto (GET /products/{id})
   1. Busca primeiramente no Redis.
   2. Se cache miss, busca no repositório e atualiza cache.
   3. Retorna DTO com dados necessários.
7. APIs
   ● POST /products - Cria produto
   ● Payload: { "name": "string", "price": number }
   ● Response: 201 Created
   ● GET /products/{id} - Consulta produto por ID
   ● Response: { "id": "uuid", "name": "string", "price": number }
8. Integrações e Observabilidade
   ● Redis configurado via docker-compose.yml e application.yml/env.
   ● Swagger gerado via SpringDoc/OpenAPI.
   ● Spring Boot Actuator expondo endpoints monitoração.
   ● Kubernetes manifests para app, Redis, services.
9. Deployment
   ● Dockerfile multi-stage.
   ● docker-compose para desenvolvimento local.
   ● Manifests K8s para app, Redis, Service (load balancer/clusterIP).
   ● Pipeline automatizado para build/teste/push imagem na ECR e apply no EKS.
 
