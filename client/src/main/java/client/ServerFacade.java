package client;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;
import java.util.List;


public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
        this.gson = new Gson();
    }
    public AuthData register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        var response = makeRequest("POST", "/user", request, RegisterResult.class);
        return new AuthData(response.authToken(), response.username());
    }

    public AuthData login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        var response = makeRequest("POST", "/session", request, LoginResult.class);
        return new AuthData(response.authToken(), response.username());
    }

    public void logout(String authToken) throws Exception {
        makeRequestWithAuth("DELETE", "/session", null, LogoutResult.class, authToken);
    }

    public List<GameEntry> listGames(String authToken) throws Exception {
        var response = makeRequestWithAuth("GET", "/game", null, ListGamesResult.class, authToken);
        return response.games();
    }

    public int createGame(String authToken, String gameName) throws Exception {
        var request = new CreateGameRequest(gameName);
        var response = makeRequestWithAuth("POST", "/game", request, CreateGameResult.class, authToken);
        return response.gameID();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = new JoinGameRequest(playerColor, gameID);
        makeRequestWithAuth("PUT", "/game", request, JoinGameResult.class, authToken);
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("authorization", authToken);

        if (request != null) {
            try (OutputStream os = connection.getOutputStream()) {
                String jsonRequest = gson.toJson(request);
                os.write(jsonRequest.getBytes());
            }
        }

        return handleResponse(connection, responseClass);
    }

    private <T> T handleResponse(HttpURLConnection connection, Class<T> responseClass) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (InputStream respBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                return gson.fromJson(reader, responseClass);
            }
        } else {
            try (InputStream respBody = connection.getErrorStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                var errorResponse = gson.fromJson(reader, ErrorResponse.class);
                throw new Exception(errorResponse.message().replace("Error: ", ""));
            }
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (request != null) {
            try (OutputStream os = connection.getOutputStream()) {
                String jsonRequest = gson.toJson(request);
                os.write(jsonRequest.getBytes());
            }
        }

        return handleResponse(connection, responseClass);
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

    public record GameEntry(int gameID, String whiteUsername, String blackUsername, String gameName) {}
}
