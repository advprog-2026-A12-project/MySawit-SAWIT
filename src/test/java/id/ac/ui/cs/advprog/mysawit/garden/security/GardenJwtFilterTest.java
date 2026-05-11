package id.ac.ui.cs.advprog.mysawit.garden.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GardenJwtFilterTest {

   @Mock
   private JwtUtil jwtUtil;

   @Mock
   private HttpServletRequest request;

   @Mock
   private HttpServletResponse response;

   @Mock
   private FilterChain chain;

   @Mock
   private Claims claims;

   private GardenJwtFilter filter;

   @BeforeEach
   void setUp() {
      filter = new GardenJwtFilter(jwtUtil, true);
   }

   @Test
   void shouldBypassAllRequestsWhenAuthIsDisabled() throws Exception {
      GardenJwtFilter disabledFilter = new GardenJwtFilter(jwtUtil, false);

      disabledFilter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      verify(jwtUtil, never()).isValid(anyString());
   }

   @Test
   void shouldBypassNonGardenEndpoints() throws Exception {
      when(request.getRequestURI()).thenReturn("/api/deliveries");

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      verify(jwtUtil, never()).isValid(anyString());
   }

   @Test
   void shouldRejectMissingAuthorizationHeader() throws Exception {
      when(request.getRequestURI()).thenReturn("/api/kebun");
      when(request.getMethod()).thenReturn("GET");
      when(request.getHeader("Authorization")).thenReturn(null);
      StringWriter writerBuffer = new StringWriter();
      when(response.getWriter()).thenReturn(new PrintWriter(writerBuffer));

      filter.doFilter(request, response, chain);

      verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      verify(chain, never()).doFilter(request, response);
      String body = writerBuffer.toString();
      assertTrue(body.contains("\"status\":\"error\""));
      assertTrue(body.contains("Missing or invalid Authorization header"));
   }

   @Test
   void shouldAllowValidGardenToken() throws Exception {
      String token = "valid-token";
      UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

      when(request.getRequestURI()).thenReturn("/api/kebun/123");
      when(request.getMethod()).thenReturn("GET");
      when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
      when(jwtUtil.isValid(token)).thenReturn(true);
      when(jwtUtil.extractClaims(token)).thenReturn(claims);
      when(claims.getSubject()).thenReturn(userId.toString());
      when(claims.get("role", String.class)).thenReturn("ADMIN");

      filter.doFilter(request, response, chain);

      verify(request).setAttribute("userId", userId);
      verify(request).setAttribute("userRole", "ADMIN");
      verify(chain).doFilter(request, response);
   }
}