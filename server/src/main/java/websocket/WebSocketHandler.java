package websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import chess.*;
import com.google.gson.*;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

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

    private void handleConnect(Session session, UserGameCommand command, UserData user) {
        try {
            GameData game = dataAccess.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Game not found");
                return;
            }

            GameConnectionManager manager = gameConnections.computeIfAbsent(
                    command.getGameID(), k -> new GameConnectionManager());

            manager.addConnection(session, user.username());

            // Send LOAD_GAME to connecting user
            sendLoadGame(session, game.game());

            // Send notification to other users
            String notificationMsg = user.username() + " joined the game";
            manager.broadcastToOthers(session, new NotificationMessage(notificationMsg));
        } catch (Exception e) {
            sendError(session, "Error connecting to game: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command, ChessMove move, UserData user) {
        try {
            GameData gameData = dataAccess.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
            }

            ChessGame game = gameData.game();

            // Check if move is valid
            try {
                game.makeMove(move);
            } catch (Exception e) {
                sendError(session, "Invalid move: " + e.getMessage());
                return;
            }

            // Update game in database
            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUser(),
                    gameData.blackUser(), gameData.gameName(), game);
            dataAccess.updateGame(updatedGame);

            // Broadcast LOAD_GAME to all players
            GameConnectionManager manager = gameConnections.get(command.getGameID());
            if (manager != null) {
                manager.broadcastToAll(new LoadGameMessage(game));

                // Send notification to others about the move
                String moveDescription = user.username() + " moved " +
                        move.getStartPosition() + " to " + move.getEndPosition();
                manager.broadcastToOthers(session, new NotificationMessage(moveDescription));

                if (game.isInCheckmate(game.getTeamTurn())) {
                    manager.broadcastToAll(new NotificationMessage(
                            game.getTeamTurn() + " is in checkmate"));
                } else if (game.isInCheck(game.getTeamTurn())) {
                    manager.broadcastToAll(new NotificationMessage(
                            game.getTeamTurn() + " is in check"));
                }
            }
        } catch (Exception e) {
            sendError(session, "Error making move: " + e.getMessage());
        }
    }
    private void handleLeave(Session session, UserGameCommand command, UserData user) {
        GameConnectionManager manager = gameConnections.get(command.getGameID());
        if (manager != null) {
            manager.removeConnection(session);

            // Send notification to others
            manager.broadcastToAll(new NotificationMessage(user.username() + " left the game"));
        }
    }

    private void handleResign(Session session, UserGameCommand command, UserData user) {
        try {
            GameData gameData = dataAccess.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
            }

            ChessGame game = gameData.game();
            if (game.isGameOver()) {
                sendError(session, "Game is already over");
                return;
            }

            // Mark game as over
            game.setGameOver(true);

            GameData updatedGame = new GameData(gameData.gameID(), gameData.whiteUser(),
                    gameData.blackUser(), gameData.gameName(), game);
            dataAccess.updateGame(updatedGame);

            // Broadcast resignation notification to ALL players
            GameConnectionManager manager = gameConnections.get(command.getGameID());
            if (manager != null) {
                manager.broadcastToAll(new NotificationMessage(user.username() + " resigned"));
            }
        } catch (Exception e) {
            sendError(session, "Error resigning: " + e.getMessage());
        }
    }
    private void sendLoadGame(Session session, ChessGame game) {
        try {
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(game)));
        } catch (IOException e) {
            // Handle error
        }
    }

    private void sendError(Session session, String errorMessage) {
        try {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(errorMessage)));
        } catch (IOException e) {
            // Handle error
        }
    }
}
