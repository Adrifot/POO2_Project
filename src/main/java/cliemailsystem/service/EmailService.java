package cliemailsystem.service;

import cliemailsystem.dao.EmailDAO;
import cliemailsystem.dao.UserDAO;
import cliemailsystem.entities.Email;

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
            System.out.println("Your inbox is empty.");
        } else {
            System.out.println("Your Inbox:");
            for (Email email : inbox) {
                System.out.println("---------------------------------");
                System.out.println("From: User " + email.getFromUserId());
                System.out.println("To: User " + email.getToUserId());
                System.out.println("Subject: " + email.getSubject());
                System.out.println("Content:");
                System.out.println(email.getContent());
                System.out.println("---------------------------------");
            }
        }
    }

    // Find the method that handles email composition and update it to:

    public void composeEmail(Scanner scanner, int currentUserId) {
        System.out.print("Enter recipient ID: ");
        String recipientIdStr = scanner.nextLine();
        int recipientId;

        try {
            recipientId = Integer.parseInt(recipientIdStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            return;
        }

        // Validate that the recipient exists
        UserDAO userDAO = new UserDAO();
        try {
            userDAO.findById(recipientId); // This will throw an exception if user doesn't exist
        } catch (RuntimeException e) {
            System.out.println("Error: Recipient with ID " + recipientId + " does not exist.");
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
            System.out.println("Email sent successfully!");
        } catch (RuntimeException e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}