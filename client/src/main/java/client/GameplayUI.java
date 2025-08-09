package client;

import chess.*;
import websocket.commands.UserGameCommand;
import java.util.Collection;
import java.util.Scanner;

public class GameplayUI {
    private final Scanner scanner;
    private final WebSocketClient webSocketClient;
    private final String authToken;
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    private final boolean isObserver;
    private ChessGame currentGame;

    public GameplayUI(Scanner scanner, String authToken, int gameID,
                      ChessGame.TeamColor playerColor, boolean isObserver) {
        this.scanner = scanner;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.isObserver = isObserver;
        this.webSocketClient = new WebSocketClient(this);
        this.currentGame = new ChessGame();
    }

    public void start(String serverUrl) throws Exception {
        // Connect to WebSocket
        webSocketClient.connect(serverUrl);

        // Send CONNECT command
        webSocketClient.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.CONNECT, authToken, gameID));

        // Start gameplay loop
        gameplayLoop();
    }

    private void gameplayLoop() {
        while (true) {
            System.out.print("\n[GAMEPLAY] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            try {
                switch (input) {
                    case "help" -> showHelp();
                    case "redraw", "redraw board" -> redrawBoard();
                    case "leave" -> {
                        leave();
                        return;
                    }
                    case "move", "make move" -> makeMove();
                    case "resign" -> resign();
                    case "highlight", "highlight moves" -> highlightMoves();
                    default -> System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("  help - Show this help message");
        System.out.println("  redraw - Redraw the chess board");
        System.out.println("  leave - Leave the game");
        if (!isObserver) {
            System.out.println("  move - Make a move");
            System.out.println("  resign - Resign from the game");
        }
        System.out.println("  highlight - Highlight legal moves for a piece");
    }

    private void redrawBoard() {
        ChessBoardDrawer.drawBoard(currentGame,
                isObserver ? ChessGame.TeamColor.WHITE : playerColor);
    }

    private void leave() throws Exception {
        webSocketClient.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.LEAVE, authToken, gameID));
        webSocketClient.disconnect();
    }

    private void makeMove() throws Exception {
        if (isObserver) {
            System.out.println("Observers cannot make moves.");
            return;
        }

        System.out.print("Enter start position (e.g., e2): ");
        String startPos = scanner.nextLine().trim();
        System.out.print("Enter end position (e.g., e4): ");
        String endPos = scanner.nextLine().trim();


}