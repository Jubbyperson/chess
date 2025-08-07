package websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import chess.*;
import com.google.gson.*;
import dataaccess.DataAccess;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    private final Map<Integer, GameConnectionManager> gameConnections = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, ChessGame.TeamColor>> gamePlayers = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> gameObservers = new ConcurrentHashMap<>();
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    @OnWebSocketConnect
    public void onConnect(Session session) {
        // Connection established, but we wait for CONNECT command
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // First parse as UserGameCommand
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            // Validate auth token
            UserData user = dataAccess.getUserByAuthToken(command.getAuthToken());
            if (user == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            // If it's a MAKE_MOVE command, parse the move separately
            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
                if (jsonObject.has("move")) {
                    ChessMove move = gson.fromJson(jsonObject.get("move"), ChessMove.class);
                    handleMakeMove(session, command, move, user);
                } else {
                    sendError(session, "No move provided");
                }
            } else {
                handleCommand(session, command, user);
            }
        } catch (Exception e) {
            sendError(session, "Invalid message format");
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        // Remove from all game connections
        for (GameConnectionManager manager : gameConnections.values()) {
            manager.removeConnection(session);
        }
    }

    private void handleCommand(Session session, UserGameCommand command, UserData user) {
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command, user);
                case LEAVE -> handleLeave(session, command, user);
                case RESIGN -> handleResign(session, command, user);
            }
        } catch (Exception e) {
            sendError(session, "Error processing command: " + e.getMessage());
        }
    }


}
