package cliemailsystem.service;

import cliemailsystem.dao.EmailDAO;
import cliemailsystem.dao.UserDAO;
import cliemailsystem.entities.Email;
import cliemailsystem.exceptions.UserNotFoundException;
import cliemailsystem.exceptions.UsernameNotFoundException;

import java.util.List;
import java.util.Scanner;

public class EmailService {
    private final EmailDAO emailDAO = new EmailDAO();

    public void showEmailMenu(Scanner scanner, int currentUserId) {
        boolean emailMenuRunning = true;

        while (emailMenuRunning) {
            System.out.println("\nEmail Menu:");
            System.out.println("1. View Inbox");
            System.out.println("2. Compose Email");
            System.out.println("3. Log Out");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1 -> viewInbox(currentUserId);
                case 2 -> composeEmail(scanner, currentUserId);
                case 3 -> emailMenuRunning = false;
                default -> System.out.println("Invalid option. Try again!");
            }
        }
    }

    private void viewInbox(int currentUserId) {
        List<Email> inbox = emailDAO.findAllForUser(currentUserId);
        if (inbox.isEmpty()) {
            System.out.println("\nYour inbox is empty.\n");
        } else {
            UserDAO userDAO = new UserDAO();
            System.out.println("Your Inbox:");
            for (int i=0; i<inbox.size(); i++) {
                Email email = inbox.get(i);
                String senderUsername = userDAO.findUsernameById(email.getFromUserId());
                System.out.println((i+1) + ". " + "From: " + senderUsername + "\n" + " | Subject: " + email.getSubject() +
                        "\n" + " | Content: " + email.getContent() + "\n");
            }
        }
    }

    public void composeEmail(Scanner scanner, int currentUserId) {
        System.out.print("Enter recipient username or ID: ");
        String recipient = scanner.nextLine();

        int recipientId = 0;
        UserDAO userDAO = new UserDAO();

        try {
            // Check if input is a number (ID) or username
            if (recipient.matches("\\d+")) {
                recipientId = Integer.parseInt(recipient);
                // Validate ID exists
                userDAO.findById(recipientId);
            } else {
                // Input is a username, find corresponding ID
                recipientId = userDAO.findUserIdByUsername(recipient);
            }
        } catch (UserNotFoundException | UsernameNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
            return;
        } catch (RuntimeException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        System.out.print("Enter email subject: ");
        String subject = scanner.nextLine();

        System.out.print("Enter email content: ");
        String content = scanner.nextLine();

        Email email = new Email(currentUserId, recipientId, subject, content);

        try {
            EmailDAO emailDAO = new EmailDAO();
            emailDAO.save(email);
            System.out.println("Email sent successfully to " + userDAO.findUsernameById(recipientId) + "!");
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}