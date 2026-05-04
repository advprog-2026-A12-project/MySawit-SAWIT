package id.ac.ui.cs.advprog.mysawit.harvest.client;

import id.ac.ui.cs.advprog.mysawit.harvest.dto.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
public class AuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // Nilai default ini dipakai kalau environment variable auth.service.url kosong
    @Value("${auth.service.url:https://mysawit-auth.onrender.com/api/v1}")
    private String authUrl;

    private HttpHeaders createAuthHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    public UserProfile getMe(String token) {

        ResponseEntity<Map> response = restTemplate.exchange(
                authUrl + "/users/me",
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeader(token)),
                Map.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Auth response kosong");
        }

        Map body = (Map) response.getBody().get("data");
        if (body == null) {
            throw new RuntimeException("Data user tidak ditemukan");
        }

        UserProfile user = new UserProfile();
        user.setId(UUID.fromString((String) body.get("id")));
        user.setRole((String) body.get("role"));

        Map roleSpecificData = (Map) body.get("roleSpecificData");
        if (roleSpecificData != null && roleSpecificData.get("assignedMandor") != null) {
            Map assignedMandor = (Map) roleSpecificData.get("assignedMandor");
            user.setMandorId(UUID.fromString((String) assignedMandor.get("id")));
        }

        return user;
    }

    public UUID getMandorIdFromUser(String token) {
        return getMe(token).getMandorId();
    }
}