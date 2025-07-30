package client;

import java.util.List;
import java.util.Scanner;

public class Client {
    private final ServerFacade facade;
    private final Scanner scanner;
    private String authToken;
    private List<ServerFacade.GameEntry> gameList;

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


}