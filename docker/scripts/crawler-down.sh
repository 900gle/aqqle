#!/usr/bin/env bash
# 크롤러용 컨테이너(MySQL)를 중지한다. 컨테이너와 데이터는 삭제하지 않는다.
set -euo pipefail

cd "$(dirname "$0")/../mysql"
docker compose -f docker-compose.yml stop
