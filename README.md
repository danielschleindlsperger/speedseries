# speedseries

Timeseries dashboard for internet speedtests.

## Development

`speederies` is implemented in [ClojureScript](https://clojurescript.org) and runs on [Node.js](https://nodejs.org/en).
Data is stored in SQLite. Database migrations are run by [goose](https://github.com/pressly/goose).

### Requirements

- Node.js >= 14
- Clojure
- JVM >= 8
- Docker for builds

### Getting started

1. Creat an empty database: `touch db.sqlite3`
2. Install npm dependencies: `npm ci`
3. Start shadow-cljs server: `npm run watch`
4. After the first compilation, run the app: `npm start`

### Database

```sh
# Help
npm run db
# Run migrations
npm run db up
# Reverse all migrations
npm run db reset
# Reverse by one migration
npm run db down
```

### Building and Publishing

```sh
# Build both server and client bundle
npm run build
# Run it 
node target/server.js
```

TODO: Docker

## Roadmap
- Configurable schedule
- Option to set a fixed server to use for speedtests
- Allow time periods to be marked with labels
  - Use case: Mark a period where a different ISP was used
  - Use case: Mark a period where for example an LTE router was placed differently
