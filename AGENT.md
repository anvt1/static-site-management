# AGENT.md — Static Site Manager

## Project Overview

A Spring Boot 3 web application for managing and deploying static websites. Users upload a ZIP archive, preview it, then publish. An admin can approve payments. The UI shell uses ZK Framework (`.zul` files) MVVM pattern.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.x (Web, Security, Data JPA, Validation) |
| UI shell | ZK Framework 10.0 (`.zul` pages) |
| Database | PostgreSQL 16 (prod) / H2 (dev profile) |
| Build | Maven 3.9 |
| Container | Docker + Docker Compose |
| Utilities | Lombok, Apache Commons Compress |

## Project Layout

```
src/main/java/com/atvo/ssm/
  config/          # Spring Security & ZK config
  model/           # JPA entities (UserAccount, Site, Deployment, DomainMapping, PaymentTransaction)
  repo/            # Spring Data repositories
  service/         # Business logic (ZipDeployService, StoragePaths, CurrentUserService, BootstrapAdmin)
  vm/              # ZK ViewModels — MVVM pattern (IndexVM, …)
  web/             # REST controllers (SiteController, AuthController, AdminController, PaymentController)

src/main/resources/
  application.yml            # Default (PostgreSQL)
  application-h2.yml         # Dev profile (H2 in-memory)
  web/zul/
    index.zhtml              # ZK shell page (ZHTML + ZUL mix)
    layout/                  # Shared layout fragments (header, footer)
    pages/                   # Per-page ZUL fragments (home, sites, payments, admin)
```

## Running Locally

### With Docker (recommended)

```bash
docker compose up --build
```

App available at: `http://localhost:8080/index.zul`

### Without Docker (dev profile)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

No external database needed — H2 is used automatically.

## Key Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/ssm` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `ssm` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `ssm` | DB password |
| `SSM_STORAGE_BASEDIR` | `data` | Root folder for uploaded/extracted site files |
| `SSM_BOOTSTRAP_ADMINEMAIL` | `admin@example.com` | Auto-created admin email on first boot |
| `SSM_BOOTSTRAP_ADMINPASSWORD` | `admin` | Auto-created admin password on first boot |

## REST API Reference

### Auth

| Method | Path | Body / Params | Auth |
|---|---|---|---|
| POST | `/register` | `{email, password}` | None |

### Sites (requires login — HTTP Basic)

| Method | Path | Body / Params | Description |
|---|---|---|---|
| GET | `/api/sites` | — | List own sites |
| POST | `/api/sites` | `{name}` | Create a site |
| GET | `/api/sites/{siteId}/deployments` | — | List deployments |
| POST | `/api/sites/{siteId}/deployments/preview` | `file=<zip>` (multipart) | Upload ZIP preview |
| POST | `/api/sites/{siteId}/deployments/{deploymentId}/publish` | — | Publish a preview |

### Payments

| Method | Path | Body / Params | Description |
|---|---|---|---|
| POST | `/api/payments` | `{amountVnd}` | Create payment request |
| POST | `/api/payments/{txId}/submit` | `receipt` (multipart) + `paymentReference` | Submit proof |

### Admin (`ROLE_ADMIN` only)

| Method | Path | Description |
|---|---|---|
| GET | `/api/admin/payments/pending` | List pending payments |
| POST | `/api/admin/payments/{txId}/approve` | Approve a payment |

## Upload Limits

Configured in `application.yml` under `ssm.upload`:

- `maxZipBytes`: 50 MB
- `maxFileCount`: 5 000 files
- `maxExtractedBytes`: 200 MB

## File Storage

Sites are stored under `SSM_STORAGE_BASEDIR` using `StoragePaths`. Each site gets a directory keyed by its ID; each deployment gets a subdirectory. Published deployments are served statically from the `live/` sub-path.

## Development Notes

- **Lombok** is used extensively — make sure your IDE has the Lombok plugin enabled.
- **DDL** is `update` (auto-migrate on startup). Do not use this in production without review.
- The UI follows the **ZK MVVM pattern**. Each page (`*.zul`) declares a ViewModel via `viewModel="@id('vm') @init('...')"`. The ViewModel calls the service layer directly — no REST calls from the frontend.
- **ZK data binding** uses `@load`, `@save`, `@bind`, and `@command` annotations on ZUL components; ViewModels use `@Init`, `@Command`, and `@NotifyChange` from `org.zkoss.bind.annotation`.
- **Authentication** uses ZK form-based login with a Spring Security session. HTTP Basic is only used for the REST endpoints under `/api/**`.
- `BootstrapAdmin` runs on startup and seeds the admin account if it does not exist.

## Build

```bash
./mvnw -DskipTests package
```

The fat JAR is produced at `target/static-site-manager-0.0.1-SNAPSHOT.jar`.

## Tests

```bash
./mvnw test
```

Tests use the `h2` profile automatically (see `application-h2.yml`).
