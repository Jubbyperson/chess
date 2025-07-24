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









}
