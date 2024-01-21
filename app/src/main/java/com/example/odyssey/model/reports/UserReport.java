package com.example.odyssey.model.reports;

import com.example.odyssey.model.users.User;

import java.time.LocalDateTime;

public class UserReport {
    private Long id;
    private String description;
    private LocalDateTime submissionDate;
    private User submitter;
    private User reported;

    public UserReport() {
    }

    public UserReport(Long id, String description, LocalDateTime submissionDate, User submitter, User reported) {
        this.id = id;
        this.description = description;
        this.submissionDate = submissionDate;
        this.submitter = submitter;
        this.reported = reported;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public User getReported() {
        return reported;
    }

    public void setReported(User reported) {
        this.reported = reported;
    }
}
