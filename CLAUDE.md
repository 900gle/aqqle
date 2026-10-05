# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Aqqle is a personal text-based portal/shopping search project built on Elasticsearch 8.8.1. It includes crawlers, a Kafka pipeline, batch indexers, a search API with a Redis cache, an admin API, a Thymeleaf web site, and a Python TensorFlow text-embedding service that is used for vector search. The README and most comments are in Korean.

Top-level layout:
- `application/aqqle/` is the Gradle multi-module Java project. All Java work happens here.
- `docker/` has docker-compose stacks for the infrastructure (`elastic/` = ES + Kibana + Logstash, `kafka/`, `redis/`, `mysql/`).
- `plugin/` has custom Elasticsearch plugin zips (`doo-plugin`, `kr-danalyzer`, `payload-score`) plus the analysis-nori dependency.
- `third_party/tf-embeddings/` is a Flask + TensorFlow Hub text-embedding API. Java calls it at `http://localhost:5000/vectors` (`common/.../HostUrl.java`).
- `k8s/` has k3d manifests for deploying only the `api` module locally (step-by-step guide in `k8s/README.md`).

## Build & run

Run all Gradle commands from `application/aqqle/` with the wrapper:

```bash
cd application/aqqle
./gradlew build                      # build all modules
./gradlew :api:build                 # build one module
./gradlew :api:test                  # test one module (JUnit 5)
./gradlew :indexer:test --tests 'com.doo.aqqle.IndexerApplicationTests'   # single test class
./gradlew :api:bootRun --args='--spring.profiles.active=local'
```

- The root build applies Spring Boot 2.7.5 to every subproject and targets Java 17 (`sourceCompatibility = '17'`). Lombok is wired in for all modules.
- Several modules (`api`, `indexer`, `extract`, `manage`, `base`, `common`) set `bootJar.enabled = false` and `jar.enabled = true`, so `./gradlew bootJar` produces nothing for them. `api/Dockerfile` copies `build/libs/api-0.0.1-SNAPSHOT.jar`, so check that the jar is actually executable before relying on the k8s flow.
- There are very few tests. Most modules have only a context-load test, and those tests need the external services listed below to be running.

Infrastructure (docker-compose):
```bash
cd docker/elastic && docker compose up -d --build     # ES/Kibana/Logstash 8.8.1
cd docker/kafka && docker compose -f kafka-full.yml up -d --build
```
Embedding service: `conda activate aqqle && python third_party/tf-embeddings/api/app.py`

## Configuration

- Each app's `application.yml` holds several profile documents separated by `---` (`local`, `dev`). Some modules (`consumer`, `crawler`, `producer`, `web`) use separate `application-{profile}.yml` files instead. Select the profile with `spring.profiles.active`.
- The local defaults expect MySQL at `localhost:3306/shop`, Elasticsearch at `localhost:9200` (user `elastic`), and Redis at `localhost:6379`.
- ES index settings and mappings are JSON resources in `indexer/src/main/resources/` (`setting.json`/`mapping.json`, plus `investment_*` and `yahoo_*` variants). They depend on the custom analyzers from `plugin/` and on dictionary files in `{ES_HOME}/config/` (`stopFilter.txt`, `synonymsFilter.txt`, `user_dictionary.txt`).

## Architecture

Data flow: **crawler** (Selenium/Jsoup scrapes shopping and news sites) → MySQL and/or **producer** → Kafka → **consumer** → MySQL → **extract** (DB → JSON files) / **indexer** (DB/files → Elasticsearch, with text embeddings from the tf-embeddings service) → **api** (search) → **web** (UI). **manage** is the admin API, used for things like managing crawl keywords.

Module notes:
- `common` is a shared library jar (no boot app). It holds JPA entities and repositories (`domain/`, `repository/`), DTOs, response wrappers (`model/CommonResult`, `SingleResult`, `ListResult`, `service/ResponseService`), the `@Timer` annotation with its AOP aspect, and `TextEmbedding`/`SendRestUtil` for calling the embedding API. Every app module depends on `project(':common')`. `base` duplicates `common`'s dependencies and has no source.
- **Batch apps use picocli.** `indexer`, `producer`, and `extract` each have `runner/AppCommandLineRunner` + `runner/AppCommand`. The positional argument picks the job. For example, indexer types are `S` (shop goods), `C`, `I` (investment), `Y` (yahoo stock data), and `T` (test), run as `java -jar indexer.jar -t S`. To add a new batch job, add a case to `AppCommand` and a service.
- **Elasticsearch clients:** `api`, `indexer`, and `manage` include both the ES 8 Java client (`co.elastic.clients:elasticsearch-java`) and the 7.17 High Level REST client. Each module has its own `config/Client`, `HighlevelClient`, and `LowLevelClient` beans. Look at which one the surrounding code uses before writing a new query.
- **api request flow:** controllers (`portal/`, `shop/`, `location/`, `cache/`, `general/`) → services in `portal/service/` → query builders in `component/query/` (`ShopSearchQuery`, `LocationSearchQuery`). Results are wrapped through `ResponseService`. Aggregation and filter caches live in `component/CacheCompo` (Spring `@Cacheable` with keys from `common/CacheKey`, backed by Redis via `RedisConfig`). `PortalService` publishes request events that are handled `@Async` in `PortalEventHandler`. Spring Retry is used in `RetryService`.
- **Error handling:** custom `C*Exception` classes in `advice/exception/` are mapped by `advice/ExceptionAdvice`. The messages come from YAML i18n bundles (`resources/i18n/exception_{en,ko}.yml`, loaded by `MessageConfiguration`). `manage` uses the same pattern.
- `web` is a Thymeleaf app with a large vendored front-end template under `src/main/resources` (about 2,400 files). Avoid sweeping searches or edits in that directory.
