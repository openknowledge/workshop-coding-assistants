#!/usr/bin/env bash
set -euo pipefail

export PATH="$HOME/.opencode/bin:$PATH"

LOG_DIR="/tmp/opencode"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/server.log"
PID_FILE="$LOG_DIR/server.pid"

# If a previous server is still running, don't start a second one.
if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "[devcontainer] OpenCode server already running (pid $(cat "$PID_FILE"))."
  exit 0
fi

HOSTNAME_BIND="${OPENCODE_HOSTNAME:-0.0.0.0}"
PORT_BIND="${OPENCODE_PORT:-4096}"

# Surface auth state. If neither a known provider env var nor an opencode auth
# entry exists, the server still starts — users can `opencode auth login`
# inside the container later.
if [ -z "${ANTHROPIC_API_KEY:-}" ] \
  && [ -z "${OPENAI_API_KEY:-}" ] \
  && [ -z "${OPENROUTER_API_KEY:-}" ] \
  && [ -z "${GROQ_API_KEY:-}" ] \
  && [ ! -f "$HOME/.local/share/opencode/auth.json" ]; then
  echo "[devcontainer] WARNING: no LLM provider credentials detected."
  echo "                Set ANTHROPIC_API_KEY (or another provider) via Codespaces"
  echo "                secrets, or run 'opencode auth login' inside the container."
fi

echo "[devcontainer] Starting OpenCode server on ${HOSTNAME_BIND}:${PORT_BIND}…"
nohup opencode serve \
  --hostname "$HOSTNAME_BIND" \
  --port "$PORT_BIND" \
  >> "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

sleep 1
if kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "[devcontainer] OpenCode server started (pid $(cat "$PID_FILE"))."
  echo "[devcontainer] Logs: $LOG_FILE"
else
  echo "[devcontainer] OpenCode server failed to start. Last log lines:"
  tail -n 20 "$LOG_FILE" || true
  exit 1
fi

# In GitHub Codespaces, force the forwarded port to public. The declarative
# `portsAttributes.visibility: public` in devcontainer.json is respected
# inconsistently — `gh codespace ports visibility` is the reliable path.
# Skipped silently outside Codespaces or if the org policy forbids public ports.
if [ "${CODESPACES:-}" = "true" ] && [ -n "${CODESPACE_NAME:-}" ] && command -v gh >/dev/null 2>&1; then
  echo "[devcontainer] Setting port ${PORT_BIND} visibility to public…"
  # Retry briefly — the port-forwarding agent may not have registered the port yet.
  for attempt in 1 2 3 4 5; do
    if gh codespace ports visibility "${PORT_BIND}:public" -c "$CODESPACE_NAME" >/dev/null 2>&1; then
      echo "[devcontainer] Port ${PORT_BIND} is now public."
      break
    fi
    if [ "$attempt" = "5" ]; then
      echo "[devcontainer] WARNING: could not set port ${PORT_BIND} to public."
      echo "                Either the port isn't forwarded yet, or your"
      echo "                organization restricts public port visibility."
      echo "                Open the 'Ports' tab and change it manually."
    else
      sleep 2
    fi
  done
fi
