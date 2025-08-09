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
        webSocketClient.connect(serverUrl);
        webSocketClient.sendCommand(new UserGameCommand(
                UserGameCommand.CommandType.CONNECT, authToken, gameID));
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

        try {
            ChessPosition start = parsePosition(startPos);
            ChessPosition end = parsePosition(endPos);
            ChessMove move = new ChessMove(start, end, null);

            ChessPiece piece = currentGame.getBoard().getPiece(start);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && end.getRow() == 8) ||
                        (piece.getTeamColor() == ChessGame.TeamColor.BLACK && end.getRow() == 1)) {
                    System.out.print("Promote to (Q/R/B/N): ");
                    String promotion = scanner.nextLine().trim().toUpperCase();
                    ChessPiece.PieceType promotionPiece = switch (promotion) {
                        case "Q" -> ChessPiece.PieceType.QUEEN;
                        case "R" -> ChessPiece.PieceType.ROOK;
                        case "B" -> ChessPiece.PieceType.BISHOP;
                        case "N" -> ChessPiece.PieceType.KNIGHT;
                        default -> ChessPiece.PieceType.QUEEN;
                    };
                    move = new ChessMove(start, end, promotionPiece);
                }
            }

            webSocketClient.sendMoveCommand(authToken, gameID, move);
        } catch (Exception e) {
            System.out.println("Invalid move format. Use format like 'e2'. Error: " + e.getMessage());
        }
    }

    private void resign() throws Exception {
        if (isObserver) {
            System.out.println("Observers cannot resign.");
            return;
        }

        System.out.print("Are you sure you want to resign? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            webSocketClient.sendCommand(new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN, authToken, gameID));
        }
    }

    private void highlightMoves() {
        System.out.print("Enter piece position (e.g., e2): ");
        String posStr = scanner.nextLine().trim();
        try {
            ChessPosition position = parsePosition(posStr);
            Collection<ChessMove> validMoves = currentGame.validMoves(position);
            ChessBoardDrawer.drawBoardWithHighlights(currentGame,
                    isObserver ? ChessGame.TeamColor.WHITE : playerColor,
                    position, validMoves);
        } catch (Exception e) {
            System.out.println("Invalid position format. Use format like 'e2'.");
        }
    }

    private ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position format: " + pos + ". Expected format like 'e2'");
        }
        
        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);
        
        if (colChar < 'a' || colChar > 'h') {
            throw new IllegalArgumentException("Invalid column: " + colChar + ". Must be a-h");
        }
        
        if (rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Invalid row: " + rowChar + ". Must be 1-8");
        }
        
        int col = colChar - 'a' + 1;
        int row = rowChar - '0';
        
        ChessPosition result = new ChessPosition(row, col);
        System.out.println("Parsed " + pos + " to row=" + row + ", col=" + col); // Debug
        return result;
    }

    public void updateGame(ChessGame game) {
        this.currentGame = game;
        redrawBoard();
    }

    public void displayError(String error) {
        System.out.println("Error: " + error);
    }

    public void displayNotification(String notification) {
        System.out.println(">>> " + notification);
    }
}