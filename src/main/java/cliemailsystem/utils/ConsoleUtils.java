package cliemailsystem.utils;

import java.util.Scanner;

public class ConsoleUtils {

    public static String readUsername(Scanner scanner) {
        System.out.print("Enter username: ");
        return scanner.nextLine();
    }

    public static String readPassword(Scanner scanner) {
        System.out.print("Enter password: ");
        return scanner.nextLine();
    }

    public static String readEmailSubject(Scanner scanner) {
        System.out.print("Enter email subject: ");
        return scanner.nextLine();
    }

    public static String readEmailContent(Scanner scanner) {
        System.out.print("Enter email content: ");
        return scanner.nextLine();
    }

    public static int readRecipientId(Scanner scanner) {
        System.out.print("Enter recipient ID: ");
        return scanner.nextInt();
    }
}