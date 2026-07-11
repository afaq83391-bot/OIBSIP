package com.example.todoapp.models;

/**
 * Task model representing a single to-do item.
 */
public class Task {
    private long id;
    private long userId;
    private String title;
    private String notes;
    private boolean isCompleted;
    private String createdAt;

    public Task(long id, long userId, String title, String notes,
                boolean isCompleted, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.notes = notes;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}