package team9.ddang.member.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import team9.ddang.member.jwt.service.JwtService;
import team9.ddang.member.service.CookieService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Value("${jwt.access.header}")
    private String accessHeader = "Authorization";

    private JwtService jwtService;

    @Mock
    private CookieService cookieService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(cookieService, redisTemplate);

        // Set values manually
        jwtService.setSecretKey("test-secret");
        jwtService.setAccessTokenExpirationPeriod(600000L); // 10 minutes
        jwtService.setRefreshTokenExpirationPeriod(1209600000L); // 14 days
        jwtService.setAccessHeader("Authorization");
    }

    @Test
    void testCreateAccessToken() {
        // Given
        String email = "user@test.com";
        String provider = "google";

        // When
        String accessToken = jwtService.createAccessToken(email, provider);

        // Then
        assertNotNull(accessToken);
        assertTrue(jwtService.isTokenValid(accessToken));
        assertEquals(email, jwtService.extractEmail(accessToken).orElse(null));
        assertEquals(provider, jwtService.extractProvider(accessToken).orElse(null));
    }

    @Test
    void testExtractAccessToken() {
        // Given
        String token = "Bearer test-token";
        when(request.getHeader(accessHeader)).thenReturn(token);

        // When
        Optional<String> extractedToken = jwtService.extractAccessToken(request);

        // Then
        assertTrue(extractedToken.isPresent());
        assertEquals("test-token", extractedToken.get());
    }

    @Test
    void testRtkToAtk() {
        // Given
        String email = "user@google.com";
        String refreshToken = jwtService.createRefreshToken(email);

        // When
        Optional<String> extractedEmail = jwtService.extractEmailFromRefreshToken(refreshToken);

        // Then
        assertTrue(extractedEmail.isPresent());
        assertEquals(email, extractedEmail.get());

        // Create a new Access Token
        String newAccessToken = jwtService.createAccessToken(email, "GOOGLE");
        assertNotNull(newAccessToken);
        assertTrue(jwtService.isTokenValid(newAccessToken));
        assertEquals(email, jwtService.extractEmail(newAccessToken).orElse(null));
    }
}