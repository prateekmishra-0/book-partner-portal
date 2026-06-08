# 📚 Book Partner Portal — Backend API

> A production-grade **Spring Data REST** API for a book publishing management system, built on the Microsoft `pubs` sample database schema. Designed as a fully decoupled backend capable of serving multiple independent clients simultaneously.

---

## 🗺 System Architecture

```
Browser
   │
   ▼
Frontend Client (Spring MVC + Thymeleaf) ── port 8081
   │
   │  HTTP REST  +  X-Project-Secret header
   ▼
Backend API (Spring Data REST + HATEOAS) ── port 8080
   │
   ▼
MySQL 8.0 Database (pubs schema)
```

The backend is completely unaware of the frontend. It exposes HATEOAS endpoints that any client — web, mobile, or microservice — can consume. The frontend is one such client; it communicates exclusively via HTTP and a shared secret header.

**Why two separate JARs?**  
- Replace or scale the frontend without touching backend logic  
- The backend can serve multiple clients simultaneously  
- Enforces true separation of concerns at the deployment level  

---

## 🏗 Architecture & Design Patterns

- **Microservices-Inspired:** Runs as a standalone Spring Boot JAR on port `8080`, communicating with a separate frontend module via HTTP REST calls. The backend has zero knowledge of the frontend's existence.

- **HATEOAS Implementation:** Built on Spring Data REST, which auto-generates HAL+JSON endpoints from JPA repository interfaces — no controller code needed. Every response includes `_links` to related resources, enforcing hypermedia-driven navigation.

- **Decoupled Dual-ID Pattern (Read-Only Shield):** For every `@ManyToOne` relationship (e.g. `Employee→Job`, `Title→Publisher`), two fields map to the same column:
  - A plain `@Column` field handles **writes** (client sends `{"pubId": "1389"}`)
  - A `@ManyToOne(fetch = FetchType.LAZY)` with `insertable=false, updatable=false` handles **reads** via JOIN — but is physically prevented from ever mutating the FK column
  
  This pattern prevents accidental relationship mutations while still allowing full navigation for reads.

- **Composite Key Handling:** Entities with composite primary keys (`Sale`, `TitleAuthor`) use `@EmbeddedId` with a dedicated `@Embeddable` key class. Custom `BackendIdConverter` implementations translate between URL-friendly strings and Java ID objects:
  - `SaleId` → `/api/sales/7131,ORD123,BU1032`
  - `TitleAuthorId` → `/api/titleAuthors/123-45-6789_BU1032`

---

## 🗃 Database Schema

Tables: `authors`, `publishers`, `titles`, `titleauthor` (join table), `stores`, `sales`, `jobs`, `employee`, `roysched`, `discounts`

Key relationships:
- `Publisher` → `Titles` (one-to-many)
- `Author` ↔ `Title` (many-to-many via `titleauthor`)
- `Store` ↔ `Title` (many-to-many via `sales` — composite key)
- `Employee` → `Job` (many-to-one, lazy)
- `Employee` → `Publisher` (many-to-one, lazy)

---

## 🛡 Security Implementation

Security is enforced at the **lowest possible level** — the Servlet Filter Chain — before Spring's `DispatcherServlet` or Spring Security ever processes the request.

- **`SecretKeyFilter`:** A custom `jakarta.servlet.Filter` annotated with `@Order(1)`, ensuring it runs as the very first gate. It validates a mandatory `X-Project-Secret` header on every incoming request. Missing or incorrect header → instant `403 Forbidden`, request dropped.

- **Pre-DispatcherServlet enforcement:** Filters run at the Tomcat (Servlet container) level — before Spring Security, before routing, before any controller code. This means even a misconfigured Spring Security rule cannot expose the backend.

- **CSRF disabled:** This is a stateless machine-to-machine REST API. REST clients (`RestClient`, mobile apps) don't use browser session cookies and don't send CSRF tokens. CSRF protection is explicitly disabled in `SecurityConfig` so that POST/PUT/DELETE requests work correctly without form-based tokens. Spring Security is present on the classpath only to configure `permitAll()` — the custom filter handles actual security.

- **`ddl-auto=none`:** Hibernate is explicitly configured to never alter or create tables. Schema changes are manual and controlled — Hibernate has zero DDL authority.

---

## 💾 Data Integrity & Soft Deletion

Physical `DELETE` statements are completely blocked. All deletions are soft-deletes, preserving full audit trails and referential integrity.

- **`@SQLDelete`:** Overrides Hibernate's DELETE SQL. When `repository.deleteById()` is called, Hibernate intercepts and executes `UPDATE authors SET is_active = false WHERE au_id = ?` instead of a `DELETE`.

- **`@SQLRestriction`:** Automatically appends `WHERE is_active = true` to every `SELECT` query at the Hibernate level — soft-deleted records are invisible to all queries without any manual filtering.

- **`@JsonIgnore` on `isActive`:** The soft-delete flag is never exposed in API responses and cannot be set by a client via JSON payload.

- **`updatable=false` on primary keys:** Once created, no primary key can ever be changed. Enforced at the Hibernate/JPA column level.

- **`updatable=false` on `pubdate`:** Once a book is published, the publication date is locked — silently ignored in any PUT/PATCH body.

---

## 🚦 Event Handlers & Security Guards

Spring Data REST bypasses the traditional controller → service layer, routing directly to the repository. Business logic is enforced via `@RepositoryEventHandler`, hooking into the persistence lifecycle before any database operation. Every mutation passes through four strict guards:

