---
name: es-check
description: 로컬 Elasticsearch 클러스터·인덱스·매핑 상태를 읽기 전용으로 점검한다. 색인 결과 확인, 매핑 비교, 검색 디버깅에 사용.
argument-hint: [index]
---

# Elasticsearch 점검 (읽기 전용)

인증: `-u elastic:$ES_PASSWORD`. 환경변수가 없으면 사용자에게 `export ES_PASSWORD=...` 설정을 요청한다. 비밀번호를 명령이나 파일에 하드코딩하지 않는다.

```bash
ES="curl -s -u elastic:$ES_PASSWORD localhost:9200"
$ES/_cluster/health?pretty
$ES/_cat/indices?v&s=index
$ES/_cat/aliases?v
$ES/_cat/plugins?v                # doo-plugin, kr-danalyzer, payload-score, nori 설치 여부
```

인덱스(`$ARGUMENTS`)가 주어지면:
- `$ES/<index>/_mapping?pretty` 를 `application/aqqle/indexer/src/main/resources/` 의 `mapping.json`(또는 `investment_*`, `yahoo_*`)과 비교해서 차이를 보고한다.
- `$ES/<index>/_count`
- 분석기 확인: `$ES/<index>/_analyze` 에 `{"analyzer":"<name>","text":"<샘플>"}` POST

규칙: GET, `_analyze`, `_search`, `_count`만 사용한다. PUT/POST로 쓰기, DELETE, `_reindex`, `_delete_by_query`는 실행하지 말고 명령만 제안한다.
