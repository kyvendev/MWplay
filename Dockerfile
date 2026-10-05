# MW Play Web
# Keep the runtime aligned with this Stremio Web snapshot.
ARG NODE_VERSION=20-alpine
FROM node:$NODE_VERSION AS base

ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"

RUN apk add --no-cache git
# This repository snapshot has a pnpm v9 lockfile containing an upstream
# GitHub release tarball without an integrity field. pnpm 11+ intentionally
# rejects such legacy entries. Pin pnpm 9 for reproducible compatibility with
# the lockfile instead of weakening pnpm 11's supply-chain protections.
RUN corepack enable && corepack prepare pnpm@9.15.9 --activate

LABEL Description="MW Play Web" Vendor="MW Play" Version="1.0.0"

RUN mkdir -p /var/www/stremio-web
WORKDIR /var/www/stremio-web

FROM base AS app

COPY package.json pnpm-lock.yaml /var/www/stremio-web/
RUN pnpm i --frozen-lockfile

COPY . /var/www/stremio-web
RUN pnpm build

FROM base AS server

RUN pnpm i express@4

FROM base

COPY http_server.js /var/www/stremio-web
COPY --from=server /var/www/stremio-web/node_modules /var/www/stremio-web/node_modules
COPY --from=app /var/www/stremio-web/build /var/www/stremio-web/build

EXPOSE 8080
CMD ["node", "http_server.js"]
