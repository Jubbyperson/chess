package dataaccess;

import chess.ChessGame;
import com.google.gson.*;
import java.sql.*;
import model.*;


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

    private void createUserTable() throws DataAccessException {
        var createUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255)
            )
            """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createUserTable)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create user table", ex);
        }
    }

    private void createGameTable() throws DataAccessException {
        var createGameTable = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL
            )
            """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createGameTable)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game table", ex);
        }
    }

    private void createAuthTable() throws DataAccessException {
        var createAuthTable = """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            )
            """;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createAuthTable)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create auth table", ex);
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "DELETE FROM users";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear users", ex);
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "DELETE FROM games";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear games", ex);
        }
    }

    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "DELETE FROM auth";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear auth", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Check if user already exists
        if (getUser(user.username()) != null) {
            throw new DataAccessException("User already exists");
        }

        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get user", ex);
        }
        return null;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUser());
            preparedStatement.setString(3, game.blackUser());
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, gson.toJson(game.game()));
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create game", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ChessGame chessGame = gson.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            chessGame
                    );
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game", ex);
        }
        return null;
    }
}
