package client;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private GameplayUI gameplayUI;
    private final Gson gson = new Gson();

    public WebSocketClient(GameplayUI gameplayUI) {
        this.gameplayUI = gameplayUI;
    }

    public void connect(String serverUrl) throws Exception {
        URI uri = new URI(serverUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    public void disconnect() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        if (session != null && session.isOpen()) {
            String message = gson.toJson(command);
            session.getBasicRemote().sendText(message);
        }
    }

    public void sendMoveCommand(String authToken, int gameID, chess.ChessMove move) throws Exception {
        if (session != null && session.isOpen()) {
            // Create the JSON manually to match test expectations
            String moveJson = String.format(
                    "{\"commandType\":\"MAKE_MOVE\",\"authToken\":\"%s\",\"gameID\":%d," +
                    "\"move\":{\"start\":{\"row\":%d,\"col\":%d},\"end\":{\"row\":%d,\"col\":%d}%s}}",
                    authToken, gameID,
                    move.getStartPosition().getRow(), move.getStartPosition().getColumn(),
                    move.getEndPosition().getRow(), move.getEndPosition().getColumn(),
                    move.getPromotionPiece() != null ?
                            ",\"promotionPiece\":\"" + move.getPromotionPiece() + "\"" : ""
            );
            session.getBasicRemote().sendText(moveJson);
        }
    }

    @OnMessage
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

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to game");
    }

    @OnClose
    @SuppressWarnings("unused")
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from game");
    }

    @OnError
    @SuppressWarnings("unused")
    public void onError(Session session, Throwable throwable) {
        gameplayUI.displayError("WebSocket error: " + throwable.getMessage());
    }
}