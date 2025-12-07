package com.nexus.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication  // Spring Boot 자동 설정, 컴포넌트 스캔, 설정 클래스 활성화
@EnableJpaAuditing      // JPA Auditing 활성화 (@CreatedDate, @LastModifiedDate 자동 처리)
public class NexusBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusBackendApplication.class, args);
    }
}
