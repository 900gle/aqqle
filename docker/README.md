# docker

시스템별로 필요한 컨테이너만 골라 띄우기 위한 compose 및 실행 스크립트 모음입니다.

## 디렉터리

| 경로 | 내용 |
|------|------|
| `scripts/` | 시스템별 기동/중지 스크립트 |
| `elastic/` | Elasticsearch / Kibana / Logstash |
| `kafka/` | Kafka / Zookeeper |
| `mysql/` | MySQL (`mysql-init-files/`의 SQL이 최초 기동 시 실행됨) |
| `redis/` | Redis |

## 시스템별 실행 스크립트

| 시스템 | 기동 / 중지 | 대상 |
|--------|-------------|------|
| crawler | `crawler-up.sh` / `crawler-down.sh` | MySQL |
| indexer | `indexer-up.sh` / `indexer-down.sh` | Elasticsearch, MySQL, 임베딩 API |
| manage | `manage-up.sh` / `manage-down.sh` | Elasticsearch, MySQL |

```bash
./docker/scripts/<시스템>-up.sh     # 기동
./docker/scripts/<시스템>-down.sh   # 중지
```

- 스크립트 위치 기준으로 동작하므로 어느 디렉터리에서 실행해도 됩니다.
- `*-down.sh`는 컨테이너를 `stop`만 하며, 컨테이너와 데이터(볼륨)는 삭제하지 않습니다.
- Elasticsearch는 `elastic/docker-compose.yml`의 `elasticsearch` 서비스만 기동합니다 (Kibana/Logstash/setup 제외).
- MySQL: 컨테이너명 `mysql`, 포트 `3306`, root 비밀번호 `root`, 타임존 `Asia/Seoul`.
- 애플리케이션 실행은 `application/aqqle/`에서 합니다 (`/run-batch` 스킬 참고).

### 임베딩 API (indexer)

`indexer-up.sh`는 컨테이너 기동 후 `third_party/tf-embeddings/api/app.py`를 백그라운드로 실행합니다.

- Python: conda 환경 `aqqle`, 주소 `http://localhost:5000/vectors`
- 이미 실행 중이면 중복 실행하지 않습니다.
- pid `scripts/.run/embedding.pid`, 로그 `scripts/.run/embedding.log` (`.run/`은 gitignore)
- `indexer-down.sh`가 컨테이너 중지와 함께 프로세스(debug reloader 자식 포함)를 종료합니다.
- 첫 실행 시 TF Hub에서 모델을 받느라 뜨는 데 시간이 걸립니다: `tail -f docker/scripts/.run/embedding.log`

## 스크립트 추가 규칙

- 파일명은 `<시스템>-up.sh` / `<시스템>-down.sh` 형태로 시스템 이름을 포함합니다.
- 해당 시스템에 필요한 서비스만 `docker compose -f ... up -d`로 실행합니다.
- 새 스크립트를 추가하면 위 표에 함께 정리합니다.
