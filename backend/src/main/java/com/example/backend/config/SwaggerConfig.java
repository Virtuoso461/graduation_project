package com.example.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 */
@Configuration
@SecurityScheme(
        name = "JWT认证",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "JWT认证")
        }
)
public class SwaggerConfig {

    @Bean
    public OpenAPI graduationProjectAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("毕业设计项目 API文档")
                        .description("本文档提供毕业设计项目的API接口说明，包含学生、教师和管理员模块的所有接口")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("邱家鹏")
                                .email("2416050435@example.com"))
                        .license(new License()
                                .name("无")
                                .url("无")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目说明文档")
                        .url("https://github.com/yourusername/graduation_project"));
    }
}