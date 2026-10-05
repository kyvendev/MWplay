# MW Play redesign changelog

## Foundation pass

- Created isolated `feat/mw-play-redesign` branch from upstream-based `main`.
- Changed browser/app metadata to MW Play.
- Added MW Play purple-to-blue visual tokens and cinematic background treatment.
- Added custom MW Play horizontal logo and square app mark as SVG assets.
- Added a separate visual-layer stylesheet to reduce conflicts with future upstream merges.
- Preserved the upstream Stremio core dependencies, addon architecture, player stack and account code in this pass.
- Added a build-validation workflow for the redesign branch.
- Added UI roadmap for the next home, details, player and TV passes.

No changes in this pass intentionally configure or bundle third-party content sources.
