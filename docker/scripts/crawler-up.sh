#!/usr/bin/env bash
# 크롤러 실행에 필요한 컨테이너(MySQL)만 기동한다.
set -euo pipefail

cd "$(dirname "$0")/../mysql"
docker compose -f docker-compose.yml up -d
