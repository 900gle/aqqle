#!/usr/bin/env bash
# PreToolUse(Bash) 훅: 되돌리기 어려운 명령을 차단한다.
# exit 2 + stderr 메시지 → Claude에게 차단 사유가 전달된다.
set -euo pipefail

cmd=$(jq -r '.tool_input.command // ""')

block() {
  echo "차단됨: $1" >&2
  exit 2
}

# Elasticsearch 인덱스/문서 삭제 (curl -X DELETE, _delete_by_query)
if echo "$cmd" | grep -Eq '(localhost|127\.0\.0\.1|elasticsearch):9200' ; then
  if echo "$cmd" | grep -Eiq -- '-X *DELETE|--request *DELETE|_delete_by_query'; then
    block "Elasticsearch 삭제 요청입니다. 필요하면 사용자가 직접 실행하세요."
  fi
fi

# docker 볼륨 삭제 (ES/MySQL 데이터 유실)
if echo "$cmd" | grep -Eq 'docker( |-)compose .*down.*(-v|--volumes)|docker volume (rm|prune)'; then
  block "docker 볼륨 삭제는 ES/MySQL 데이터를 날립니다."
fi

# DB 파괴적 SQL
if echo "$cmd" | grep -Eiq 'mysql.*(drop +(table|database)|truncate +table)'; then
  block "MySQL DROP/TRUNCATE 명령입니다."
fi

exit 0
