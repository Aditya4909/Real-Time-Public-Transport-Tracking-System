package com.transittrack.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger OpenAPI 3 configuration.
 *
 * Annotations explanation:
 * - @Configuration: Identifies this as a Spring IoC configuration bean.
 * - @OpenAPIDefinition: Configures global metadata, info, contacts, and security requirements for OpenAPI specification.
 * - @SecurityScheme: Registers Bearer JWT authentication scheme for interactive Swagger UI testing.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TransitTrack – Real-Time Public Transport Tracking System API",
                version = "1.0.0",
                description = "RESTful and WebSocket APIs for public transit fleet management, live vehicle location telemetry, and dynamic arrival ETA calculations.",
                contact = @Contact(
                        name = "TransitTrack Engineering Team",
                        email = "support@transittrack.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        security = {
                @SecurityRequirement(name = "Bearer Authentication")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Enter JWT Bearer token obtained from /api/v1/auth/login"
)
public class OpenApiConfig {
}
