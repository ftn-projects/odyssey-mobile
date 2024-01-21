package com.example.odyssey.model.reservations;

import com.example.odyssey.model.TimeSlot;

import java.time.LocalDateTime;

public class ReservationRequest {
    private Long id;
    private Double price;
    private Integer guestNumber;

    private Status status;
    private LocalDateTime requestDate;
    private TimeSlot timeSlot;
    private Long accommodationId;
    private Long guestId;

    public ReservationRequest() {
    }

    public ReservationRequest(Long id, Double price, Integer guestNumber, Status status, LocalDateTime requestDate, TimeSlot timeSlot, Long accommodationId, Long guestId) {
        this.id = id;
        this.price = price;
        this.guestNumber = guestNumber;
        this.status = status;
        this.requestDate = requestDate;
        this.timeSlot = timeSlot;
        this.accommodationId = accommodationId;
        this.guestId = guestId;
    }
    public enum Status {REQUESTED, ACCEPTED, DECLINED, CANCELLED_REQUEST, CANCELLED_RESERVATION}
}
