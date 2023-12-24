package com.example.odyssey.model;

public class Address {
    private String street;
    private Integer number;
    private String city;
    private String country;

    public Address(String street, String city, String country) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.country = country;
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
