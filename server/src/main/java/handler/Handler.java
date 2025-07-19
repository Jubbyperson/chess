package handler;

import dataaccess.DataAccess;
import service.ClearService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import service.requests.RegisterRequest;
import service.results.RegisterResult;
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
        }
    }
}
