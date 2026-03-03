# nginx-tomcat-readwrite-architecture

Nginx + Tomcat + MySQL Source/Replica 환경에서 **요청 메서드(GET/POST/PUT/DELETE)에 따라 읽기/쓰기 DB를 분기**하는 프로젝트입니다.

| 요청 | 연결 DB |
|------|---------|
| `GET` | Replica DB (`REPLICA_DS`) |
| `POST` / `PUT` / `DELETE` | Source DB (`SOURCE_DS`) |

웹 애플리케이션은 **Servlet/JSP 기반**이며, 로그인 후 **지역별 소비 리포트(업종 TOP5)**를 조회할 수 있습니다.

---

## 1. Architecture

애플리케이션 시작 시 `ApplicationContextListener`가 두 개의 커넥션 풀(DataSource)을 초기화합니다.

- `SOURCE_DS` — 쓰기(변경) 트래픽용 DataSource
- `REPLICA_DS` — 읽기(조회) 트래픽용 DataSource

요청이 들어오면 `DBManager.getConnection(req)`가 HTTP Method를 기준으로 DataSource를 선택합니다. 각 DAO/Servlet은 DB URL·포트를 직접 판단하지 않고 `DBManager`에서 받은 `Connection`만 사용하므로, **읽기/쓰기 분기 정책을 한 곳(DBManager)에서 일관되게 관리**할 수 있습니다.

```
Client
  │
  ▼
[Nginx]  (Reverse Proxy)
  │
  ▼
[Tomcat] (Servlet/JSP · Session Login · API)
  │
  ├── Write (POST/PUT/DELETE) ──► [MySQL Source]
  │                                      │ binlog
  └── Read  (GET)             ──► [MySQL Replica]
                                   ▲ (Replication)
```

> 실무에서는 복제 지연(lag)이나 강한 일관성 요구가 있을 때 "특정 조회는 Source로 강제" 같은 정책도 함께 설계합니다.

---

## 2. 주요 기능

**로그인 / 로그아웃**
- `POST /auth/login`
- `POST /logout`

**인증 필터**
- `/report`, `/report/*` 접근 시 세션 검사

**리포트 화면**
- `GET /report` — JSP 페이지 렌더링
- `GET /report/region?region=서울` — 지역별 업종 TOP5 JSON 반환

**점검용 API**
- `GET  /service/card-data` — 조회 (Replica)
- `POST /service/card-data` — 데이터 삽입 (Source)
- `GET  /test/hikari` — Source/Replica 커넥션 점검

---

## 3. 프로젝트 구조

```
src/main/java/dev/sample
├── ApplicationContextListener.java   # Source/Replica DataSource 초기화
├── DBManager.java                    # HTTP Method 기반 DB 라우팅
├── auth
│   ├── LoginServlet.java
│   ├── LogoutServlet.java
│   ├── AuthFilter.java
│   └── User.java
└── report
    ├── ReportPageServlet.java
    ├── TopSpendServlet.java
    ├── ReportService.java
    ├── ReportReadDao.java
    └── dto/IndustrySpendDto.java
```

---

## 4. 실행 전 준비사항

**필수 환경**
- JDK 11 이상 (권장)
- Apache Tomcat 9 이상
- MySQL 8.x 2대 (Source / Replica)

**현재 코드에 하드코딩된 DB 접속 정보** (`ApplicationContextListener` 기준)

| 역할 | JDBC URL | 계정 |
|------|----------|------|
| Source | `jdbc:mysql://localhost:3308/card_db?serverTimezone=Asia/Seoul` | root / 1234 |
| Replica | `jdbc:mysql://localhost:3309/card_db?serverTimezone=Asia/Seoul` | root / 1234 |

---

## 5. DB 스키마 참고

애플리케이션은 다음 두 테이블을 사용합니다.

**`users`** (로그인)
- 최소 컬럼: `user_id`, `login_id`, `password`, `name`

**`CARD_TRANSACTION`** (리포트 조회)
- 컬럼: `BAS_YH`(기준년월), `HOUS_SIDO_NM`(지역), 업종별 금액 컬럼들

---

## 6. 실행 방법

1. MySQL Source/Replica 실행 및 Replication 구성
2. Tomcat에 WAR 배포 (또는 IDE에서 Tomcat Run)
3. 브라우저 접속 — `http://localhost:8080/` 또는 설정된 컨텍스트 경로

> 컨텍스트 경로는 Tomcat 설정(배포명/WAR명)에 따라 달라질 수 있습니다.

---

## 7. 요청 라우팅 동작 확인

### 브라우저 / API로 확인

| 요청 | 라우팅 |
|------|--------|
| `GET /report/region?region=서울` | Replica DB |
| `GET /service/card-data` | Replica DB |
| `POST /service/card-data` | Source DB |

### 서버 로그로 확인

`DBManager`가 아래와 같은 로그를 출력합니다.

```
# GET 요청
Request Method : GET
Real Connect URL : jdbc:mysql://localhost:3309/...

# POST 요청
Request Method : POST
Real Connect URL : jdbc:mysql://localhost:3308/...
```

## 8. 보완 권장사항

- **DB 접속 정보 외부화** — `.properties`, 환경변수, JNDI 등 활용
- **비밀번호 해시 적용** — 평문 비교 제거, BCrypt 등 사용
- **쿼리 성능 최적화** — SQL·리포트 쿼리 튜닝 및 인덱스 점검
- **로깅 프레임워크 통일** — `System.out.println` → SLF4J
- **헬스체크 및 Failover** — Source/Replica 장애 시 자동 전환 전략 추가
