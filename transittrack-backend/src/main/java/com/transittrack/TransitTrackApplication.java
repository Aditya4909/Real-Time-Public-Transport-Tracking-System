package com.transittrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for TransitTrack – Real-Time Public Transport Tracking System.
 *
 * Annotations explanation:
 * - @SpringBootApplication: A convenience composite annotation combining:
 *   1. @Configuration: Tags the class as a source of bean definitions for the application context.
 *   2. @EnableAutoConfiguration: Instructs Spring Boot to automatically configure beans based on classpath dependencies
 *      (e.g., configuring HikariCP DataSource when MySQL connector is present, setting up Hibernate JPAEntityManagerFactory, etc.).
 *   3. @ComponentScan: Enables component scanning on the current package (com.transittrack) and all its subpackages,
 *      automatically discovering @Controller, @Service, @Repository, and @Component beans.
 */
@SpringBootApplication
public class TransitTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitTrackApplication.class, args);
    }
}
