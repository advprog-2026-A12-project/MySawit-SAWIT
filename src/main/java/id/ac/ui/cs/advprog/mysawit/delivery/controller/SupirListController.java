package id.ac.ui.cs.advprog.mysawit.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupirListController {

    private final RestTemplate restTemplate;

    @Value("${auth.service.base-url:http://localhost:8001}")
    private String authBaseUrl;

    @Value("${auth.admin.email:admin@mysawit.local}")
    private String adminEmail;

    @Value("${auth.admin.password:change-this-to-a-strong-secret}")
    private String adminPassword;

    /**
     * Proxy endpoint: allows Mandor to get the Supir Truk list.
     * SAWIT backend logs in as Admin internally, then calls AUTH to fetch user list.
     */
    @GetMapping("/supir-list")
    public ResponseEntity<?> getSupirList(
            @RequestAttribute(value = "role", required = false) String role,
            @RequestParam(required = false) String name) {

        if (!"MANDOR".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: hanya Mandor atau Admin yang bisa melihat daftar Supir");
        }

        try {
            // Step 1: Login as Admin internally to get a valid JWT
            String loginUrl = authBaseUrl + "/api/v1/auth/login";
            Map<String, String> loginBody = Map.of("email", adminEmail, "password", adminPassword);

            HttpHeaders loginHeaders = new HttpHeaders();
            loginHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(loginBody, loginHeaders);

            @SuppressWarnings("unchecked")
            Map<String, Object> loginResponse = restTemplate.postForObject(loginUrl, loginRequest, Map.class);

            if (loginResponse == null || !loginResponse.containsKey("data")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Gagal login ke Auth service");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> loginData = (Map<String, Object>) loginResponse.get("data");
            String adminToken = (String) loginData.get("accessToken");

            // Step 2: Use the Admin JWT to fetch the Supir Truk list
            String url = authBaseUrl + "/api/v1/users?role=SUPIR_TRUK&size=100"
                    + (name != null && !name.isBlank() ? "&name=" + name : "");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, Object.class);

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Gagal menghubungi Auth service: " + e.getMessage());
        }
    }
}
