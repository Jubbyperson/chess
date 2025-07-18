package service.results;

import java.util.List;
public record ListGamesResult(List<GameEntry> games) {
    public record GameEntry(int gameID, String whiteUsername, String blackUsername, String gameName) {}
}
