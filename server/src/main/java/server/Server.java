package server;

import dataaccess.SqlDataAccess;
import handler.Handler;
import spark.*;
import websocket.WebSocketHandler;

public class Server {
    private final Handler handler;

    public Server(){
        try {
            this.handler = new Handler(new SqlDataAccess());
        } catch (dataaccess.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", handler::clear);
        Spark.post("/user", handler::register);
        Spark.post("/session", handler::login);
        Spark.delete("/session", handler::logout);
        Spark.get("/game", handler::listGames);
        Spark.post("/game", handler::createGame);
        Spark.put("/game", handler::joinGame);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body("{\"message\":\"Error: " + e.getMessage() + "\"}");
        });
        Spark.webSocket("/ws", new WebSocketHandler(handler.getDataAccess()));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
