# ProTube REST API (current implementation)

Base URL: `http://localhost:8080/api`. This is a code inventory, not an assertion that every endpoint has passed integration tests.

| Method | Path | Authentication | Purpose |
|---|---|---|---|
| POST | /auth/register | Public | Register email and password |
| GET | /auth/me | HTTP Basic | Verify current account |
| GET | /videos | Public | Catalogue |
| GET | /videos/search?q= | Public | Backend search |
| POST | /videos/upload | HTTP Basic | Upload MP4 and metadata |
| PUT | /videos/{id}/metadata | HTTP Basic + owner | Update metadata |
| GET | /videos/{id}/comments | Public | Read comments |
| POST | /videos/{id}/comments | HTTP Basic | Publish comment |
| POST | /videos/{id}/comments/{commentId}/replies | HTTP Basic | Reply |
| GET | /videos/{id}/comments/{commentId}/likes | Public | Like count |
| POST | /videos/{id}/comments/{commentId}/likes | HTTP Basic | Toggle like |
| GET | /videos/{id}/stats | Public | View and comment counts |
| POST | /videos/{id}/stats/view | Public | Count an opening |
| GET | /profile/me | HTTP Basic | Read own profile |
| PUT | /profile/me | HTTP Basic | Update own profile |
| GET | /profile/{id} | Public | Public profile (verify exact mapping) |

Media resources use `/media/**`, not `/api/**`.

Frontend API clients live in `frontend/src/api`. UI components must not implement backend authorization or database operations.

## Verification checklist
- Compare this inventory against all Spring `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping` declarations before treating it as exhaustive.
- Test success, unauthorized, forbidden, validation failure, and missing resource responses.
- Verify authentication, ownership, persistent storage, Docker startup and CI.
- Do not expose HTTP Basic credentials over unencrypted public connections.
