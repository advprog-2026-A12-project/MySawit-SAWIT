package id.ac.ui.cs.advprog.mysawit.delivery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String SECRET = "bXlTYXdpdExvY2FsRGV2ZWxvcG1lbnRTZWNyZXRLZXlUaGF0SXNMb25nRW5vdWdoRm9ySFMyNTZBbGdvcml0aG0=";

    private static final UUID TEST_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private JwtUtil jwtUtil;
    private String validToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        validToken = Jwts.builder()
                .subject(TEST_USER_ID.toString())
                .claim("role", "MANDOR")
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L)) // 1 jam
                .signWith(key)
                .compact();
    }

    @Test
    void isValid_withValidToken_shouldReturnTrue() {
        assertThat(jwtUtil.isValid(validToken)).isTrue();
    }

    @Test
    void isValid_withGarbageToken_shouldReturnFalse() {
        assertThat(jwtUtil.isValid("this.is.not.valid")).isFalse();
    }

    @Test
    void isValid_withEmptyString_shouldReturnFalse() {
        assertThat(jwtUtil.isValid("")).isFalse();
    }

    @Test
    void parseToken_withValidToken_shouldReturnClaims() {
        Claims claims = jwtUtil.parseToken(validToken);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(TEST_USER_ID.toString());
    }

    @Test
    void getUserId_shouldReturnCorrectUUID() {
        Claims claims = jwtUtil.parseToken(validToken);
        UUID userId = jwtUtil.getUserId(claims);

        assertThat(userId).isEqualTo(TEST_USER_ID);
    }

    @Test
    void getRole_shouldReturnCorrectRole() {
        Claims claims = jwtUtil.parseToken(validToken);
        String role = jwtUtil.getRole(claims);

        assertThat(role).isEqualTo("MANDOR");
    }

    @Test
    void isValid_withExpiredToken_shouldReturnFalse() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        String expiredToken = Jwts.builder()
                .subject(TEST_USER_ID.toString())
                .claim("role", "MANDOR")
                .expiration(new Date(System.currentTimeMillis() - 1000L)) // sudah expire
                .signWith(key)
                .compact();

        assertThat(jwtUtil.isValid(expiredToken)).isFalse();
    }

    @Test
    void parseToken_withWrongSecretToken_shouldThrowException() {
        String otherSecret = "YW5vdGhlclNlY3JldEtleVRoYXRJc0xvbmdFbm91Z2hGb3JIUzI1NkFsZ29yaXRobTEyMw==";
        SecretKey otherKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(otherSecret));
        String tokenWithWrongSecret = Jwts.builder()
                .subject(TEST_USER_ID.toString())
                .claim("role", "ADMIN")
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(otherKey)
                .compact();

        assertThatThrownBy(() -> jwtUtil.parseToken(tokenWithWrongSecret))
                .isInstanceOf(Exception.class);
    }
}
