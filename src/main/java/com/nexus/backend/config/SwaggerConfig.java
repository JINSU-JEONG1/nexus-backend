package com.nexus.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger UI 설정 (API 문서화)
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("nexus Backend API")
                        .description("nexus Backend REST API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("jsjeong")
                                .email("jinsu6993@naver.com")))
                .servers(List.of(
                        new Server().url("http://localhost:4000").description("로컬 개발용"),
                        new Server().url("https://api.nexus-backend.com").description("운영 서버")
                ));

    }
}
