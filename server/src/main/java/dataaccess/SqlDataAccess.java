package dataaccess;

import com.google.gson.*;


public class SqlDataAccess implements DataAccess{
    private final Gson gson = new GsonBuilder().create();

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        createTables();
    }
    private void createTables() throws DataAccessException{
        createUserTable();
        createAuthTable();
        createGameTable();
    }
}
