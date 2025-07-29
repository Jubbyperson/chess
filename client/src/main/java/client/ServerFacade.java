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
