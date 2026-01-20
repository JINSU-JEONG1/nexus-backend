package com.nexus.backend.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
// Auditing 기능을 활성화하여 엔티티의 생성시간, 수정시간 등을 자동으로 관리합니다.
@EnableJpaAuditing
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager em;

    /**
     * JPAQueryFactory 를 bean으로 구성하여 매번 EntityManager로 부터 주입받는 부분을 줄임.
     *
     * @return JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
