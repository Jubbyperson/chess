package client;

import com.google.gson.Gson;
import model.*;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
        this.gson = new Gson();
    }

    private record RegisterRequest(String username, String password, String email) {}
    private record LoginRequest(String username, String password) {}
    private record CreateGameRequest(String gameName) {}
    private record JoinGameRequest(String playerColor, int gameID) {}
    private record RegisterResult(String username, String authToken) {}
    private record LoginResult(String username, String authToken) {}
    private record LogoutResult() {}
    private record ListGamesResult(List<GameEntry> games) {}
    private record CreateGameResult(int gameID) {}
    private record JoinGameResult() {}
    private record ErrorResponse(String message) {}
}
