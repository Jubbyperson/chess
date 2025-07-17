package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class DataAccessMemory implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
}
