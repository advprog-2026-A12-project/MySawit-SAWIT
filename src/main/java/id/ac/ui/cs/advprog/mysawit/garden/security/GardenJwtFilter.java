package id.ac.ui.cs.advprog.mysawit.garden.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class GardenJwtFilter implements Filter {

   private final JwtUtil jwtUtil;
   private final boolean authEnabled;

   public GardenJwtFilter(
         @Qualifier("gardenJwtUtil") JwtUtil jwtUtil,
           @Value("${garden.auth.enabled:true}") boolean authEnabled) {
      this.jwtUtil = jwtUtil;
      this.authEnabled = authEnabled;
   }

   @Override
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
           throws IOException, ServletException {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      if (!authEnabled) {
         chain.doFilter(request, response);
         return;
      }

      if (!request.getRequestURI().startsWith("/api/kebun")) {
         chain.doFilter(request, response);
         return;
      }

      String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setContentType("application/json");
         response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
         return;
      }

      String token = authHeader.substring(7);

      if (!jwtUtil.isValid(token)) {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setContentType("application/json");
         response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
         return;
      }

      Claims claims = jwtUtil.extractClaims(token);

      try {
         request.setAttribute("userId", UUID.fromString(claims.getSubject()));
      } catch (IllegalArgumentException ex) {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         response.setContentType("application/json");
         response.getWriter().write("{\"error\":\"Invalid user id in token\"}");
         return;
      }

      request.setAttribute("userRole", claims.get("role", String.class));
      request.setAttribute("userName", claims.get("name", String.class));

      chain.doFilter(request, response);
   }
}