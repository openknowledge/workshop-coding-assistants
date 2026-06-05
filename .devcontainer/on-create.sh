#!/usr/bin/env bash
set -euo pipefail

echo "[devcontainer] Installing OpenCode CLI…"
# Official installer: https://opencode.ai/docs
curl -fsSL https://opencode.ai/install | bash

# Make opencode available in non-login shells
if ! grep -q 'opencode/bin' "$HOME/.bashrc" 2>/dev/null; then
  echo 'export PATH="$HOME/.opencode/bin:$PATH"' >> "$HOME/.bashrc"
fi
export PATH="$HOME/.opencode/bin:$PATH"

echo "[devcontainer] OpenCode version:"
opencode --version || true

# Pre-warm Maven dependencies so the first server start is fast
if [ -f customer-manager-server/pom.xml ]; then
  echo "[devcontainer] Pre-fetching Maven dependencies…"
  (cd customer-manager-server && ./mvnw -B -q -DskipTests dependency:go-offline || true)
fi

# Pre-install npm dependencies for the client
if [ -f customer-manager-client/package.json ]; then
  echo "[devcontainer] Installing client dependencies…"
  (cd customer-manager-client && npm ci || npm install || true)
fi

echo "[devcontainer] onCreate done."
