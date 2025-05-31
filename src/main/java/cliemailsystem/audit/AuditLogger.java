package cliemailsystem.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogger {

    private static final String FILE_NAME = "audit_log.csv";
    private static AuditLogger instance;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditLogger() {

        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Action,Timestamp\n");
                }
                System.out.println("Audit log file created at: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize audit log file: " + e.getMessage());
        }
    }

    public static synchronized AuditLogger getInstance() {
        if (instance == null) {
            instance = new AuditLogger();
        }
        return instance;
    }

    public void log(String action) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write(action + "," + timestamp + "\n");
            // Flush -> ensure data is written immediately
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }
}