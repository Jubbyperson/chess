package client;

import java.util.List;
import java.util.Scanner;

public class Client {
    private final ServerFacade facade;
    private final Scanner scanner;
    private String authToken;
    private List<ServerFacade.GameEntry> gameList;
    private WebSocketClient webSocketClient;

    public Client(int port) {
        this.facade = new ServerFacade(port);
        this.scanner = new Scanner(System.in);
        this.authToken = null;
        this.gameList = null;
    }

    public void run() {
        System.out.println("240 Chess Client");
        System.out.println("Type 'help' for commands");

        while (true) {
            if (authToken == null) {
                preloginUI();
            } else {
                postloginUI();
            }
        }
    }

    private void preloginUI() {
        System.out.print("\n[LOGGED_OUT] >>> ");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "help" -> showPreloginHelp();
            case "quit" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            case "login" -> login();
            case "register" -> register();
            default -> System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void postloginUI() {
        System.out.print("\n[LOGGED_IN] >>> ");
        String input = scanner.nextLine().trim().toLowerCase();

        switch (input) {
            case "help" -> showPostloginHelp();
            case "logout" -> logout();
            case "create game" -> createGame();
            case "list games" -> listGames();
            case "play game" -> playGame();
            case "observe game" -> observeGame();
            default -> System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }

    private void showPreloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help     - Show this help message");
        System.out.println("  quit     - Exit the program");
        System.out.println("  login    - Login to your account");
        System.out.println("  register - Create a new account");
    }

    private void showPostloginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help         - Show this help message");
        System.out.println("  logout       - Logout of your account");
        System.out.println("  create game  - Create a new game");
        System.out.println("  list games   - List all games");
        System.out.println("  play game    - Join a game as a player");
        System.out.println("  observe game - Join a game as an observer");
    }

    private void register() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            var authData = facade.register(username, password, email);
            authToken = authData.authToken();
            System.out.println("Registration successful! You are now logged in.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            var authData = facade.login(username, password);
            authToken = authData.authToken();
            System.out.println("Login successful!");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void logout() {
        try {
            facade.logout(authToken);
            authToken = null;
            gameList = null;
            System.out.println("Logout successful!");
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void createGame() {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine().trim();

            int gameID = facade.createGame(authToken, gameName);
            System.out.println("Game created successfully!");
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            gameList = facade.listGames(authToken);
            if (gameList.isEmpty()) {
                System.out.println("No games available.");
            } else {
                System.out.println("Available games:");
                for (int i = 0; i < gameList.size(); i++) {
                    var game = gameList.get(i);
                    String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "none";
                    String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "none";
                    System.out.println((i + 1) + ". " + game.gameName() + " (White: " + whitePlayer + ", Black: " + blackPlayer + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void playGame() {
        if (gameList == null || gameList.isEmpty()) {
            System.out.println("No games available. Use 'list games' first.");
            return;
        }

        try {
            System.out.print("Enter game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (gameNumber < 0 || gameNumber >= gameList.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            System.out.print("Enter color (WHITE/BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color. Must be WHITE or BLACK.");
                return;
            }

            var game = gameList.get(gameNumber);
            facade.joinGame(authToken, game.gameID(), color);
            System.out.println("Joined game as " + color + " player!");

            var chessGame = new chess.ChessGame();
            chessGame.getBoard().resetBoard();
            ChessBoardDrawer.drawBoard(chessGame,
                    color.equals("WHITE") ? chess.ChessGame.TeamColor.WHITE : chess.ChessGame.TeamColor.BLACK);

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame() {
        if (gameList == null || gameList.isEmpty()) {
            System.out.println("No games available. Use 'list games' first.");
            return;
        }

        try {
            System.out.print("Enter game number: ");
            int gameNumber = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (gameNumber < 0 || gameNumber >= gameList.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            var game = gameList.get(gameNumber);
            facade.joinGame(authToken, game.gameID(), "OBSERVER");
            System.out.println("Joined game as observer!");

            var chessGame = new chess.ChessGame();
            chessGame.getBoard().resetBoard();
            ChessBoardDrawer.drawBoard(chessGame, chess.ChessGame.TeamColor.WHITE);

        } catch (NumberFormatException e) {
            System.out.println("Invalid game number. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Failed to observe game: " + e.getMessage());
        }
    }
}