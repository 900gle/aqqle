#!/usr/bin/env bash
# 관리(manage) 실행에 필요한 컨테이너(Elasticsearch, MySQL)만 기동한다.
set -euo pipefail

BASE="$(cd "$(dirname "$0")/.." && pwd)"

(cd "$BASE/elastic" && docker compose -f docker-compose.yml up -d --build elasticsearch)
(cd "$BASE/mysql" && docker compose -f docker-compose.yml up -d)
