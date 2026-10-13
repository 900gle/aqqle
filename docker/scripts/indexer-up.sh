#!/usr/bin/env bash
# 인덱서 실행에 필요한 컨테이너(Elasticsearch, MySQL)를 기동하고 임베딩 API를 백그라운드로 실행한다.
set -euo pipefail

BASE="$(cd "$(dirname "$0")/.." && pwd)"

(cd "$BASE/elastic" && docker compose -f docker-compose.yml up -d --build elasticsearch)
(cd "$BASE/mysql" && docker compose -f docker-compose.yml up -d)

# 임베딩 API (백그라운드 실행) — http://localhost:5000/vectors
RUN_DIR="$BASE/scripts/.run"
PID_FILE="$RUN_DIR/embedding.pid"
LOG_FILE="$RUN_DIR/embedding.log"
mkdir -p "$RUN_DIR"

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "임베딩 API가 이미 실행 중입니다 (pid $(cat "$PID_FILE"))"
  exit 0
fi

PYTHON="$(conda info --base)/envs/aqqle/bin/python"
cd "$BASE/../third_party/tf-embeddings/api"
nohup "$PYTHON" app.py > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"
echo "임베딩 API 시작 (pid $!), 로그: $LOG_FILE"
