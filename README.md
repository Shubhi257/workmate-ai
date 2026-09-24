# WorkMate AI

> Enterprise Knowledge & Support Assistant powered by Spring Boot, RAG, PGVector, Redis, Ollama, Docker, and GitHub Actions.

WorkMate AI is an AI-powered internal knowledge assistant designed to help employees find answers from company documents. It combines Retrieval-Augmented Generation (RAG), vector similarity search, conversational context, JWT authentication, document processing, caching, and containerized deployment.

---

## 🚀 Key Features

- 🔐 **JWT Authentication**
    - User registration and login
    - BCrypt password hashing
    - Protected chat APIs

- 📄 **Document Management**
    - Upload PDF, DOCX, and TXT files
    - Extract text using Apache Tika
    - Store document metadata and extracted content

- ✂️ **Document Chunking**
    - Splits documents into smaller chunks
    - Uses configurable chunk size and overlap
    - Stores chunks in PostgreSQL

- 🧠 **Retrieval-Augmented Generation (RAG)**
    - Converts document chunks into embeddings
    - Stores embeddings in PostgreSQL with PGVector
    - Performs similarity search for relevant context
    - Sends retrieved context to the local LLM
    - Generates grounded answers from available documents

- 💬 **Conversational Chat**
    - Persistent conversations
    - Stores user and assistant messages
    - Uses previous conversation context for follow-up questions

- ⚡ **Redis Caching**
    - Caches repeated AI responses
    - Configurable cache TTL
    - Cache invalidation when new documents are uploaded

- 🤖 **Local AI with Ollama**
    - Chat model: `llama3.2:3b`
    - Embedding model: `nomic-embed-text`
    - No dependency on paid cloud LLM APIs for local development

- 🐳 **Docker & Docker Compose**
    - Containerized Spring Boot application
    - Docker health check
    - Connectivity with PostgreSQL, Redis, and Ollama

- 🧪 **Automated Testing**
    - Unit tests
    - Repository tests
    - Service-layer tests
    - JWT tests
    - RAG-related tests
    - Full application-context test

- 🔄 **CI/CD**
    - GitHub Actions
    - Java 21 setup
    - Automated Maven tests
    - Application build
    - Docker image build
    - Docker image publishing to GitHub Container Registry (GHCR)

- ❤️ **Application Monitoring**
    - Spring Boot Actuator
    - Health endpoint
    - Database and Redis health indicators
    - Liveness and readiness information

---

## 🏗️ Architecture

```text
                         ┌──────────────────────┐
                         │       Client         │
                         │ Postman / Frontend   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     Spring Boot      │
                         │      REST APIs       │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
                    ▼               ▼                ▼
             ┌────────────┐  ┌────────────┐  ┌────────────┐
             │    JWT     │  │ Documents  │  │    Chat    │
             │  Security  │  │  & RAG     │  │  Service   │
             └────────────┘  └─────┬──────┘  └─────┬──────┘
                                   │                │
                                   ▼                ▼
                            ┌──────────────────────────┐
                            │       PostgreSQL          │
                            │ Users / Documents / Chat │
                            └────────────┬─────────────┘
                                         │
                                         ▼
                                  ┌──────────────┐
                                  │   PGVector   │
                                  │   Embeddings │
                                  └──────┬───────┘
                                         │
                                         ▼
                                  ┌──────────────┐
                                  │    Ollama    │
                                  │ llama3.2:3b  │
                                  └──────────────┘

                              ┌──────────────┐
                              │    Redis     │
                              │    Cache     │
                              └──────────────┘

                         GitHub → Actions → GHCR
```

---

## 🔎 RAG Flow

WorkMate AI uses Retrieval-Augmented Generation instead of sending a question directly to the LLM.

```text
User Question
      │
      ▼
Generate / process query
      │
      ▼
Vector similarity search
      │
      ▼
PGVector
      │
      ▼
Top relevant document chunks
      │
      ▼
Build grounded prompt
      │
      ▼
Ollama / Llama 3.2
      │
      ▼
AI Answer + Sources
```

### Example

Question:

```text
Who approves work from home requests?
```

The system retrieves relevant content from the uploaded company policy and generates:

```text
Employees must obtain approval from their reporting manager
before working from home.
```

The assistant is instructed to avoid inventing information when the answer is not present in the available document context.

---

## 🛠️ Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| API | Spring Web / REST |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL 18.6 |
| Vector Database | PGVector |
| AI Framework | Spring AI |
| LLM | Ollama / Llama 3.2 3B |
| Embeddings | Ollama / Nomic Embed Text |
| Document Parsing | Apache Tika |
| Cache | Redis / Memurai |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Containerization | Docker |
| Orchestration | Docker Compose |
| Monitoring | Spring Boot Actuator |
| CI/CD | GitHub Actions |
| Container Registry | GitHub Container Registry |

---

## 📁 Project Structure

```text
workmate-ai/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── src/
│   ├── main/
│   │   ├── java/com/workmate/workmate_ai/
│   │   │
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   │
│   │   ├── controller/
│   │   │   ├── HealthController.java
│   │   │   ├── AuthController.java
│   │   │   ├── DocumentController.java
│   │   │   ├── AiController.java
│   │   │   ├── ChatController.java
│   │   │   └── RedisTestController.java
│   │   │
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │
│   └── test/
│
├── Dockerfile
├── compose.yaml
├── .dockerignore
├── .gitignore
├── pom.xml
├── mvnw
└── mvnw.cmd
```

---

## 🔐 Authentication

The application uses stateless JWT authentication.

### Authentication flow

