# Testing the MW Play redesign

Checkout the redesign branch and install dependencies:

```bash
git checkout feat/mw-play-redesign
pnpm install --frozen-lockfile
pnpm start
```

For a production validation:

```bash
pnpm build
```

## First-pass checks

1. Browser title reads **MW Play — Seu mundo em streaming**.
2. The application keeps loading the existing account, library, addon and player architecture.
3. Main surfaces use the darker MW Play palette with purple/blue accents.
4. Mobile viewport remains responsive.
5. Keyboard/remote focus and player controls remain functional.

The `main` branch remains untouched until the redesign is reviewed and merged.
