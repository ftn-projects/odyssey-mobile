package com.example.odyssey.model.notifications;

import com.example.odyssey.model.reservations.AccreditReservation;
import com.example.odyssey.model.reviews.AccommodationReview;
import com.example.odyssey.model.reviews.HostReview;
import com.example.odyssey.model.users.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private Long id;
    private String title;
    private String text;
    private LocalDateTime date;
    private Boolean read;
    private Type type;
    private User receiver;
    private AccreditReservation reservation;
    private AccommodationReview accommodationReview;
    private HostReview hostReview;

    public enum Type {GENERIC, ACCOMMODATION_REVIEW, HOST_REVIEW, RESERVATION_REQUESTED, RESERVATION_ACCEPTED, RESERVATION_DECLINED, RESERVATION_CANCELLED}


    public Notification(Long id, String title, String text, LocalDateTime date, Boolean read, Type type, User receiver, AccreditReservation reservation, AccommodationReview accommodationReview, HostReview hostReview) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.read = read;
        this.type = type;
        this.receiver = receiver;
        this.reservation = reservation;
        this.accommodationReview = accommodationReview;
        this.hostReview = hostReview;
    }

    public String getShortTitle() {
        return text.length() > 20 ? text.substring(0, 20) + "..." : text;
    }

    public String getShortText() {
        return text.length() > 30 ? text.substring(0, 30) + "..." : text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public AccreditReservation getReservation() {
        return reservation;
    }

    public void setReservation(AccreditReservation reservation) {
        this.reservation = reservation;
    }

    public AccommodationReview getAccommodationReview() {
        return accommodationReview;
    }

    public void setAccommodationReview(AccommodationReview accommodationReview) {
        this.accommodationReview = accommodationReview;
    }

    public HostReview getHostReview() {
        return hostReview;
    }

    public void setHostReview(HostReview hostReview) {
        this.hostReview = hostReview;
    }
}
