package id.ac.ui.cs.advprog.mysawit.garden.client;

import id.ac.ui.cs.advprog.mysawit.garden.client.dto.AuthUserResponse;
import id.ac.ui.cs.advprog.mysawit.garden.exception.InvalidUserRoleException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.UserNotFoundInAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * Client untuk berkomunikasi dengan Auth Service (MySawit-AUTH).
 * Mengikuti pola yang sama dengan harvest/client/AuthClient.java
 * untuk konsistensi arsitektur antar-modul.
 */
@Component("gardenAuthClient")
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    private final RestTemplate restTemplate;
    private final String authBaseUrl;

    public AuthClient(
            @Value("${auth.service.url:https://mysawit-auth.onrender.com/api/v1}") String authBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.authBaseUrl = authBaseUrl;
    }

    /**
     * Ambil data user dari Auth Service berdasarkan userId.
     * Digunakan untuk validasi role dan pengambilan nama/email.
     *
     * @param userId ID user yang ingin dicari
     * @param token  JWT Bearer token untuk otorisasi
     * @return AuthUserResponse berisi data user
     * @throws UserNotFoundInAuthException jika user tidak ditemukan di Auth Service
     */
    @SuppressWarnings("unchecked")
    public AuthUserResponse getUserById(UUID userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    authBaseUrl + "/users/" + userId,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            if (response.getBody() == null) {
                throw new UserNotFoundInAuthException(userId);
            }

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            if (data == null) {
                throw new UserNotFoundInAuthException(userId);
            }

            // Auth Service UserDetailResponseData menggunakan Lombok @Value
            // dengan `boolean isActive`. Jackson serializes ini sebagai "active"
            // (strips prefix "is" untuk primitive boolean).
            // Kita handle kedua kemungkinan: "isActive" dan "active"
            Boolean isActive = null;
            if (data.containsKey("isActive")) {
                isActive = (Boolean) data.get("isActive");
            } else if (data.containsKey("active")) {
                isActive = (Boolean) data.get("active");
            }

            // id bisa berupa String UUID
            String idStr = data.get("id") != null ? data.get("id").toString() : null;

            return AuthUserResponse.builder()
                    .id(idStr)
                    .username((String) data.get("username"))
                    .email((String) data.get("email"))
                    .name((String) data.get("name"))
                    .role((String) data.get("role"))
                    .isActive(isActive)
                    .mandorCertificationNumber((String) data.get("mandorCertificationNumber"))
                    .build();

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User {} not found in Auth Service: {}", userId, ex.getMessage());
            throw new UserNotFoundInAuthException(userId);
        } catch (HttpClientErrorException ex) {
            log.warn("Auth Service error for user {}: {} {}", userId,
                    ex.getStatusCode(), ex.getMessage());
            throw new UserNotFoundInAuthException(userId);
        } catch (RestClientException ex) {
            log.error("Failed to connect to Auth Service: {}", ex.getMessage());
            throw new UserNotFoundInAuthException(userId);
        }
    }

    /**
     * Validasi bahwa user memiliki role MANDOR.
     * Digunakan sebelum assign mandor ke kebun.
     */
    public AuthUserResponse validateMandor(UUID mandorId, String token) {
        AuthUserResponse user = getUserById(mandorId, token);
        if (!"MANDOR".equals(user.getRole())) {
            throw new InvalidUserRoleException("MANDOR", user.getRole());
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UserNotFoundInAuthException(mandorId);
        }
        return user;
    }

    /**
     * Validasi bahwa user memiliki role SUPIR_TRUK.
     * Digunakan sebelum assign supir ke kebun.
     */
    public AuthUserResponse validateSupirTruk(UUID supirId, String token) {
        AuthUserResponse user = getUserById(supirId, token);
        if (!"SUPIR_TRUK".equals(user.getRole())) {
            throw new InvalidUserRoleException("SUPIR_TRUK", user.getRole());
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UserNotFoundInAuthException(supirId);
        }
        return user;
    }
}
