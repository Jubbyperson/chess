package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import service.results.*;
import service.requests.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        if (dataAccess.getUser(request.username()) != null) {
            throw new Exception("already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth =  new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);
        return new RegisterResult(request.username(), authToken);
    }
}
