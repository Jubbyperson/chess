package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameConnectionManager {
    private final Map<Session, String> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addConnection(Session session, String username) {
        connections.put(session, username);
    }

    public void removeConnection(Session session) {
        connections.remove(session);
    }

    public void broadcastToAll(ServerMessage message) {
        String jsonMessage = gson.toJson(message);
        connections.keySet().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(jsonMessage);
                }
            } catch (IOException e) {
                // Handle error
            }
        });
    }
    public void broadcastToOthers(Session excludeSession, ServerMessage message) {
        String jsonMessage = gson.toJson(message);
        connections.keySet().stream()
                .filter(session -> session != excludeSession && session.isOpen())
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(jsonMessage);
                    } catch (IOException e) {
                        // Handle error
                    }
                });
    }
}