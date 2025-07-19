package service;

import dataaccess.DataAccessMemory;
import org.junit.jupiter.api.*;
import service.requests.*;
import service.results.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;
    private DataAccessMemory dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new DataAccessMemory();
        userService = new UserService(dataAccess);
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@email.com");

        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
    }

    @Test
    @DisplayName("Register Duplicate Username")
    public void registerDuplicateUsername() {
        RegisterRequest request1 = new RegisterRequest("testuser", "password", "test@email.com");
        RegisterRequest request2 = new RegisterRequest("testuser", "password2", "test2@email.com");

        assertDoesNotThrow(() -> userService.register(request1));
        Exception exception = assertThrows(Exception.class, () -> userService.register(request2));
        assertEquals("already taken", exception.getMessage());
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "test@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testuser", "password");

        LoginResult result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
    }

    @Test
    @DisplayName("Login Invalid Credentials")
    public void loginInvalidCredentials() {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "test@email.com");
        assertDoesNotThrow(() -> userService.register(registerRequest));
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        Exception exception = assertThrows(Exception.class, () -> userService.login(loginRequest));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "test@email.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());

        LogoutResult result = userService.logout(logoutRequest);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Logout Invalid Token")
    public void logoutInvalidToken() {
        LogoutRequest logoutRequest = new LogoutRequest("invalid-token");

        Exception exception = assertThrows(Exception.class, () -> userService.logout(logoutRequest));
        assertEquals("unauthorized", exception.getMessage());
    }
}