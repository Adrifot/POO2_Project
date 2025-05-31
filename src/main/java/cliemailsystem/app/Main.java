package cliemailsystem.app;

import cliemailsystem.service.UserService;
import cliemailsystem.service.EmailService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        UserService userService = new UserService();
        EmailService emailService = new EmailService();

        System.out.println("Welcome to the CLI Email System!");
        boolean running = true;

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Register");
            System.out.println("2. Log In");
            System.out.println("3. Exit");

            int option = getValidOption(scanner);

            switch (option) {
                case 1 -> userService.registerUser(scanner);
                case 2 -> {
                    if (userService.logIn(scanner)) {
                        emailService.showEmailMenu(scanner, userService.getCurrentUserId());
                    }
                }
                case 3 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }

                default -> System.out.println("Invalid option. Try again!");
            }
        }

        scanner.close();
    }

    private static int getValidOption(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Choose an option: ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}



