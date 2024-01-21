package com.example.odyssey.model.users;

import com.example.odyssey.model.Address;

import java.io.Serializable;

public class User implements Serializable {
    protected Long id;
    protected String email;
    protected String name;
    protected String surname;
    protected String phone;
    protected String role;
    protected Address address;
    protected Settings settings;
    protected String bio;

    public static class Settings implements Serializable{
        private Boolean reservationRequested = true;
        private Boolean reservationAccepted = true;
        private Boolean reservationDeclined = true;
        private Boolean reservationCancelled = true;
        private Boolean profileReviewed = true;
        private Boolean accommodationReviewed = true;
    }

    public User() {
    }

    public User(Long id, String email, String name, String surname, String phone, String role, Address address, Settings settings, String bio) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.role = role;
        this.address = address;
        this.settings = settings;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