1. **Upsert Hack Defense** (`@HandleBeforeCreate`): Blocks `POST` requests that include an ID belonging to an existing record. Returns `409 Conflict` — prevents silent overwrites of live data.

2. **Ghost Insert Defense** (`@HandleBeforeSave`): Blocks `PUT`/`PATCH` requests targeting a non-existent ID. Returns `404 Not Found` — prevents phantom record creation.

3. **API Safety Bouncer** (`@HandleBeforeCreate`): Programmatically forces `isActive = true` on every new entity, regardless of what the client sends. A client cannot pre-delete a record via the creation payload.

4. **PATCH Null-Safety** (`@HandleBeforeSave`): If a `PATCH` merge results in a `null` `isActive` value, it is defaulted to `true` — prevents database constraint violations from partial updates.

---

## ✅ Bean Validation

Spring Data REST does **not** automatically run Jakarta Bean Validation (`@NotBlank`, `@Pattern`, `@Size`, etc.) on save or create operations. Without explicit wiring, all validation annotations are silently ignored.

**`RestValidationConfig`** solves this by:
1. Creating a `LocalValidatorFactoryBean` — Spring's wrapper around Jakarta Validation's `ValidatorFactory`
2. Registering it into `ValidatingRepositoryEventListener` for both `beforeCreate` (POST) and `beforeSave` (PUT/PATCH) events

Validation errors surface as `RepositoryConstraintViolationException` → caught by `GlobalExceptionHandler` → `400 Bad Request` with a structured error body.

---

## 🔍 Projections & Payload Reduction

`@Projection` interfaces define controlled subsets of entity fields returned in API responses. `excerptProjection` applies the projection automatically to collection responses (`GET /api/authors`), reducing payload size and preventing over-fetching.

Projections are registered manually in `RestConfig` (implements `RepositoryRestConfigurer`) because Spring Data REST only auto-discovers projections in the same package as the entity — the project uses a dedicated `projection` package.

---

## 🧪 Testing & CI/CD Pipeline

- **`MockMvc` + `@SpringBootTest`:** Full integration tests that simulate real HTTP requests against the Spring context without starting a network server.

- **`@DataJpaTest`:** Slice tests for repository-layer behavior with an in-memory database — isolated from web layer concerns.

- **`@Transactional` rollback:** Every test wraps its database operations in a transaction that rolls back after completion. Zero side effects between tests; each starts from an identical, clean state. `saveAndFlush()` is used where constraint violations must be triggered immediately rather than at commit.

- **GitHub Actions CI/CD (`pr-check.yml`):**
  - Triggers on every pull request to `main`
  - Spins up a real **MySQL 8.0 Docker container** in the CI runner — tests run against a real database engine, not H2
  - Executes `mvn clean test`
  - Publishes unit test reports as pipeline artifacts
  - **Blocks merge** if any test fails — no broken code reaches `main`

---

## 🔁 Full Request Journey

**Example: `GET /api/authors` (fetching paginated authors)**

```
Frontend (port 8081)
  └─ RestClient: GET http://localhost:8080/api/authors?page=0&size=5
       + Header: X-Project-Secret: <secret>

Backend (port 8080)
  └─ SecretKeyFilter (@Order 1) ── validates header ✓
  └─ Spring Security ── permitAll() ✓
  └─ Spring Data REST ── intercepts /api/authors
  └─ AuthorRepository.findAll(Pageable)
       └─ Hibernate SQL: SELECT * FROM authors WHERE is_active = true LIMIT 5
  └─ Response: HAL+JSON with _embedded.authors + _links + page metadata

Frontend
  └─ ObjectMapper parses _embedded.authors → List<AuthorDto>
  └─ ObjectMapper parses _links.self.href → extracts entity IDs
  └─ ObjectMapper parses page → PageMetadata
  └─ Thymeleaf renders authors-page.html → HTML sent to browser
```

**Example: `POST /api/authors` (creating an author)**

```
HTTP POST /api/authors { JSON body }
  └─ SecretKeyFilter ── validates X-Project-Secret ✓
  └─ RestValidationConfig ── runs @NotBlank, @Pattern, @Size validators
       └─ Invalid → RepositoryConstraintViolationException → 400 Bad Request
  └─ AuthorEventHandler @HandleBeforeCreate
       └─ Guard 1: ID already exists? → 409 Conflict
       └─ Guard 3: force isActive = true
  └─ AuthorRepository.save() → Hibernate INSERT
  └─ Response: 201 Created + saved entity body
```

---

## ⚙ Key Configuration (`application.properties`)

| Property | Value | Reason |
|---|---|---|
| `spring.data.rest.base-path` | `/api` | All endpoints under `/api` |
| `spring.jpa.hibernate.ddl-auto` | `none` | Hibernate never alters schema |
| `spring.data.rest.return-body-on-create` | `true` | POST returns saved entity (default is empty 201) |
| `spring.data.rest.return-body-on-update` | `true` | PUT/PATCH returns updated entity |

---

## 📦 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 (Streams, Lambdas, NIO.2) |
| Framework | Spring Boot, Spring Data REST, Spring Security |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8.0 |
| Validation | Jakarta Bean Validation (JSR-380) |
| Testing | JUnit 5, MockMvc, `@DataJpaTest` |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven, GitHub Actions |
| Utilities | Lombok, Jackson |
