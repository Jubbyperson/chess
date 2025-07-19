package service;

import dataaccess.DataAccessMemory;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.requests.*;
import service.results.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private ClearService clearService;
    private DataAccessMemory dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new DataAccessMemory();
        clearService = new ClearService(dataAccess);
    }

    @Test
    @DisplayName("Clear Success")
    public void clearSuccess() throws Exception {
        // Arrange - add some data first
        dataAccess.createUser(new UserData("testuser", "password", "test@email.com"));
        dataAccess.createAuth(new AuthData("token", "testuser"));
        dataAccess.createGame(new GameData(1, null, null, "Test Game", new chess.ChessGame()));

        // Verify data exists
        assertNotNull(dataAccess.getUser("testuser"));
        assertNotNull(dataAccess.getAuth("token"));
        assertFalse(dataAccess.listGames().isEmpty());

        // Act
        ClearRequest request = new ClearRequest();
        ClearResult result = clearService.clear(request);

        // Assert
        assertNotNull(result);
        assertNull(dataAccess.getUser("testuser"));
        assertNull(dataAccess.getAuth("token"));
        assertTrue(dataAccess.listGames().isEmpty());
    }
}