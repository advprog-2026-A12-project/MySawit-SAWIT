package id.ac.ui.cs.advprog.mysawit.delivery.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        responseWriter = new StringWriter();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalNoAuthHeaderShouldPassThroughWithoutAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).isValid(any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalAuthHeaderNotBearerShouldPassThroughWithoutAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).isValid(any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternalInvalidTokenShouldReturn401() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
        when(jwtUtil.isValid("invalid.token.here")).thenReturn(false);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(responseWriter.toString()).contains("Token tidak valid atau sudah expired");
    }

    @Test
    void doFilterInternalValidTokenShouldSetAttributesAndAuthenticate() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String token = "valid.jwt.token";
        Claims claims = mock(Claims.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isValid(token)).thenReturn(true);
        when(jwtUtil.parseToken(token)).thenReturn(claims);
        when(jwtUtil.getUserId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("MANDOR");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("userId"), eq(userId));
        verify(request).setAttribute(eq("role"), eq("MANDOR"));
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo(userId.toString());
    }

    @Test
    void doFilterInternalValidTokenAuthShouldContainCorrectRole() throws Exception {
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        String token = "supir.jwt.token";
        Claims claims = mock(Claims.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isValid(token)).thenReturn(true);
        when(jwtUtil.parseToken(token)).thenReturn(claims);
        when(jwtUtil.getUserId(claims)).thenReturn(userId);
        when(jwtUtil.getRole(claims)).thenReturn("SUPIR_TRUK");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPIR_TRUK"));
    }

    @Test
    void doFilterInternalParseTokenThrowsExceptionShouldReturn401WithErrorMessage() throws Exception {
        String token = "malformed.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isValid(token)).thenReturn(true);
        when(jwtUtil.parseToken(token)).thenThrow(new RuntimeException("Signature mismatch"));
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(responseWriter.toString()).contains("Token error");
        assertThat(responseWriter.toString()).contains("Signature mismatch");
    }

    @Test
    void doFilterInternalUnauthorizedResponseShouldWriteJsonBody() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtUtil.isValid("bad.token")).thenReturn(false);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        String body = responseWriter.toString();
        assertThat(body).startsWith("{");
        assertThat(body).contains("error");
        assertThat(body).endsWith("}");
    }
}
