package com.transittrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test ensuring the Spring application context loads properly.
 */
@SpringBootTest
@ActiveProfiles("dev")
class TransitTrackApplicationTests {

    @Test
    void contextLoads() {
        // Context loading verification
    }
}
