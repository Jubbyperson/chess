package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class DataAccessMemory implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }

    @Override
    public void clearAuth() throws DataAccessException {
        authTokens.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        int newGameID = getNextGameID();
        GameData gameWithID = new GameData(newGameID, game.whiteUser(), game.blackUser(), 
                                         game.gameName(), game.game());
        games.put(newGameID, gameWithID);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        authTokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

    @Override
    public UserData getUserByAuthToken(String authToken) throws DataAccessException {
        AuthData authData = authTokens.get(authToken);
        if (authData == null) {
            return null;
        }
        return users.get(authData.username());
    }

    public int getNextGameID() {
        return nextGameID++;
    }
}
