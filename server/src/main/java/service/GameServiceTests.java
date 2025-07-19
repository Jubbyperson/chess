package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMemory;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import service.requests.*;
import service.results.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private DataAccessMemory dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = new DataAccessMemory();
        gameService = new GameService(dataAccess);
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws Exception {
        // Arrange
        String authToken = "valid-token";
        dataAccess.createAuth(new AuthData(authToken, "testuser"));
        CreateGameRequest request = new CreateGameRequest(authToken, "Test Game");

        // Act
        CreateGameResult result = gameService.createGame(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() {
        // Arrange
        CreateGameRequest request = new CreateGameRequest("invalid-token", "Test Game");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> gameService.createGame(request));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws Exception {
        // Arrange
        String authToken = "valid-token";
        dataAccess.createAuth(new AuthData(authToken, "testuser"));
        dataAccess.createGame(new GameData(1, null, null, "Game 1", new ChessGame()));
        dataAccess.createGame(new GameData(2, null, null, "Game 2", new ChessGame()));
        ListGamesRequest request = new ListGamesRequest(authToken);

        // Act
        ListGamesResult result = gameService.listGames(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.games());
        assertEquals(2, result.games().size());
    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() {
        // Arrange
        ListGamesRequest request = new ListGamesRequest("invalid-token");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> gameService.listGames(request));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() throws Exception {
        // Arrange
        String authToken = "valid-token";
        dataAccess.createAuth(new AuthData(authToken, "testuser"));
        dataAccess.createGame(new GameData(1, null, null, "Test Game", new ChessGame()));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 1);

        // Act
        JoinGameResult result = gameService.joinGame(request);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Join Game Spot Already Taken")
    public void joinGameSpotAlreadyTaken() throws Exception {
        // Arrange
        String authToken1 = "valid-token-1";
        String authToken2 = "valid-token-2";
        dataAccess.createAuth(new AuthData(authToken1, "user1"));
        dataAccess.createAuth(new AuthData(authToken2, "user2"));
        dataAccess.createGame(new GameData(1, null, null, "Test Game", new ChessGame()));

        // First user joins as white
        JoinGameRequest request1 = new JoinGameRequest(authToken1, "WHITE", 1);
        gameService.joinGame(request1);

        // Second user tries to join as white
        JoinGameRequest request2 = new JoinGameRequest(authToken2, "WHITE", 1);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> gameService.joinGame(request2));
        assertEquals("already taken", exception.getMessage());
    }

    @Test
    @DisplayName("Join Game Invalid Game ID")
    public void joinGameInvalidGameId() throws DataAccessException {
        // Arrange
        String authToken = "valid-token";
        dataAccess.createAuth(new AuthData(authToken, "testuser"));
        JoinGameRequest request = new JoinGameRequest(authToken, "WHITE", 999);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> gameService.joinGame(request));
        assertEquals("bad request", exception.getMessage());
    }
}