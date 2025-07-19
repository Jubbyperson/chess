package handler;

import dataaccess.DataAccess;
import service.ClearService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import service.requests.*;
import service.results.*;
import spark.Request;
import spark.Response;


public class Handler {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public Handler(DataAccess dataAccess){
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.clearService = new ClearService(dataAccess);
    }

    public Object register(Request request, Response response){
        try {
            RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
            if (registerRequest.username() == null || registerRequest.username().isEmpty() ||
                    registerRequest.password() == null || registerRequest.password().isEmpty() ||
                    registerRequest.email() == null || registerRequest.email().isEmpty()) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            RegisterResult result = userService.register(registerRequest);
            response.status(200);
            return gson.toJson(result);
        } catch (Exception e){
            if (e.getMessage().equals("already taken")){
                response.status(403);
            } else {
                response.status(400);
            }
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object login(Request req, Response res) {
        try {
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
            if (request.username() == null || request.username().isEmpty() ||
                    request.password() == null || request.password().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            LoginResult result = userService.login(request);
            res.status(200);
            return gson.toJson(result);
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            LogoutResult result = userService.logout(request);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = gameService.listGames(request);
            res.status(200);
            return gson.toJson(result);
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);
            if (request.gameName() == null || request.gameName().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            request = new CreateGameRequest(authToken, request.gameName());
            CreateGameResult result = gameService.createGame(request);
            res.status(200);
            return gson.toJson(result);
        } catch (Exception e) {
            if (e.getMessage().equals("unauthorized")) {
                res.status(401);
            } else {
                res.status(400);
            }
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
            if (request.playerColor() == null || request.playerColor().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }
            request = new JoinGameRequest(authToken, request.playerColor(), request.gameID());
            JoinGameResult result = gameService.joinGame(request);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            if (e.getMessage().equals("unauthorized")) {
                res.status(401);
            } else if (e.getMessage().equals("already taken")) {
                res.status(403);
            } else {
                res.status(400);
            }
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object clear(Request req, Response res) {
        try {
            ClearRequest request = new ClearRequest();
            ClearResult result = clearService.clear(request);
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message){}
}
