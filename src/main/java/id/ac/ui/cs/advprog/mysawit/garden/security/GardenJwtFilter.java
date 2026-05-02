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
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * JWT authentication and authorization filter untuk endpoint /api/kebun.
 * Menerapkan role-based access control: ADMIN untuk mutasi, ADMIN/MANDOR untuk read.
 */
@Component
@Order(2)
public class GardenJwtFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GardenJwtFilter.class);
    private static final String KEBUN_PATH = "/api/kebun";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Set<String> MUTATION_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");
    private static final Set<String> READ_ALLOWED_ROLES = Set.of("ADMIN", "MANDOR");

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

        if (!authEnabled || !request.getRequestURI().startsWith(KEBUN_PATH)
                || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtil.isValid(token)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
            return;
        }

        Claims claims = jwtUtil.extractClaims(token);
        UUID userId;
        try {
            userId = UUID.fromString(claims.getSubject());
        } catch (IllegalArgumentException ex) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid user id in token");
            return;
        }

        String role = claims.get("role", String.class);
        String method = request.getMethod().toUpperCase();

        if (MUTATION_METHODS.contains(method) && !"ADMIN".equals(role)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Hanya ADMIN yang dapat melakukan operasi ini");
            return;
        }

        if ("GET".equals(method) && !READ_ALLOWED_ROLES.contains(role)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Role tidak memiliki akses ke resource ini");
            return;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("userRole", role);

        log.debug("Garden auth passed: userId={}, role={}, method={}", userId, role, method);
        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":\"error\",\"message\":\"" + message
                        + "\",\"timestamp\":\"" + Instant.now() + "\"}"
        );
    }
}