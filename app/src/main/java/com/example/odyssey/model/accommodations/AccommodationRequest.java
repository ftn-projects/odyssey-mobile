package com.example.odyssey.model.accommodations;

import android.net.Uri;

import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccommodationRequest implements Serializable {
    private Long id;
    private LocalDate submissionDate;
    private AccommodationRequestSubmission.Type type;
    private User host;
    private Accommodation details;
    private Long accommodationId;

    public AccommodationRequest() {
    }

    public AccommodationRequest(Long id, LocalDate submissionDate, AccommodationRequestSubmission.Type type, User host, Accommodation details, Long accommodationId) {
        this.id = id;
        this.submissionDate = submissionDate;
        this.type = type;
        this.host = host;
        this.details = details;
        this.accommodationId = accommodationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public AccommodationRequestSubmission.Type getType() {
        return type;
    }

    public void setType(AccommodationRequestSubmission.Type type) {
        this.type = type;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Accommodation getDetails() {
        return details;
    }

    public void setDetails(Accommodation details) {
        this.details = details;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }
}
