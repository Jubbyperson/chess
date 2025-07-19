package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import handler.Handler;
import spark.*;

public class Server {
    private final Handler handler;

    public Server(){
        this.handler = new Handler(new DataAccessMemory());
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

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
