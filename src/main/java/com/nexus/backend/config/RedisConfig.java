package com.nexus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis 인프라 설정을 담당하는 구성 클래스.
 * 인프라 우선(Infrastructure First) 원칙에 따라 데이터 저장소 연결을 정의함.
 */
@Configuration
public class RedisConfig {

    /**
     * 문자열 기반 Redis 조작을 위한 Template 빈 등록.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        
        // Connection Factory를 주입
        template.setConnectionFactory(redisConnectionFactory);
        
        return template;
    }
}