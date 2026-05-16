package id.ac.ui.cs.advprog.mysawit.garden.client;

import id.ac.ui.cs.advprog.mysawit.garden.client.dto.AuthUserResponse;
import id.ac.ui.cs.advprog.mysawit.garden.exception.InvalidUserRoleException;
import id.ac.ui.cs.advprog.mysawit.garden.exception.UserNotFoundInAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    private AuthClient authClient;
    private MockRestServiceServer mockServer;
    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        authClient = new AuthClient("http://localhost:8001/api/v1");

        // Access the internal RestTemplate to create MockRestServiceServer
        Field restTemplateField = AuthClient.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restTemplateField.get(authClient);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void getUserByIdSuccessReturnsUser() {
        String responseJson = """
                {
                    "status": "success",
                    "data": {
                        "id": "11111111-1111-1111-1111-111111111111",
                        "username": "mandor1",
                        "email": "mandor@sawit.com",
                        "name": "Budi Mandor",
                        "role": "MANDOR",
                        "active": true
                    }
                }
                """;

        mockServer.expect(requestTo("http://localhost:8001/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        AuthUserResponse result = authClient.getUserById(userId, "test-token");

        assertNotNull(result);
        assertEquals("Budi Mandor", result.getName());
        assertEquals("MANDOR", result.getRole());
        mockServer.verify();
    }

    @Test
    void getUserByIdNotFoundThrowsException() {
        mockServer.expect(requestTo("http://localhost:8001/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(UserNotFoundInAuthException.class,
                () -> authClient.getUserById(userId, "test-token"));

        mockServer.verify();
    }

    @Test
    void getUserByIdServerErrorThrowsException() {
        mockServer.expect(requestTo("http://localhost:8001/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(UserNotFoundInAuthException.class,
                () -> authClient.getUserById(userId, "test-token"));

        mockServer.verify();
    }

    @Test
    void validateMandorCorrectRoleReturnsUser() {
        String responseJson = """
                {
                    "status": "success",
                    "data": {
                        "id": "11111111-1111-1111-1111-111111111111",
                        "username": "mandor1",
                        "email": "mandor@sawit.com",
                        "name": "Budi Mandor",
                        "role": "MANDOR",
                        "active": true
                    }
                }
                """;

        mockServer.expect(requestTo("http://localhost:8001/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        AuthUserResponse result = authClient.validateMandor(userId, "test-token");

        assertEquals("MANDOR", result.getRole());
        mockServer.verify();
    }

    @Test
    void validateMandorWrongRoleThrowsException() {
        String responseJson = """
                {
                    "status": "success",
                    "data": {
                        "id": "11111111-1111-1111-1111-111111111111",
                        "username": "supir1",
                        "email": "supir@sawit.com",
                        "name": "Dedi Supir",
                        "role": "SUPIR_TRUK",
                        "active": true
                    }
                }
                """;

        mockServer.expect(requestTo("http://localhost:8001/api/v1/users/" + userId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        assertThrows(InvalidUserRoleException.class,
                () -> authClient.validateMandor(userId, "test-token"));

        mockServer.verify();
    }
}
