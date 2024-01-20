package com.example.odyssey.model.reviews;

import com.example.odyssey.model.users.User;
import com.example.odyssey.model.accommodations.Accommodation;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AccommodationReview extends Review implements Serializable {
private Accommodation accommodation;

    public AccommodationReview(Long id, Double rating, String comment, LocalDateTime submissionDate, User submitter, Review.Status status, Accommodation accommodation) {
        super(id, rating, comment, submissionDate, submitter, status);
        this.accommodation = accommodation;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

}
