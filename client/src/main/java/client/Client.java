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



}