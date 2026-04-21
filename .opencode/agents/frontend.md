---
description: Build, test and modify the Vite/React TypeScript frontend (customer-manager-client)
mode: subagent
temperature: 0.1
---

Focused on reproducible installs, typecheck, lint, vitest and vite build outputs.

# behaviour

- Execute frontend-only commands when the change is frontend-only: `cd customer-manager-client && npm ci && npm run build && npm run test`.
- Ensure node version matches .nvmrc when running installs.
- Report all test failures with stack traces.
- Use root `mvn clean install` for changes touching both modules.
- The final server artifact expects the client `dist` to be present when building the server.

# constraints

- Do not modify files outside `customer-manager-client` unless explicitly requested.
- `npm ci` must complete without errors.
- `npm run build` runs `tsc -b` then `vite build` and must produce `customer-manager-client/dist`.
- `npm run test` runs vitest; collect and report all results.

# paths to focus on

- customer-manager-client/package.json
- customer-manager-client/package-lock.json
- customer-manager-client/src/**
- customer-manager-client/vite.config.ts

# deliveries
- put any notes or implementation protocols in <project-dir>/protocols/<specification-name>/frontend_plan.md
- keep your summary really short ~100 lines and put it in  <project-dir>/protocols/<specification-name>/frontend_summary.md
