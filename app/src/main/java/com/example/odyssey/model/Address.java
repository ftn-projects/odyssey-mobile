package com.example.odyssey.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Address implements Serializable {
    private String street = "";
    private String city = "";
    private String country = "";

    public Address() {
    }

    public Address(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }

    @NonNull
    @Override
    public String toString() {
        return (street.trim().isEmpty() ? "" : street + ", ") +
                (city.trim().isEmpty() ? "" : city + ", ") +
                country.trim();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
