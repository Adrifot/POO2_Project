package cliemailsystem.service;

import cliemailsystem.audit.AuditLogger;
import cliemailsystem.dao.EmailDAO;
import cliemailsystem.dao.UserDAO;
import cliemailsystem.entities.Email;
import cliemailsystem.exceptions.UserNotFoundException;
import cliemailsystem.exceptions.UsernameNotFoundException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EmailService {
    private final EmailDAO emailDAO = new EmailDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

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
                case 1 -> viewInbox(scanner, currentUserId);
                case 2 -> composeEmail(scanner, currentUserId);
                case 3 -> emailMenuRunning = false;
                default -> System.out.println("Invalid option. Try again!");
            }
        }
    }

    private void viewInbox(Scanner scanner, int currentUserId) {
        List<Email> inbox = emailDAO.findAllForUser(currentUserId);
        if (inbox.isEmpty()) {
            System.out.println("\nYour inbox is empty.\n");
            return;
        }

        UserDAO userDAO = new UserDAO();
        System.out.println("\nYour Inbox:");
        for (int i=0; i<inbox.size(); i++) {
            Email email = inbox.get(i);
            String senderUsername = userDAO.findUsernameById(email.getFromUserId());
            System.out.println((i+1) + ". " + email.getSubject() + " - from " + senderUsername +
                    (email.getStatus() == Email.EmailStatus.READ ? "" : " (Unread)"));
        }

        // Email action menu
        System.out.println("\nOptions:");
        System.out.println("1. Read an email");
        System.out.println("2. Return to main menu");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print("Enter the number of the email you want to read: ");
            int emailIndex = scanner.nextInt();
            scanner.nextLine();

            if (emailIndex < 1 || emailIndex > inbox.size()) {
                System.out.println("Invalid email number.");
                return;
            }

            Email selectedEmail = inbox.get(emailIndex - 1);
            readEmail(scanner, selectedEmail, currentUserId);
        }
    }

    private void readEmail(Scanner scanner, Email email, int currentUserId) {

        if (email.getStatus() != Email.EmailStatus.READ) {
            email.setStatus(Email.EmailStatus.READ);
            emailDAO.update(email);
        }

        UserDAO userDAO = new UserDAO();
        String senderUsername = userDAO.findUsernameById(email.getFromUserId());
        String formattedDate = email.getTimestamp().format(dateFormatter);

        System.out.println("\n========== EMAIL ==========");
        System.out.println("From: " + senderUsername);
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + formattedDate);
        System.out.println("---------------------------");
        System.out.println(email.getContent());
        System.out.println("===========================\n");

        System.out.println("Options:");
        System.out.println("1. Reply to this email");
        System.out.println("2. Delete this email");
        System.out.println("3. Return to inbox");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> replyToEmail(scanner, email, currentUserId);
            case 2 -> deleteEmail(email, currentUserId);
            case 3 -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void deleteEmail(Email email, int currentUserId) {
        if (email.getToUserId() == currentUserId) try {
            emailDAO.deleteById(email.getId());
            System.out.println("Email deleted successfully!");
            AuditLogger.getInstance().log("Email#" + email.getId() + " deleted by User with id " + currentUserId);
        } catch (Exception e) {
            System.out.println("Failed to delete email: " + e.getMessage());
        } else {
            System.out.println("You do not have permission to delete this email.");
        }
    }

    private void replyToEmail(Scanner scanner, Email originalEmail, int currentUserId) {
        int recipientId = originalEmail.getFromUserId();
        String subject = "RE: " + originalEmail.getSubject();

        UserDAO userDAO = new UserDAO();
        String recipientName = userDAO.findUsernameById(recipientId);

        System.out.println("\nReplying to " + recipientName);
        System.out.println("Subject: " + subject);

        System.out.print("Enter your reply: ");
        String content = scanner.nextLine();

        Email reply = new Email(currentUserId, recipientId, subject, content);

        try {
            emailDAO.save(reply);
            System.out.println("Reply sent successfully!");
            AuditLogger.getInstance().log("Email#" + originalEmail.getId() + " replied by User with id" + currentUserId);
        } catch (Exception e) {
            System.out.println("Failed to send reply: " + e.getMessage());
        }
    }


    public void composeEmail(Scanner scanner, int currentUserId) {
        System.out.print("Enter recipient username or ID: ");
        String recipient = scanner.nextLine();

        int recipientId = 0;
        UserDAO userDAO = new UserDAO();

        try {
            if (recipient.matches("\\d+")) {
                recipientId = Integer.parseInt(recipient);
                userDAO.findById(recipientId);
            } else {
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
            AuditLogger.getInstance().log("Email#" + email.getId() + " sent by User with id " + currentUserId + " to User with id " + recipientId);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}