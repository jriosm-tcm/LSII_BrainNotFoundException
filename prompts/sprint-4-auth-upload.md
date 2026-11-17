# Sprint 4 — AI assistance record (proposed; team review required)

User request: "vale pues haz el sprint" following a discussion of Sprint 4 for ProTube.

Scope agreed in conversation:
- US-01: register users with email and password
- US-02: login with backend authentication
- US-06: authenticated MP4 upload with metadata
- add focused automated tests where possible

Implementation choices made by AI (not yet team-approved):
- Spring Security stateless HTTP Basic for a local MVP, BCrypt hashes, JPA user entity
- frontend holds Basic credentials only in React memory, clears them on logout/refresh
- authenticated upload stores MP4 and JSON metadata in the existing configured store
- dev H2 database remains; production PostgreSQL configuration already existed
- no claim of full MVP security, Docker, CI, or test coverage compliance

Required review:
1. Team approves AI configuration and this prompt/approach per README/REQUIREMENT.md.
2. Run Maven tests, frontend TypeScript build, and manual registration/login/upload checks.
3. Review authentication design before production use; HTTP Basic requires HTTPS.
4. Decide persistent PostgreSQL setup for development and delivery.
5. Review upload size limits, file validation and cleanup behavior.

All related commits explicitly include [AI-assisted].
