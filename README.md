# Nexus 백엔드 & 아키텍처 개요

🔗 해당 프로젝트는 아래 주소에서 확인하실수 있습니다. [http://js-nexus.kro.kr/](http://js-nexus.kro.kr/)


## 📖 프로젝트 소개
Nexus는 **MSA (마이크로서비스 아키텍처)** 기반의 개인 프로젝트 포트폴리오를 관리하기 위한 **중앙 API 게이트웨이 및 통합 서버**입니다.
여러 개별 토이 프로젝트(마이크로서비스)로의 트래픽을 중개하고, 인증 및 인가를 중앙에서 관리하는 역할을 담당합니다.

이 프로젝트는 컨테이너화 및 자동화된 배포 파이프라인을 통해 확장성 있고 효율적인 관리가 가능하도록 설계되었습니다.

---

## 🏗️ 시스템 아키텍처

전체 시스템은 **Oracle Cloud Infrastructure** 상에서 호스팅됩니다. 각 컴포넌트는 Docker를 통해 컨테이너화되어 일관성과 격리성을 보장합니다.


## 🛠️ 기술 스택 (Tech Stack)

### **Frontend**
- **Framework**: Vue.js 2
- **Server**: Nginx (리버스 프록시 및 정적 파일 서빙)
- **Deployment**: Docker Container

### **Backend**
- **Framework**: Spring Boot
- **Role**: API 게이트웨이, 비즈니스 로직 처리, 인증/인가
- **Deployment**: Docker Container

### **Database & Storage**
- **Main Database**: PostgreSQL (Docker Container)
- **Cache Store**: Redis (Docker Container)

### **DevOps & Infrastructure**
- **Cloud Provider**: Oracle Cloud
- **CI/CD**: Jenkins (호스트 서버에 직접 설치)
- **Containerization**: Docker & Docker Compose

---

## 🚀 주요 기능
- **중앙 집중식 관리**: 여러 마이크로서비스에 대한 단일 진입점 제공.
- **ShortUrl 서비스**: Write-Through 캐싱 전략을 적용한 고성능 URL 단축 서비스.
- **마이크로서비스 라우팅**: 내부의 다른 서비스로 동적 라우팅 지원.
- **확장 가능한 아키텍처**: Docker 기반 배포로 쉬운 스케일링 및 관리.

---

## 📂 프로젝트 구조
```
nexus-backend/
├── src/
│   ├── main/
│   │   ├── java/com/nexus/backend/
│   │   │   ├── api/          # API 기능 구현 (예: ShortUrl)
│   │   │   ├── common/       # 전역 유틸리티 및 예외 처리
│   │   │   └── config/       # 설정 파일 (Redis, Web 등)
│   │   └── resources/
│   │       └── application.yml
└── Dockerfile                # 백엔드 Docker 설정
```

---

## 🔧 시작하기 (로컬 개발 환경)

### 필수 요구사항 (Prerequisites)
- JDK 21
- Docker & Docker Compose
- Gradle

### 실행 방법
```bash
# 1. 지원 서비스 실행 (DB, Redis)
docker-compose up -d redis postgres

# 2. Spring Boot 애플리케이션 실행
./gradlew bootRun
```
