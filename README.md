# 📚 Book Partner Portal (Backend API)

This repository contains the backend module for a microservices-inspired book publishing management system. It is built as a highly secure, scalable Spring Data REST API that exposes the database as RESTful HATEOAS endpoints. 

This backend is designed to be completely decoupled from the frontend, meaning it is capable of serving multiple clients (such as web, mobile, or other microservices) simultaneously.

## 🏗 Architecture & Design Patterns

*   **Microservices-Inspired:** Acts as a standalone backend running on port 8080, communicating with a separate frontend module via HTTP REST calls.
*   **HATEOAS Implementation:** Built on Spring Data REST, the API automatically wraps responses in HAL+JSON format, providing `_links` to related resources to enforce hypermedia-driven navigation.
*   **Decoupled Dual-ID Pattern:** Employs a Read-Only Shield pattern for relationships. For example, a raw `@Column` handles writes, while an un-insertable/un-updatable `@ManyToOne` annotation safely manages JOINs without risking accidental mutations of related entities.
*   **Composite Key Handling:** Implements custom `BackendIdConverter` classes to translate complex, embedded composite keys (like `SaleId`) into URL-friendly strings for RESTful routing.

## 🛡 Security Implementation

Instead of relying solely on standard framework configurations, security is enforced at the lowest possible level: the Servlet Filter Chain.

*   **`SecretKeyFilter`:** A custom `jakarta.servlet.Filter` annotated with `@Order(1)` ensures it runs as the very first check, before Spring's `DispatcherServlet` or Spring Security.
*   **Pre-Routing Authorization:** The filter intercepts all incoming traffic to validate a mandatory `X-Project-Secret` header. If the header is missing or incorrect, it instantly drops the request with a `403 Forbidden` status, protecting the server from unauthorized external access.
*   **Stateless Operations:** Because this is a machine-to-machine REST API, CSRF protection is explicitly disabled to allow stateless token/header-based communication.

## 💾 Data Integrity & Soft Deletion

To maintain a strict audit trail and preserve referential integrity, physical deletions are completely restricted.

*   **Hibernate `@SQLDelete`:** Overrides default JPA delete operations. When a delete request is received, Hibernate intercepts it and executes an `UPDATE` statement to set an `is_active` flag to `false`.
*   **Hibernate `@SQLRestriction`:** Automatically appends `is_active = true` to every `SELECT` query, ensuring that soft-deleted records are completely invisible at the ORM level.
*   **API Obfuscation:** The `isActive` flag is annotated with `@JsonIgnore` to guarantee it is never exposed in API responses or manipulated via client JSON payloads.

## 🚦 Event Handlers & Security Guards

Spring Data REST bypasses traditional service layers, so business logic and advanced security checks are enforced using `@RepositoryEventHandler` to hook directly into the persistence lifecycle. Every API mutation is forced through four strict guards:

1.  **Upsert Hack Defense (`@HandleBeforeCreate`):** Blocks POST requests that include an existing ID, throwing a `409 Conflict` to prevent silent overwrites of existing data.
2.  **Ghost Insert Defense (`@HandleBeforeSave`):** Prevents PUT/PATCH requests to non-existent IDs, throwing a `404 Not Found` to stop phantom record creation.
3.  **API Safety Bouncer:** Programmatically forces `isActive = true` upon entity creation, ensuring no client can maliciously pre-delete a record via the API.
4.  **PATCH Null-Safety:** Intercepts partial updates. If a PATCH merge results in a null `isActive` value, it defaults it to `true` to prevent database constraint violations.

## 🧪 Testing & CI/CD Pipeline

*   **Test Isolation:** API tests utilize `@SpringBootTest` and `MockMvc`, wrapped in `@Transactional` to ensure a completely clean database state before and after each test.
*   **GitHub Actions:** Configured with a robust CI/CD pipeline (`pr-check.yml`) that triggers on pull requests. It spins up a MySQL 8.0 Docker container, executes the `mvn clean test` suite, publishes unit test reports, and blocks merges if any tests fail.
