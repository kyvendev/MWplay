# Branding replacement TODO

Visible branding replacement should be done component-by-component after build validation. Search targets include `Stremio`, upstream logo/image imports, PWA manifest strings, favicon assets and any account/about screens that intentionally identify the upstream project.

Do not blindly rename internal package imports such as `stremio/*` or upstream dependency package names: those are code identifiers, not customer-facing branding.
