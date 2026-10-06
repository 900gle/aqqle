# CLAUDE.md

이 파일은 이 저장소에서 작업하는 Claude Code(claude.ai/code)를 위한 가이드입니다.

## 개요

Elasticsearch 8.8.1 기반 개인 포털/쇼핑 검색 프로젝트입니다.
- `application/aqqle/`: Gradle 멀티 모듈 Java 프로젝트 (모든 Java 작업은 여기서).
- `docker/`: ES/Kibana/Logstash, Kafka, Redis, MySQL docker-compose.
- `plugin/`: 커스텀 ES 플러그인 (`doo-plugin`, `kr-danalyzer`, `payload-score`) + analysis-nori.
- `third_party/tf-embeddings/`: Flask + TensorFlow 텍스트 임베딩 API (`http://localhost:5000/vectors`, `common/.../HostUrl.java`).
- `k8s/`: `api` 모듈 전용 k3d 매니페스트 (`k8s/README.md` 참고).

## 빌드 및 실행

`application/aqqle/`에서 실행:

```bash
./gradlew build
./gradlew :api:test
./gradlew :indexer:test --tests 'com.doo.aqqle.IndexerApplicationTests'
./gradlew :api:bootRun --args='--spring.profiles.active=local'
```

- `api`, `indexer`, `extract`, `manage`, `common`, `base`는 `bootJar`가 비활성화되어 있어 실행 가능한 jar가 생성되지 않습니다. `api/Dockerfile`(k8s 배포)은 이 jar를 그대로 복사하므로 주의하세요.
- 테스트는 대부분 컨텍스트 로드 테스트뿐이며, 외부 서비스(MySQL, ES, Redis)가 실행 중이어야 통과합니다.

인프라 및 임베딩 서비스:
```bash
cd docker/elastic && docker compose up -d --build
cd docker/kafka && docker compose -f kafka-full.yml up -d --build
conda activate aqqle && python third_party/tf-embeddings/api/app.py
```

## 설정

- 프로파일은 `local`, `dev`. 대부분 모듈은 `application.yml` 하나에 `---`로 구분하고, `consumer`, `crawler`, `producer`, `web`은 `application-{profile}.yml`로 분리되어 있습니다.
- 로컬 기본값: MySQL `localhost:3306/shop`, ES `localhost:9200`(user `elastic`), Redis `localhost:6379`.
- ES 인덱스 setting/mapping은 `indexer/src/main/resources/`의 JSON이며, `plugin/`의 커스텀 분석기와 `{ES_HOME}/config/`의 사전 파일(`stopFilter.txt`, `synonymsFilter.txt`, `user_dictionary.txt`)에 의존합니다.

## 아키텍처

데이터 흐름: **crawler** → MySQL / **producer** → Kafka → **consumer** → MySQL → **extract**(DB → JSON) / **indexer**(→ ES, 임베딩 포함) → **api**(검색) → **web**(UI). **manage**는 관리자 API.

- `common`: 모든 앱이 의존하는 공유 라이브러리 (JPA 엔티티/리포지토리, 응답 래퍼 `ResponseService`, `@Timer` AOP, 임베딩 호출 `TextEmbedding`).
- **배치 앱(`indexer`, `producer`, `extract`)은 picocli 사용** (`runner/AppCommand`). 예: `java -jar indexer.jar -t S` — `-t`는 값 없는 필수 플래그이고, 작업은 위치 인자(`S`, `C`, `I`, `Y`, `T`)로 결정됩니다. 새 작업은 `AppCommand`에 case 추가.
- **ES 클라이언트:** `api`, `indexer`, `manage`는 ES 8 Java 클라이언트와 7.17 High Level REST 클라이언트를 함께 사용합니다. 새 쿼리 작성 전 주변 코드가 어떤 클라이언트를 쓰는지 확인하세요.
- **api:** 컨트롤러 → `portal/service/` → `component/query/` 쿼리 빌더. 집계/필터 캐시는 `component/CacheCompo`(`@Cacheable` + Redis). 예외는 `advice/ExceptionAdvice` + i18n YAML(`exception_{en,ko}.yml`) — `manage`도 동일.
- `web/src/main/resources`에는 약 2,400개의 벤더 프론트엔드 파일이 있으니 광범위한 검색/수정을 피하세요.