```text
Register
   ↓
Password hashed with BCrypt
   ↓
User stored in PostgreSQL
   ↓
Login
   ↓
Credentials validated
   ↓
JWT generated
   ↓
JWT sent in Authorization header
   ↓
JwtAuthenticationFilter validates token
   ↓
Protected API accessed
```

Protected APIs require:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 📚 Document Processing

Supported document formats:

- PDF
- DOCX
- TXT

Processing pipeline:

```text
Upload File
    ↓
Validate file type
    ↓
Save file
    ↓
Extract text using Apache Tika
    ↓
Save document metadata
    ↓
Split extracted text into chunks
    ↓
Generate embeddings
    ↓
Store vectors in PGVector
```

---

## ⚡ Redis Caching

AI responses are cached using Spring Cache with Redis.

Example flow:

```text
Question
   ↓
Redis cache lookup
   │
   ├── Cache hit → Return cached answer
   │
   └── Cache miss
          ↓
       Ollama
          ↓
       Store result in Redis
          ↓
       Return answer
```

The cache uses a configurable TTL.

When a new document is uploaded, the relevant AI answer cache is evicted so that future responses can use the updated knowledge base.

---

## 🧪 Testing

The project contains tests for:

- User service
- Document service
- Document chunking
- Document text extraction
- Embedding service
- Vector store service
- RAG service
- Chat service
- JWT service
- User repository
- Document repository
- Document chunk repository
- Conversation repository
- Chat message repository
- Spring Boot application context

Run the complete test suite:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

---

## 🐳 Docker

### Build the application

```powershell
.\mvnw.cmd clean package -DskipTests
```

### Build Docker image

```powershell
docker build -t workmate-ai:latest .
```

### Start using Docker Compose

```powershell
docker compose up -d
```

### Check container

```powershell
docker compose ps
```

The container should eventually show:

```text
healthy
```

### Stop the application

```powershell
docker compose down
```

---

## ❤️ Health & Monitoring

Spring Boot Actuator exposes:

```text
GET /actuator/health
```

Example response:

```json
{
  "status": "UP"
}
```

The health information includes application components such as:

- Database
- Redis
- Disk space
- Liveness
- Readiness

A Docker health check is also configured against the Actuator health endpoint.

---

## 🔄 CI/CD Pipeline

GitHub Actions automatically runs when code is pushed to `main` or `master`, or when a pull request targets those branches.

Pipeline:

```text
Git Push / Pull Request
          ↓
    Checkout Code
          ↓
       Java 21
          ↓
     Run Tests
          ↓
   Build Spring Boot
          ↓
     Build Docker
          ↓
    Login to GHCR
          ↓
    Push Docker Image
```

Docker images are published to GitHub Container Registry.

Example image:

```text
ghcr.io/<github-username>/workmate-ai:latest
```

---

## 📡 Main API Endpoints

### Authentication

```http
POST /api/auth/register
POST /api/auth/login
```

### Health

```http
GET /api/health
GET /actuator/health
```

### Documents

```http
POST /api/documents/upload
GET /api/documents
```

### AI

```http
POST /api/ai/ask
POST /api/ai/search
POST /api/ai/index/{documentId}
```

### Chat

```http
POST /api/chat
```

The chat endpoint requires JWT authentication.

---

## ⚙️ Configuration

Create a local `.env` file for secrets and environment-specific configuration.

Example:

```env
POSTGRES_PASSWORD=your_password
```

Do **not** commit `.env` to Git.

The application uses environment variables for container-specific configuration such as:

```text
PostgreSQL
Redis
Ollama
```

---

## ▶️ Running Locally

### Prerequisites

Install:

- Java 21
- PostgreSQL
- Redis / Memurai
- Ollama
- Docker Desktop

### Start Ollama models

```bash
ollama pull llama3.2:3b
ollama pull nomic-embed-text
```

### Start the application

```powershell
.\mvnw.cmd spring-boot:run
```

Application:

```text
http://localhost:8080
```

Health:

```text
http://localhost:8080/api/health
```

---

## 🐳 Running with Docker Compose

Build the application:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Build the image:

```powershell
docker build -t workmate-ai:latest .
```

Start:

```powershell
docker compose up -d
```

Check:

```powershell
docker compose ps
```

Health:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

---

## 🔒 Security Notes

- Passwords are stored using BCrypt hashing.
- JWT authentication is stateless.
- Secrets are kept outside source control.
- `.env` is excluded using `.gitignore`.
- Production deployments should use a dedicated secret-management solution.
- JWT signing secrets should be rotated and stored securely in production.

---

## 📈 Future Improvements

Potential next improvements include:

- Role-based access control
- Admin dashboard
- Document versioning
- Better semantic chunking
- Streaming AI responses
- Conversation title generation
- Advanced source citation UI
- Rate limiting
- Centralized logging
- OpenTelemetry tracing
- Cloud deployment
- Managed PostgreSQL and Redis
- Production-grade secret management
- Automated deployment after successful CI

---

## 🎯 Project Highlights

WorkMate AI demonstrates practical experience with:

- Java 21
- Spring Boot
- Spring Security
- JWT
- REST APIs
- PostgreSQL
- JPA / Hibernate
- Redis
- Vector databases
- Embeddings
- RAG
- LLM integration
- Docker
- Docker Compose
- Automated testing
- GitHub Actions
- GitHub Container Registry
- Application health monitoring

---

## 👩‍💻 Author

**Shubhi Chandra**

Java Backend Developer

Built with Java, Spring Boot, PostgreSQL, Redis, RAG, Ollama, Docker, and GitHub Actions.
