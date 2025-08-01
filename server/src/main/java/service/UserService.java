package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        UserData user = new UserData(request.username(), hashedPassword, request.email());
        dataAccess.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth =  new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);
        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new Exception("unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth =  new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);
        return new LoginResult(request.username(), authToken);
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        AuthData auth = dataAccess.getAuth(request.authToken());
        if (auth == null) {
            throw new Exception("unauthorized");
        }

        dataAccess.deleteAuth(request.authToken());
        return new LogoutResult();
    }
}
