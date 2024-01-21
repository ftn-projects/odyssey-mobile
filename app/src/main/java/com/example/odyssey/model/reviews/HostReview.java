package com.example.odyssey.model.reviews;

import com.example.odyssey.model.users.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HostReview extends Review implements Serializable {
    private User host;

    public HostReview(Long id, Double rating, String comment, LocalDateTime submissionDate,User submitter, Review.Status status, User host) {
        super(id, rating, comment, submissionDate, submitter, status);
        this.host = host;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    @Override
    public String getTitle() {
        return host.getName() + " " + host.getSurname();
    }
}
