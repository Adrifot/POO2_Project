package cliemailsystem.audit;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuditLogger {
    private static final String FILE_NAME = AuditLogger.class.getClassLoader().getResource("audit_log.csv").getPath();
    private static AuditLogger instance;

    private AuditLogger() {}

    public static synchronized AuditLogger getInstance() {
        if (instance == null) {
            instance = new AuditLogger();
        }
        return instance;
    }

    public void log(String action) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(action + "," + LocalDateTime.now() + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to audit log. " + e.getMessage(), e);
        }
    }
}