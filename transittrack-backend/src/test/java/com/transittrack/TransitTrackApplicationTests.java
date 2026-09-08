package com.transittrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test verifying that the Spring Boot application context loads cleanly with test configuration.
 */
@SpringBootTest
class TransitTrackApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that all entity mappings, repositories, security beans, and services wire up without error
    }
}
