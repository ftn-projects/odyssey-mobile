package com.example.odyssey.model.reviews;

import com.example.odyssey.model.users.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Review implements Serializable {
    private Long id;
    private Double rating;
    private String comment;
    private LocalDateTime submissionDate;
    private User submitter;
    private Status status;
    public enum Status {REQUESTED, DECLINED, ACCEPTED, REPORTED}

    public Review(Long id, Double rating, String comment, LocalDateTime submissionDate, User submitter, Status status) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.submissionDate = submissionDate;
        this.submitter = submitter;
        this.status = status;
    }

    public abstract String getTitle();

    public Long getId() {
        return id;
    }

    public Double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public User getSubmitter() {
        return submitter;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}


