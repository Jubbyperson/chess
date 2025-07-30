package client;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        var auth = facade.register("user1", "pass", "user1@email.com");
        assertNotNull(auth);
        assertTrue(auth.authToken().length() > 5);
    }

    @Test
    void registerDuplicateFails() throws Exception {
        facade.register("user2", "pass", "user2@email.com");
        Exception ex = assertThrows(Exception.class, () -> {
            facade.register("user2", "pass", "user2@email.com");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("already taken"));
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register("user3", "pass", "user3@email.com");
        var auth = facade.login("user3", "pass");
        assertNotNull(auth);
        assertEquals("user3", auth.username());
    }

    @Test
    void loginFails() {
        Exception ex = assertThrows(Exception.class, () -> {
            facade.login("nope", "wrong");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void logoutSuccess() throws Exception {
        var auth = facade.register("user4", "pass", "user4@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    void logoutFails() {
        Exception ex = assertThrows(Exception.class, () -> {
            facade.logout("badtoken");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void createGameSuccess() throws Exception {
        var auth = facade.register("user5", "pass", "user5@email.com");
        int gameId = facade.createGame(auth.authToken(), "game1");
        assertTrue(gameId > 0);
    }

    @Test
    void createGameFails() {
        Exception ex = assertThrows(Exception.class, () -> {
            facade.createGame("badtoken", "game2");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void listGamesSuccess() throws Exception {
        var auth = facade.register("user6", "pass", "user6@email.com");
        facade.createGame(auth.authToken(), "game3");
        var games = facade.listGames(auth.authToken());
        assertFalse(games.isEmpty());
    }

    @Test
    void listGamesFails() {
        Exception ex = assertThrows(Exception.class, () -> {
            facade.listGames("badtoken");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }
}
