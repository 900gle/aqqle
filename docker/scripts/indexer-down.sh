#!/usr/bin/env bash
# 인덱서용 컨테이너(Elasticsearch, MySQL)와 임베딩 API를 중지한다. 컨테이너와 데이터는 삭제하지 않는다.
set -euo pipefail

BASE="$(cd "$(dirname "$0")/.." && pwd)"

(cd "$BASE/elastic" && docker compose -f docker-compose.yml stop elasticsearch)
(cd "$BASE/mysql" && docker compose -f docker-compose.yml stop)

# 임베딩 API 종료 (debug 모드의 reloader 자식 프로세스까지)
PID_FILE="$BASE/scripts/.run/embedding.pid"
if [ -f "$PID_FILE" ]; then
  PID="$(cat "$PID_FILE")"
  if kill -0 "$PID" 2>/dev/null; then
    pkill -P "$PID" 2>/dev/null || true
    kill "$PID" 2>/dev/null || true
  fi
  rm -f "$PID_FILE"
fi
