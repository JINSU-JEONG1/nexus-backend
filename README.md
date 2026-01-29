# Nexus Backend

> 🔗 **Live Demo**: 아래 url에서 확인하실수 있습니다.
 [http://js-nexus.kro.kr/]

**Nexus Backend**는 개인프로젝트로 개발한 다양한 웹 서비스와 유틸리티의 핵심 로직을 담당하는 통합 백엔드 저장소입니다. 

프론트엔드와 통신하여 URL 단축 서비스 등 실질적인 기능을 제공하며, 
**Oracle Cloud Infrastructure** 에 **Docker**와 **Jenkins**를 이용한 자동화 배포 환경을 직접 구축하였으며,
새로운 기술적 시도와 서비스 확장을 지속적으로 수행하는 개인 프로젝트들의 기술적 허브 역할을 합니다.

---

## 🎯 Technical Highlights

### 핵심 성과
- **고성능 캐싱 시스템 구축**: Redis Write-Through 전략으로 **80% 이상의 응답 속도 개선** 
- **확장 가능한 아키텍처 설계**: Stateless 구조로 수평 확장 가능한 MSA 구현
- **CI/CD 파이프라인**: Jenkins + Docker를 통한 배포 환경 구축
- **Production-Ready 코드**: Swagger 기반 API 문서 자동화 및 계층형 아키텍처 적용

### 🛠️ Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6, Spring Data JPA
- **Database**: PostgreSQL
- **Cache**: Redis 
- **Infrastructure**: Oracle Cloud (OCI)
- **CI/CD**: Docker, Jenkins
- **Documentation**: Swagger (OpenAPI 3.0)

---

## 🏗️ Architecture
<img width="1408" height="768" alt="nexus-architecture" src="https://github.com/user-attachments/assets/8e8ad4f8-c140-4b5c-85f9-b6dd00216276" />




### ShortUrl Service (고성능 URL 단축 서비스)

**핵심 기술 구현**
- **양방향 캐싱 전략**: Redis를 활용한 Write-Through + Cache-Aside 패턴
  - `shortUrls` 캐시: 리다이렉트 성능 최적화
  - `originUrls` 캐시: 중복 생성 방지 (DB INSERT 제거)
- **Base62 인코딩**: Auto Increment ID를 URL-safe한 짧은 키로 변환
- **중복 처리 로직**: 캐시 우선 조회로 불필요한 DB 접근 최소화

**통계 및 트렌드 분석**
- **Rolling Window 전략**: 기간별 데이터의 정밀한 추이 분석
  - `Daily`: 최근 7일간의 변동 추이
  - `Weekly`: 최근 6주간의 주차별 성장률
  - `Monthly`: 최근 12개월간의 장기 트렌드

**성능 개선 결과**
- 캐시 HIT 시: PostgreSQL 대비 **20배 이상 빠른 응답** (50ms → 1ms)
- DB 부하 감소: 단축 URL redirect 요청의 **90% 이상을 Redis에서 처리**

---

## 📂 Project Structure

```
nexus-backend/
├── api/
│   ├── shortUrl/          # URL 단축 서비스
│   │   ├── controller/    # REST API Layer
│   │   ├── service/       # Business Logic + Cache
│   │   ├── repository/    # Data Access
│   │   ├── entity/        # JPA Entity
│   │   └── dto/           # Request/Response DTO
│   └── user/              # 사용자 관리 (TBD)
├── common/                # 공통 모듈 (API 응답, 유틸리티)
├── config/                # Spring 설정 (Swagger, CORS)
└── resources/
    └── application.yml    # 환경별 설정
```

---

## ⚙️ Getting Started

### Prerequisites
- JDK 21, Docker, Gradle

### Run
```bash
# 개발 모드
./gradlew bootRun
```

### API Documentation
- **Swagger UI**: http://localhost:4000/swagger-ui.html

---

## 🚀 Features


**주요 기능**
- ✅ URL 단축 및 자동 리다이렉트
- ✅ 중복 생성 방지 (캐시 기반)
- ✅ Base62 인코딩 (짧고 안전한 키 생성)
- ✅ 클릭 통계 및 트렌드 분석
- 🔜 미구현 기능: 만료 시간 설정

---

## 📊 Database Schema (ERD)

### `short_url` (URL 정보 테이블)
| Column | Type | Description |
|--------|------|-------------|
| id | bigint | Primary Key (PK) |
| origin_url | text | 원본 URL 주소 |
| short_url | varchar(10) | 단축된 62진수 키값 |
| expired_at | timestamp | URL 만료 일시 (Optional) |
| created_at | timestamp | 생성 일시 |
| updated_at | timestamp | 수정 일시 |

### `short_url_stats` (통계 테이블)
| Column | Type | Description |
|--------|------|-------------|
| id | bigint | Primary Key (PK) |
| short_url_id | bigint | `short_url` 테이블 참조 키 |
| stat_date | date | 통계 기준 날짜 (yyyy-MM-dd) |
| click_count | bigint | 해당 날짜의 총 클릭 수 |
| referer | text | 접근 경로 정보 |
| created_at | timestamp | 기록 생성 일시 |
| updated_at | timestamp | 기록 수정 일시 |

---

## 🔧 Technical Implementation & Design Rationale

### 1. Url 단축 로직 (Base62 & ID-Based)
- **ID 기반 인코딩**: DB의 Auto-Increment PK를 기반으로 **Base62(0-9, a-z, A-Z)** 인코딩을 수행합니다. 
- **효율성**: 단순 해시 방식보다 충돌 위험이 적으며, 짧은 문자열(단 10자리 내외)로 수십억 개의 URL을 표현할 수 있습니다.

### 2. 하이브리드 캐싱 전략 (Hybrid Caching)
성능 극대화를 위해 데이터의 성격에 따라 두 가지 캐시 전략을 혼용합니다.

#### **A. Write-Through (URL 메타데이터)**
- 대상: 원본 URL 및 단축 URL 매핑 정보
- 방식: 새로운 URL 생성 시 DB와 Redis에 동시에 데이터를 기록합니다.
- 장점: 읽기 요청 시 DB가 아닌 캐시에 접근하도록 하여  **1ms 미만의 응답 속도**를 보장합니다.

#### **B. Write-Behind / Write-Back (클릭 통계)**
- 대상: 실시간 클릭 수 (`click_count`)
- 방식: 사용자가 단축 URL을 클릭할 때마다 DB에 직접 쓰지 않고, **Redis의 `INCR` 명령**으로 카운트를 먼저 올립니다. 이후 **5분 주기 스케줄러**(`ShortUrlStatsScheduler`)가 Redis의 데이터를 DB로 일괄 업데이트(Bulk Update)합니다.
- 장점: 초당 수천 건의 클릭이 발생해도 DB 커넥션 부하를 최소화할수 있습니다.

### 3. Rolling Window 통계 알고리즘
- 특정 시점의 스냅샷이 아닌, **현재 시점부터 과거를 역산(Rolling)**하는 동적 데이터 집계 방식을 적용했습니다.
- QueryDSL을 활용하여 날짜별 `GROUP BY` 쿼리를 최적화하고, 서비스 레이어에서 주차/월별 데이터를 Map 기반으로 재구성하여 프론트엔드 차트 라이브러리에 최적화된 포맷으로 반환합니다.

---

## 🔧 Infrastructure & Scalability
- **Stateless Design**: 서버 내부에 상태를 저장하지 않아 트래픽 증가 시 수평 확장(Scale-out)이 용이합니다.
- **PostgreSQL**: 안정적인 관계형 데이터 저장 및 인덱싱 최적화.
- **Redis**: 고성능 인메모리 저장소를 활용한 트래픽 병목 현상 해소.

---

## 🎓 Future Enhancements
- JWT 인증/인가 시스템
- oAuth 로그인
- Rate Limiting (요청 제한)

---

## License
MIT License
