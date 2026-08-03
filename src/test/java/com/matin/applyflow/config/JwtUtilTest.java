package com.matin.applyflow.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        String secret = Base64.getEncoder()
                .encodeToString("a-test-secret-key-thats-long-enough-for-hs256".getBytes());
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 3600000L);

        userDetails = new User("matin", "password", List.of());
    }

    @Test
    void generateAccessToken_thenExtractUsername_returnsOriginalUsername() {
        String token = jwtUtil.generateAccessToken(userDetails);

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("matin");
    }

    @Test
    void isTokenValid_withMatchingUser_returnsTrue() {
        String token = jwtUtil.generateAccessToken(userDetails);

        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_returnsFalse() {
        String token = jwtUtil.generateAccessToken(userDetails);
        UserDetails differentUser = new User("someone-else", "password", List.of());

        assertThat(jwtUtil.isTokenValid(token, differentUser)).isFalse();
    }
}