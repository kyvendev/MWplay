# Upstream compatibility

MW Play is being developed as a visual/product layer over the existing open-source client rather than as a rewrite of its streaming core.

For easier maintenance:

- Keep upstream copyright/license notices intact.
- Prefer MW-specific styles/assets in separate files where practical.
- Avoid renaming internal `stremio` module aliases solely for branding; those are implementation details and changing them creates unnecessary merge risk.
- Keep account, addon protocol, playback, casting, subtitles and synchronization behavior unchanged unless a product change specifically requires otherwise.
- Review upstream security and compatibility fixes periodically before integrating them.
