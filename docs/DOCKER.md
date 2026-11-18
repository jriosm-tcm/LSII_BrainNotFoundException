# Running ProTube with Docker

Prerequisite: Docker Engine + Compose plugin, or Docker Desktop.

1. Clone the repository and switch to the `Alexis` branch.
2. Copy `.env.example` to `.env` and replace `POSTGRES_PASSWORD` with a strong password.
3. From the repository root run:

```bash
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

4. Open http://localhost:3000. Requests to `/api/**` and `/media/**` go through Nginx to the backend. Backend port 8080 is internal to the Compose network.

Stop: `docker compose down`. Persistent PostgreSQL data and videos remain in Docker volumes. **Do not run `docker compose down -v` unless you intend to delete them.**

For a clean build: `docker compose build --no-cache`.

The Docker deployment has not yet been verified end-to-end on a fresh machine. Check backend startup, user registration, video upload/playback, and persistence after restart.

## Notes

- Docker Compose is intended for local/demo use. Configure TLS, secure secrets, migrations, backups and a proper authentication mechanism before public deployment.
- Videos stored in the Docker volume are not automatically imported from a developer's local video directory.
- PostgreSQL and the backend are not published on host ports by default.
