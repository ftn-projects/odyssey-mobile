package com.example.odyssey.model.reservations;

import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.users.User;

import java.time.LocalDate;

public class AccreditReservation {
    private Long id;
    private Double price;
    private Integer guestNumber;
    private Integer cancellationNumber;
    private Status status;
    private LocalDate requestDate;
    private LocalDate start;
    private LocalDate end;
    private Accommodation accommodation;
    private User guest;

    public enum Status {REQUESTED, DECLINED, CANCELLED_REQUEST, CANCELLED_RESERVATION, ACCEPTED}

    public AccreditReservation(Long id, Double price, Integer guestNumber, Integer cancellationNumber,
                               Status status, LocalDate requestDate, LocalDate start,
                               LocalDate end, Accommodation accommodation, User guest) {
        this.id = id;
        this.price = price;
        this.guestNumber = guestNumber;
        this.cancellationNumber = cancellationNumber;
        this.status = status;
        this.requestDate = requestDate;
        this.start = start;
        this.end = end;
        this.accommodation = accommodation;
        this.guest = guest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(Integer guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Integer getCancellationNumber() {
        return cancellationNumber;
    }

    public void setCancellationNumber(Integer cancellationNumber) {
        this.cancellationNumber = cancellationNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }
}
