# ProteaHealth admin API (PHP 8 + PDO)

Drop-in replacement for the Java servlets. Same endpoints and JSON output:

| Endpoint          | Methods                                  |
|-------------------|------------------------------------------|
| /api/questions    | GET list; POST action=delete&id=         |
| /api/answers      | GET list; POST action=delete&id=         |
| /api/tracking     | GET (optional ?patient_id=)              |
| /api/access       | GET ?role=; POST role=&id=&action=allow|block |

## Setup
Requires PHP 8.0+ with pdo_mysql. Set environment variables (Apache `SetEnv`, php-fpm `env[]`, or `.env` via your host):

    DB_HOST=localhost  DB_NAME=proteahealth  DB_USER=...  DB_PASS=...
    ADMIN_TOKEN=<long random string>   # e.g. php -r "echo bin2hex(random_bytes(32));"
    APP_DEBUG=0

Call with `Authorization: Bearer <ADMIN_TOKEN>` (or `X-Admin-Token`).
Lists accept `?limit=` (max 1000) and `?offset=`.

## Changes from the Java version
- Credentials moved out of code (env vars); use a least-privilege MySQL user, not root.
- All endpoints now require an admin token (previously open to anyone).
- Raw SQL error messages no longer sent to clients; logged server-side.
- Anonymous questions no longer leak `patient_id`.
- Only GET/POST allowed (405 otherwise); strict integer ID validation.
- Pagination added; tracking counts use joins instead of per-row subqueries.
- Security headers, no-store caching, directory listing and source files blocked.
- Put the site behind HTTPS; the token travels in a header.
