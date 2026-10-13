#!/usr/bin/env bash
# 관리(manage)용 컨테이너(Elasticsearch, MySQL)를 중지한다. 컨테이너와 데이터는 삭제하지 않는다.
set -euo pipefail

BASE="$(cd "$(dirname "$0")/.." && pwd)"

(cd "$BASE/elastic" && docker compose -f docker-compose.yml stop elasticsearch)
(cd "$BASE/mysql" && docker compose -f docker-compose.yml stop)
