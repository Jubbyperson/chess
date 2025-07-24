package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class SqlDataAccessTests {
    private SqlDataAccess dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new SqlDataAccess();
        dao.clearUsers();
        dao.clearGames();
        dao.clearAuth();
    }

    // ------------------- User Tests -------------------
    @Test
    void createUserSuccess() throws Exception {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        dao.createUser(user);
        UserData fromDb = dao.getUser("alice");
        assertNotNull(fromDb);
        assertEquals("alice", fromDb.username());
        assertEquals("alice@example.com", fromDb.email());
    }

    @Test
    void createUserDuplicateFails() throws Exception {
        UserData user = new UserData("bob", "password456", "bob@example.com");
        dao.createUser(user);
        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }

    @Test
    void getUserSuccess() throws Exception {
        UserData user = new UserData("carol", "password789", "carol@example.com");
        dao.createUser(user);
        UserData fromDb = dao.getUser("carol");
        assertNotNull(fromDb);
        assertEquals("carol", fromDb.username());
    }

    @Test
    void getUserNotFoundReturnsNull() throws Exception {
        UserData fromDb = dao.getUser("nonexistent");
        assertNull(fromDb);
    }

    @Test
    void clearUsersSuccess() throws Exception {
        UserData user = new UserData("dave", "password000", "dave@example.com");
        dao.createUser(user);
        dao.clearUsers();
        assertNull(dao.getUser("dave"));
    }

    // ------------------- Game Tests -------------------
    @Test
    void createGameSuccess() throws Exception {
        GameData game = new GameData(0, null, null, "Test Game", null); // Let DB assign ID
        dao.createGame(game);
        List<GameData> games = dao.listGames();
        boolean found = false;
        for (GameData g : games) {
            if ("Test Game".equals(g.gameName())) { found = true; }
        }
        assertTrue(found);
    }

    @Test
    void getGameSuccess() throws Exception {
        GameData game = new GameData(0, null, null, "Game3", null);
        dao.createGame(game);
        List<GameData> games = dao.listGames();
        GameData foundGame = null;
        for (GameData g : games) {
            if ("Game3".equals(g.gameName())) { foundGame = g; }
        }
        assertNotNull(foundGame);
        assertEquals("Game3", foundGame.gameName());
    }

    @Test
    void getGameNotFoundReturnsNull() throws Exception {
        GameData fromDb = dao.getGame(999999); // Use a very high ID that won't exist
        assertNull(fromDb);
    }

    @Test
    void listGamesSuccess() throws Exception {
        dao.createGame(new GameData(0, null, null, "Game4", null));
        dao.createGame(new GameData(0, null, null, "Game5", null));
        List<GameData> games = dao.listGames();
        boolean found4 = false, found5 = false;
        for (GameData g : games) {
            if ("Game4".equals(g.gameName())) { found4 = true; }
            if ("Game5".equals(g.gameName())) { found5 = true; }
        }
        assertTrue(found4 && found5);
    }

    @Test
    void clearGamesSuccess() throws Exception {
        dao.createGame(new GameData(0, null, null, "Game6", null));
        dao.clearGames();
        List<GameData> games = dao.listGames();
        assertTrue(games.isEmpty());
    }

    // ------------------- Auth Tests -------------------
    @Test
    void createAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token1", "alice");
        dao.createAuth(auth);
        AuthData fromDb = dao.getAuth("token1");
        assertNotNull(fromDb);
        assertEquals("alice", fromDb.username());
    }

    @Test
    void createAuthDuplicateFails() throws Exception {
        AuthData auth = new AuthData("token2", "bob");
        dao.createAuth(auth);
        assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
    }

    @Test
    void getAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token3", "carol");
        dao.createAuth(auth);
        AuthData fromDb = dao.getAuth("token3");
        assertNotNull(fromDb);
        assertEquals("carol", fromDb.username());
    }

    @Test
    void getAuthNotFoundReturnsNull() throws Exception {
        AuthData fromDb = dao.getAuth("notatoken");
        assertNull(fromDb);
    }

    @Test
    void deleteAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token4", "dave");
        dao.createAuth(auth);
        dao.deleteAuth("token4");
        assertNull(dao.getAuth("token4"));
    }

    @Test
    void clearAuthSuccess() throws Exception {
        dao.createAuth(new AuthData("token5", "eve"));
        dao.clearAuth();
        assertNull(dao.getAuth("token5"));
    }
}
