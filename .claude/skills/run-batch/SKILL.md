---
name: run-batch
description: indexer/extract/crawler/producer 배치 작업을 picocli 인자로 실행한다. 색인, 추출, 크롤링을 돌릴 때 사용.
argument-hint: <module> <type> [profile]
---

# 배치 실행

인자: `$ARGUMENTS` → `<module> <type> [profile=local]`

## 작업 타입 (`runner/AppCommand.java`)

| 모듈 | 타입 | 동작 |
|---|---|---|
| indexer | `S` | 쇼핑 상품 색인 (`IndexerService`) |
| indexer | `C` | `IndexerService.index("ddd")` |
| indexer | `I` | 투자 데이터 색인 (`InvestmentService`) |
| indexer | `Y` | 야후 주식 데이터 색인 (`YahooDataService`) |
| indexer | `T` | 테스트 색인 (`TestIndexService`) |
| extract | `D` / `I` / `M` / `Y` / `YD` | Extract / Investment / Merge / Yahoo / YahooData 서비스 |
| crawler | `T` / `Y` / `YD` / `N` | Tmon / Yahoo / YahooData / Naver 크롤링 |
| producer | `S` | Kafka 테스트 메시지 발행 |

타입이 표에 없으면 실행 전에 해당 `AppCommand.java`를 다시 확인한다 (표가 낡았을 수 있음).

## 실행

`bootJar`가 비활성화된 모듈이 많으므로 `java -jar` 대신 `bootRun`을 쓴다. `application/aqqle/`에서:

```bash
./gradlew :<module>:bootRun --args='--spring.profiles.active=<profile> -t <type>'
```

## 주의

- indexer는 ES를 쓰고, 상품 색인(`S`)은 임베딩 서비스(`localhost:5000`)도 필요하다. 실행 전에 확인한다: `curl -s -o /dev/null -w '%{http_code}' localhost:5000/vectors`
- 실행 전에 대상 profile과 영향을 받는 인덱스/테이블을 사용자에게 알린다. `dev` profile은 반드시 확인을 받는다.
- `getExitCode`가 예외에도 0을 반환한다. 종료 코드만 보고 성공을 판단하지 말고 로그에서 `Exception`/`ERROR`를 확인한다.
