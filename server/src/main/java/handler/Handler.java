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
            RegisterResult result = userService.register(registerRequest);
            response.status(200);
            return gson.toJson(result);
        } catch (Exception e){
            if (e.getMessage().equals("already taken")){
                response.status(403);
            } else {
                response.status(400);
            }
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()))
        }
    }

    public Object login(Request req, Response res) {
        try {
            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
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


    private record ErrorResponse(String message){}
}
