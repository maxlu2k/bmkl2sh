package com.demo.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApi(@Value("${open.api.title}") String title,
                           @Value("${open.api.version}") String version,
                           @Value("${open.api.description}") String description,
                           @Value("${open.api.serversUrl}") String serversUrl,
                           @Value("${open.api.serversName}") String serversName) {
        return new OpenAPI().info(new Info()
                .title(title)
                .version(version)
                .description(description)
                .license(new License().name("API License").url("http://domain.vn/license")))
                .servers(List.of(new Server().url(serversUrl).description(serversName)))
                        .components(          //cấu hình authorizations
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }

    @Bean
    public GroupedOpenApi gouGroupedOpenApi(){
        return GroupedOpenApi.builder()
                .group("api-service")
                .packagesToScan("com.demo.controllers") //scan tất cả controller trong package đó chỉ định sai sẽ lỗi
                .build();
    }

}
