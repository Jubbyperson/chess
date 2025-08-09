package client;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.net.URI;

@WebSocket
public class WebSocketClient {
    private Session session;
    private GameplayUI gameplayUI;
    private final Gson gson = new Gson();

    public WebSocketClient(GameplayUI gameplayUI) {
        this.gameplayUI = gameplayUI;
    }

    public void connect(String serverUrl) throws Exception {
        URI uri = new URI(serverUrl);
        org.eclipse.jetty.websocket.client.WebSocketClient client = new org.eclipse.jetty.websocket.client.WebSocketClient();
        client.start();

        ClientUpgradeRequest request = new ClientUpgradeRequest();
        this.session = client.connect(this, uri, request).get();
    }

    public void disconnect() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        if (session != null && session.isOpen()) {
            String message = gson.toJson(command);
            session.getRemote().sendString(message);
        }
    }

    public void sendMoveCommand(String authToken, int gameID, chess.ChessMove move) throws Exception {
        if (session != null && session.isOpen()) {
            // Create the JSON manually to match test expectations
            String moveJson = String.format(
                    "{\"commandType\":\"MAKE_MOVE\",\"authToken\":\"%s\",\"gameID\":%d,\"move\":{\"start\":{\"row\":%d,\"col\":%d},\"end\":{\"row\":%d,\"col\":%d}%s}}",
                    authToken, gameID,
                    move.getStartPosition().getRow(), move.getStartPosition().getColumn(),
                    move.getEndPosition().getRow(), move.getEndPosition().getColumn(),
                    move.getPromotionPiece() != null ?
                            ",\"promotionPiece\":\"" + move.getPromotionPiece() + "\"" : ""
            );
            session.getRemote().sendString(moveJson);
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGameMessage loadGameMsg = gson.fromJson(message, LoadGameMessage.class);
                    gameplayUI.updateGame(loadGameMsg.getGame());
                }
                case ERROR -> {
                    ErrorMessage errorMsg = gson.fromJson(message, ErrorMessage.class);
                    gameplayUI.displayError(errorMsg.getErrorMessage());
                }
                case NOTIFICATION -> {
                    NotificationMessage notificationMsg = gson.fromJson(message, NotificationMessage.class);
                    gameplayUI.displayNotification(notificationMsg.getMessage());
                }
            }
        } catch (Exception e) {
            gameplayUI.displayError("Error processing server message: " + e.getMessage());
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected to game");
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Disconnected from game");
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        gameplayUI.displayError("WebSocket error: " + error.getMessage());
    }
}