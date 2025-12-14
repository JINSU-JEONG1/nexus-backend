# step 1 빌드 환경 (builder)
FROM gradle:8.5-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 소스코드복사
COPY src src
# JAR생성
RUN ./gradlew bootJar --no-daemon

# Step 2: 실행 환경 (runner)
FROM eclipse-temurin:21-jre AS runner
# 작업 디렉토리 설정
WORKDIR /app
# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar
# 컨테이너 포트 노출
EXPOSE 4000
# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]