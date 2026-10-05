# MW Play security notes

- Do not hard-code account credentials, access tokens or paid-service credentials in the client or repository.
- Keep user authentication handled by the existing account flow until a separate authorized backend is intentionally designed.
- Treat third-party addons as external services; availability and security are outside the client project's control.
- Do not bundle unauthorized content sources into distributable builds.
- Keep dependencies and upstream security fixes under review.
