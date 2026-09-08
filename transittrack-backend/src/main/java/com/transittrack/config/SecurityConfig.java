package com.transittrack.config;

import com.transittrack.security.JwtAuthenticationEntryPoint;
import com.transittrack.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 6 configuration class.
 *
 * Annotations explanation:
 * - @Configuration: Identifies this as a Spring configuration class providing @Bean definitions.
 * - @EnableWebSecurity: Activates Spring Security's web security support.
 * - @EnableMethodSecurity(prePostEnabled = true): Enables @PreAuthorize and @Secured method-level security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password encoder bean utilizing BCrypt hashing with salt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring's AuthenticationManager bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the HTTP security filter chain, endpoint authorization rules, CORS, and JWT filter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since we use stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Handle authentication exceptions
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                // Configure stateless session management (no HTTP session is created)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: Authentication, Swagger UI, API Docs, and WebSocket handshake
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/ws-transit/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Public read-only endpoints for transit users
                        .requestMatchers(HttpMethod.GET, "/api/v1/routes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stops/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/{id}/location").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/{id}/eta").permitAll()

                        // Admin dashboard and management endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/routes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/stops/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/stops/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/stops/**").hasRole("ADMIN")

                        // Live driver tracking transmission
                        .requestMatchers("/api/v1/tracking/location").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers("/api/v1/tracking/**").hasAnyRole("DRIVER", "ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Allow framing for H2 console if needed
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // Register custom JWT authentication filter before standard UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS policy allowing frontend apps (React, Angular, Mobile) to communicate with the API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
