---
name: build-test
description: 변경된 Gradle 모듈을 찾아 빌드/테스트한다. Java 코드 수정 후 검증할 때 사용.
---

# 변경 모듈 빌드·테스트

1. 변경 파일에서 모듈을 찾는다:
   ```bash
   git -C /Users/doo/project/aqqle diff --name-only HEAD | grep '^application/aqqle/' | cut -d/ -f3 | sort -u
   ```
   - `common`이 바뀌었으면 모든 앱 모듈이 영향을 받으므로 `./gradlew build -x test`로 전체 컴파일한다.
2. `application/aqqle/`에서 모듈별로 컴파일 먼저 확인한다: `./gradlew :<module>:compileJava`
3. 테스트: `./gradlew :<module>:test` (단일 클래스는 `--tests '<FQCN>'`)
   - 대부분 `contextLoads` 테스트라 MySQL(3306)·ES(9200)·Redis(6379)가 떠 있어야 한다. 실패 시 먼저 `docker ps`로 인프라 상태를 확인하고, 인프라 미기동으로 인한 실패인지 코드 문제인지 구분해서 보고한다.
4. 결과를 모듈별로 성공/실패와 핵심 에러 메시지만 요약한다.
