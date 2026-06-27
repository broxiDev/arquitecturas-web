package com.farmacyfood.auth.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "mi.clave.secreta.jwt.para.test.1234567890";
    private static final long EXPIRATION_MS = 3600000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_MS);
    }

    @Test
    void generateToken_produceTokenValido() {
        String token = jwtUtil.generateToken("juan", "cliente");

        assertNotNull(token);
        assertTrue(jwtUtil.validate(token));
    }

    @Test
    void getUsername_extraeSubject() {
        String token = jwtUtil.generateToken("juan", "cliente");

        assertEquals("juan", jwtUtil.getUsername(token));
    }

    @Test
    void getRole_extraeClaim() {
        String token = jwtUtil.generateToken("juan", "cliente");

        assertEquals("cliente", jwtUtil.getRole(token));
    }

    @Test
    void validate_retornaFalse_paraTokenInvalido() {
        assertFalse(jwtUtil.validate("token.invalido.xyz"));
    }

    @Test
    void validate_retornaFalse_paraTokenExpirado() {
        var secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
                .subject("juan")
                .claim("role", "cliente")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtUtil.validate(expiredToken));
    }
}
