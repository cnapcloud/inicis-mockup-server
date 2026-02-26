# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

KG 이니시스 간편인증 Mock Server — a test simulator for Keycloak integration with KG Inicis identity verification. Built with Spring Boot 3.2, Java 17, Thymeleaf, and Lombok. Runs on port 9090.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run (two ways)
mvn spring-boot:run
java -jar target/inicis-mock-server-1.0.0.jar

# Run tests
mvn test

# Docker
docker build -t inicis-mock-server .
docker run -p 9090:9090 inicis-mock-server
```

## Architecture

Base package: `com.example.inicis.mock`

- **controller/InicisMockController** — Single controller handling all HTTP endpoints:
  - `GET /` — Home page
  - `GET /auth` — Auth page (Keycloak redirects here with `mid`, `returnUrl`, `reqSvcCd`, etc.)
  - `POST /auth/process` — Processes user's success/failure selection, then POSTs result back to Keycloak's callback URL via `RestTemplate`
- **service/MockAuthService** — Generates success/failure `AuthResponse` objects. Produces SHA-256-based CI values. Contains `TestUsers` inner class with 5 hardcoded test users.
- **config/MockProperties** — `@ConfigurationProperties(prefix = "inicis.mock")` binding for `validMids`, `autoSuccess`, `authDelayMs`.
- **dto/** — `AuthRequest` (inbound params) and `AuthResponse` (outbound result with `@Builder`).
- **templates/** — Thymeleaf views: `auth.html` (auth selection UI), `index.html` (home), `error.html`.

## Auth Flow

1. Keycloak redirects user to `GET /auth` with merchant/callback params
2. If `autoSuccess=true`, picks a random test user and immediately POSTs success to callback
3. Otherwise, renders `auth.html` for manual user/result selection
4. On form submit, `POST /auth/process` generates response and POSTs to Keycloak's `returnUrl`
5. Controller follows redirect from Keycloak's response

## Configuration

Key properties in `application.yml` under `inicis.mock`:
- `valid-mids` — allowed merchant IDs (empty = allow all)
- `auto-success` — skip UI, always succeed (default: false)
- `auth-delay-ms` — simulated auth delay in ms (default: 2000)

## Language

Code comments and UI text are in Korean. Maintain this convention.
