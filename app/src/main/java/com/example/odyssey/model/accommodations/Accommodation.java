package com.example.odyssey.model.accommodations;

import com.example.odyssey.model.Address;
import com.example.odyssey.model.users.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Accommodation implements Serializable {
    private Long id;
    private String title;
    private String description;
    private Type type;
    private Address address;
    private PricingType pricing;
    private Set<Amenity> amenities = new HashSet<>();
    private User host;
    private Double defaultPrice;
    private Boolean automaticApproval;
    private Long cancellationDue;
    private Set<AvailabilitySlot> availableSlots = new HashSet<>();
    private Integer minGuests;
    private Integer maxGuests;
    private Double totalPrice;
    private Double averageRating;

    public enum Type {ROOM, APARTMENT, HOUSE}
    public enum PricingType {PER_NIGHT, PER_PERSON}

    public static PricingType getPricingType(String string){
        if(string.equals("PER PERSON")) return PricingType.PER_PERSON;
        return PricingType.PER_NIGHT;
    }

    public Accommodation() {
    }

    public Accommodation(Long id, String title, String description, Type type, Address address, PricingType pricing, Set<Amenity> amenities, User host, Double defaultPrice, Boolean automaticApproval, Long cancellationDue, Set<AvailabilitySlot> availableSlots, Integer minGuests, Integer maxGuests, Double totalPrice, Double averageRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.address = address;
        this.pricing = pricing;
        this.amenities = amenities;
        this.host = host;
        this.defaultPrice = defaultPrice;
        this.automaticApproval = automaticApproval;
        this.cancellationDue = cancellationDue;
        this.availableSlots = availableSlots;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.totalPrice = totalPrice;
        this.averageRating = averageRating;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PricingType getPricing() {
        return pricing;
    }

    public void setPricing(PricingType pricing) {
        this.pricing = pricing;
    }

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<Amenity> amenities) {
        this.amenities = amenities;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Double getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(Double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public Boolean getAutomaticApproval() {
        return automaticApproval;
    }

    public void setAutomaticApproval(Boolean automaticApproval) {
        this.automaticApproval = automaticApproval;
    }

    public Long getCancellationDue() {
        return cancellationDue;
    }

    public void setCancellationDue(Long cancellationDue) {
        this.cancellationDue = cancellationDue;
    }

    public Set<AvailabilitySlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Set<AvailabilitySlot> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public Integer getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(Integer minGuests) {
        this.minGuests = minGuests;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
