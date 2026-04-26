package id.ac.ui.cs.advprog.mysawit.delivery.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DeliverySecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    private DeliverySecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new DeliverySecurityConfig(jwtAuthFilter);
    }

    @Test
    void corsConfigurationSource_shouldNotBeNull() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        assertThat(source).isNotNull();
    }

    @Test
    void corsConfigurationSource_shouldAllowLocalhostOrigin() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/deliveries");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains("http://localhost:3000");
    }

    @Test
    void corsConfigurationSource_shouldAllowProductionOrigin() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/deliveries");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains("https://mysawit-fe.onrender.com");
    }

    @Test
    void corsConfigurationSource_shouldAllowRequiredHttpMethods() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/deliveries");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }

    @Test
    void corsConfigurationSource_shouldAllowAllHeaders() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/deliveries");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedHeaders()).contains("*");
    }

    @Test
    void corsConfigurationSource_shouldAllowCredentials() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/deliveries");
        CorsConfiguration corsConfig = source.getCorsConfiguration(request);

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    @Test
    void corsConfigurationSource_shouldApplyToAllPaths() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        for (String uri : new String[] { "/api/deliveries", "/api/supir-list", "/api/deliveries/1/status" }) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(uri);
            CorsConfiguration corsConfig = source.getCorsConfiguration(request);
            assertThat(corsConfig)
                    .as("CORS config seharusnya ada untuk uri: " + uri)
                    .isNotNull();
        }
    }
}
