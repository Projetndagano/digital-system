package com.group.payment.unit;

import com.group.payment.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
            "bse3203-digital-payment-secret-key-victoria-university");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void TC_UNIT_001_generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken("test@example.com", "USER");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void TC_UNIT_002_extractEmail_shouldReturnCorrectEmail() {
        String token = jwtUtil.generateToken("test@example.com", "USER");
        assertEquals("test@example.com", jwtUtil.extractEmail(token));
    }

    @Test
    void TC_UNIT_003_extractRole_shouldReturnCorrectRole() {
        String token = jwtUtil.generateToken("test@example.com", "ADMIN");
        assertEquals("ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void TC_UNIT_004_isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("test@example.com", "USER");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void TC_UNIT_005_isTokenValid_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    void TC_UNIT_006_isTokenValid_shouldReturnFalseForExpiredToken() {
        JwtUtil expiredUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredUtil, "secret",
            "bse3203-digital-payment-secret-key-victoria-university");
        ReflectionTestUtils.setField(expiredUtil, "expiration", -1000L);
        String token = expiredUtil.generateToken("test@example.com", "USER");
        assertFalse(expiredUtil.isTokenValid(token));
    }
}