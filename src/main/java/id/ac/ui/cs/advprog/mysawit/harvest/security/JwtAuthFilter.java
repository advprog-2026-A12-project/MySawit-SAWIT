package id.ac.ui.cs.advprog.mysawit.harvest.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component("harvestJwtAuthFilter")
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(@Qualifier("harvestJwtUtil") JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/harvest");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().equals("/api/harvest/health")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Token tidak ditemukan");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.parseToken(token);

            UUID userId = jwtUtil.getUserId(claims);
            String role = jwtUtil.getRole(claims);

            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    )
            );

            chain.doFilter(request, response);

        } catch (Exception e) {
            sendUnauthorized(response,
                    "Token error (" + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}