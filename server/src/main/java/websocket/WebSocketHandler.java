package websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;

public class WebSocketHandler {
    private final Map<Integer, GameConnectionManager> gameConnections = new ConcurrentHashMap<>();
    private final Map<Integer, Map<String, ChessGame.TeamColor>> gamePlayers = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> gameObservers = new ConcurrentHashMap<>();
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

}
