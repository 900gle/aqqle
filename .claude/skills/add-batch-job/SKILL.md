---
name: add-batch-job
description: indexer/extract/crawler/producer 모듈에 새 배치 작업 타입을 추가한다.
argument-hint: <module> <type> <설명>
---

# 배치 작업 추가

인자: `$ARGUMENTS` → `<module> <type> <설명>`

1. `application/aqqle/<module>/src/main/java/com/doo/aqqle/runner/AppCommand.java` 를 읽고 기존 분기 방식을 따른다:
   - indexer / producer: 서비스 빈을 생성자 주입(`@RequiredArgsConstructor`)하고 `switch`에 `case` + `break` 추가
   - extract / crawler: 타입 → 서비스 빈 이름 매핑으로 `services.get("<BeanName>")` 조회. 새 서비스는 공통 인터페이스를 구현하고 빈 이름을 맞춘다.
2. 서비스는 같은 모듈의 기존 서비스 패키지에 만든다. ES 작업이면 주변 코드가 쓰는 클라이언트(ES 8 Java client 또는 7.17 HighLevel)를 그대로 따른다.
3. 새 인덱스면 `indexer/src/main/resources/` 에 `<name>_setting.json` / `<name>_mapping.json` 을 추가한다. 기존 파일의 analyzer 설정을 참고한다.
4. `@Parameters` description의 타입 목록과 `.claude/skills/run-batch/SKILL.md` 의 표를 갱신한다.
5. `./gradlew :<module>:compileJava` 로 컴파일을 확인한다.
