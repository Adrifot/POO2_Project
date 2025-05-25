package cliemailsystem.service;

import cliemailsystem.dao.EmailDAO;
import cliemailsystem.entities.Email;
import cliemailsystem.utils.ConsoleUtils;

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

    private void composeEmail(Scanner scanner, int currentUserId) {
        int recipientId = ConsoleUtils.readRecipientId(scanner);
        String subject = ConsoleUtils.readEmailSubject(scanner);
        String content = ConsoleUtils.readEmailContent(scanner);

        Email email = new Email(currentUserId, recipientId, subject, content);
        emailDAO.save(email);

        System.out.println("Email sent successfully!");
    }
}