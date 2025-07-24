package dataaccess;

import model.UserData;
import model.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
    private SqlDataAccess dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new SqlDataAccess();
        dao.clearUsers();
        dao.clearGames();
        dao.clearAuth();
    }

    @Test
    void createUser_success() throws Exception {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        dao.createUser(user);
        UserData fromDb = dao.getUser("alice");
        assertNotNull(fromDb);
        assertEquals("alice", fromDb.username());
        assertEquals("alice@example.com", fromDb.email());
    }

    @Test
    void createUser_duplicate_fails() throws Exception {
        UserData user = new UserData("bob", "password456", "bob@example.com");
        dao.createUser(user);
        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }

    @Test
    void getUser_success() throws Exception {
        UserData user = new UserData("carol", "password789", "carol@example.com");
        dao.createUser(user);
        UserData fromDb = dao.getUser("carol");
        assertNotNull(fromDb);
        assertEquals("carol", fromDb.username());
    }

    @Test
    void getUser_notFound_returnsNull() throws Exception {
        UserData fromDb = dao.getUser("nonexistent");
        assertNull(fromDb);
    }

    @Test
    void clearUsers_success() throws Exception {
        UserData user = new UserData("dave", "password000", "dave@example.com");
        dao.createUser(user);
        dao.clearUsers();
        assertNull(dao.getUser("dave"));
    }

    @Test
    void createGame_success() throws Exception {
        GameData game = new GameData(1, null, null, "Test Game", null); // Fill in with actual ChessGame if needed
        dao.createGame(game);
        GameData fromDb = dao.getGame(1);
        assertNotNull(fromDb);
        assertEquals("Test Game", fromDb.gameName());
    }

    @Test
    void createGame_duplicate_fails() throws Exception {
        GameData game = new GameData(2, null, null, "Game2", null);
        dao.createGame(game);
        assertThrows(DataAccessException.class, () -> dao.createGame(game));
    }







}
