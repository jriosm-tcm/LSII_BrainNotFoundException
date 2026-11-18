# Sprint 6 — implementation and review

User request: "para el siguiente sprint puedes añadir las funcionalidades que habias dicho?"

Context: proposed Sprint 6 includes comment replies and likes, video analytics, persistent PostgreSQL, Docker Compose, E2E tests and CI. User previously asked for minimal unnecessary AI commentary in source code. Keep clear AI-assistance records as required by course instructions.

Changes authored with AI assistance:
- Comment replies and like toggles (Spring Data JPA) and React controls
- Basic view and comment counts; view events counted when opening a video
- Persistent local H2 database for development, PostgreSQL service for Docker Compose
- Backend Dockerfile and GitHub Actions backend/frontend build workflow

Limitations:
- This is not a verified release; compile, unit/integration tests, browser tests and Docker startup remain to be run.
- E2E tests are not yet implemented.
- CI workflow has not been observed running successfully.
- Local HTTP Basic authentication is not appropriate for deployment over unencrypted HTTP.
- View counts are opens, not unique viewers or verified video playback.
- PostgreSQL setup and Docker deployment need manual verification.
- Team should review AI-assisted changes and ensure course documentation requirements are met.
