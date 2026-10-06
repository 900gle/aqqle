---
name: infra-up
description: 로컬 인프라(ES/Kibana/Logstash, Kafka, MySQL, Redis, 임베딩 API)를 기동하고 상태를 점검한다.
---

# 로컬 인프라 기동·점검

1. 현재 상태: `docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'`
2. 필요한 스택만 기동한다 (`/Users/doo/project/aqqle/docker/` 기준):
   - ES 스택: `cd docker/elastic && docker compose up -d --build`
   - Kafka: `cd docker/kafka && docker compose -f kafka-full.yml up -d --build`
   - MySQL / Redis: `docker/mysql`, `docker/redis` 의 compose 파일 확인 후 `up -d`
3. 헬스 체크:
   - ES: `curl -s -u elastic:$ES_PASSWORD localhost:9200/_cluster/health`
   - Redis: `docker exec <redis 컨테이너> redis-cli ping`
   - MySQL: 3306 포트 리스닝 확인 (`lsof -i :3306`)
   - 임베딩 API: `curl -s -o /dev/null -w '%{http_code}' localhost:5000/vectors`
4. 임베딩 API가 내려가 있으면 사용자에게 별도 터미널에서 실행을 안내한다 (conda 환경 필요, 장시간 실행 프로세스):
   `conda activate aqqle && python third_party/tf-embeddings/api/app.py`
5. 서비스별 UP/DOWN 표로 보고한다.

`docker compose down -v`, `docker volume rm` 은 데이터가 유실되므로 사용하지 않는다 (훅으로 차단됨).
