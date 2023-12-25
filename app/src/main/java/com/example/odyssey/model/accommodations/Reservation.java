package com.example.odyssey.model.accommodations;

import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.User;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private Double price;
    private Integer guestNumber;
    private Status status;
    private LocalDateTime requestDate;
    private LocalDateTime reservationDate;
    private TimeSlot timeSlot;
    private Accommodation accommodation;
    private User guest;

    public enum Status {REQUESTED, DECLINED, CANCELLED_REQUEST, CANCELLED_RESERVATION, ACCEPTED}
}
