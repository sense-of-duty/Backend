package org.example.gdgpage.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_KEY = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GDGoC 그로우톤 프로젝트")
                        .description("GDGoC 통합 페이지 스웨거")
                        .version("1.0.1")
                )
                .components(new Components()
                        .addSecuritySchemes(BEARER_KEY,
                                new SecurityScheme()
                                        .name(BEARER_KEY)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(BEARER_KEY));
    }
}
