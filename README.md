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
- **완전 자동화된 CI/CD 파이프라인**: Jenkins + Docker를 통한 무중단 배포 환경 구축
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


### ShortUrl Service (고성능 URL 단축 서비스)

**핵심 기술 구현**
- **양방향 캐싱 전략**: Redis를 활용한 Write-Through + Cache-Aside 패턴
  - `shortUrls` 캐시: 리다이렉트 성능 최적화 (~1ms)
  - `originUrls` 캐시: 중복 생성 방지 (DB INSERT 제거)
- **Base62 인코딩**: Auto Increment ID를 URL-safe한 짧은 키로 변환
- **중복 처리 로직**: 캐시 우선 조회로 불필요한 DB 접근 최소화

**성능 개선 결과**
- 캐시 HIT 시: PostgreSQL 대비 **50배 이상 빠른 응답** (50ms → 1ms)
- DB 부하 감소: 읽기 요청의 **95% 이상을 Redis에서 처리**

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
- 🔜 미구현 기능: 만료 시간 설정, 클릭 통계

---

## 🔧 Technical Implementation

### Caching Strategy
- **Write-Through**: 생성 즉시 Redis 캐시 저장
- **Cache-Aside**: `@Cacheable` 기반 자동 조회 캐싱
- **양방향 캐싱**: `{short→origin}`, `{origin→short}` 동시 관리

### Infrastructure
- **Oracle Cloud**: Docker 기반 호스팅
- **CI/CD**: Jenkins 자동 배포 파이프라인

---

## 🎓 Future Enhancements
- JWT 인증/인가 시스템
- 실시간 통계 대시보드 (조회수, 접근 로그)
- Custom Alias 지원
- Rate Limiting (요청 제한)

---

## License
MIT License
