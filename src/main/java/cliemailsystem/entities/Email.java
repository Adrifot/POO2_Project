package cliemailsystem.entities;

import java.time.LocalDateTime;

public class Email {
    private int id; // Primary key in DB
    private final int fromUserId;
    private final int toUserId;
    private final LocalDateTime created_at;
    private final String subject;
    private final String content;
    private EmailStatus status;

    public Email(int fromUserId, int toUserId, String subject, String content) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.subject = subject;
        this.content = content;
        this.created_at = LocalDateTime.now();
        this.status = EmailStatus.NEW;
    }

    public Email(int id, int fromUserId, int toUserId, String subject, String content, LocalDateTime createdAt) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.subject = subject;
        this.content = content;
        this.created_at = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public LocalDateTime getTimestamp() {
        return created_at;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void markAsRead() {
        this.status = EmailStatus.READ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public enum EmailStatus {
        NEW,
        READ,
        FORWARDED;
    }
}