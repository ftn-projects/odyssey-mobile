package com.example.odyssey.model.accommodations;

import android.net.Uri;

import com.example.odyssey.model.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccommodationRequestSubmission implements Serializable {
    private Long id;
    private Type requestType;
    private String newTitle;
    private String newDescription;
    private Accommodation.Type newType;
    private Address newAddress = new Address();
    private Accommodation.PricingType newPricing;
    private Set<Amenity> newAmenities = new HashSet<>();
    private Double newDefaultPrice;
    private Boolean newAutomaticApproval;
    private Long newCancellationDue;
    private Set<AvailabilitySlot> newAvailableSlots = new HashSet<>();
    private Integer newMinGuests;
    private Integer newMaxGuests;
    private Set<String> newImages = new HashSet<>();
    private List<Uri> localImageUris = new ArrayList<>();
    private List<String> remoteImageNames = new ArrayList<>();
    private Long hostId;
    private Long accommodationId;



    public enum Type {CREATE, UPDATE}
    public AccommodationRequestSubmission() {
    }

    public AccommodationRequestSubmission(Long id, Type requestType, String newTitle, String newDescription,
                                          Accommodation.Type newType, Address newAddress, Accommodation.PricingType newPricing,
                                          Set<Amenity> newAmenities, Double newDefaultPrice, Boolean newAutomaticApproval, Long newCancellationDue,
                                          Set<AvailabilitySlot> newAvailableSlots, Integer newMinGuests, Integer newMaxGuests, Set<String> newImages, Long hostId, Long accommodationId) {
        this.id = id;
        this.requestType = requestType;
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newType = newType;
        this.newAddress = newAddress;
        this.newPricing = newPricing;
        this.newAmenities = newAmenities;
        this.newDefaultPrice = newDefaultPrice;
        this.newAutomaticApproval = newAutomaticApproval;
        this.newCancellationDue = newCancellationDue;
        this.newAvailableSlots = newAvailableSlots;
        this.newMinGuests = newMinGuests;
        this.newMaxGuests = newMaxGuests;
        this.newImages = newImages;
        this.hostId = hostId;
        this.accommodationId = accommodationId;
    }

    public void loadData(Accommodation accommodation) {
        newTitle = accommodation.getTitle();
        newDescription = accommodation.getDescription();
        newType = accommodation.getType();
        newAddress = accommodation.getAddress();
        newPricing = accommodation.getPricing();
        newAmenities = new HashSet<>(accommodation.getAmenities());
        newDefaultPrice = accommodation.getDefaultPrice();
        newAutomaticApproval = accommodation.getAutomaticApproval();
        newCancellationDue = accommodation.getCancellationDue();
        newAvailableSlots = new HashSet<>(accommodation.getAvailableSlots());
        newMinGuests = accommodation.getMinGuests();
        newMaxGuests = accommodation.getMaxGuests();
        hostId = accommodation.getHost().getId();
        accommodationId = accommodation.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getRequestType() {
        return requestType;
    }

    public void setRequestType(Type requestType) {
        this.requestType = requestType;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public Accommodation.Type getNewType() {
        return newType;
    }

    public void setNewType(Accommodation.Type newType) {
        this.newType = newType;
    }

    public Address getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(Address newAddress) {
        this.newAddress = newAddress;
    }

    public Accommodation.PricingType getNewPricing() {
        return newPricing;
    }

    public void setNewPricing(Accommodation.PricingType newPricing) {
        this.newPricing = newPricing;
    }

    public Set<Amenity> getNewAmenities() {
        return newAmenities;
    }

    public void setNewAmenities(Set<Amenity> newAmenities) {
        this.newAmenities = newAmenities;
    }

    public Double getNewDefaultPrice() {
        return newDefaultPrice;
    }

    public void setNewDefaultPrice(Double newDefaultPrice) {
        this.newDefaultPrice = newDefaultPrice;
    }

    public Boolean getNewAutomaticApproval() {
        return newAutomaticApproval;
    }

    public void setNewAutomaticApproval(Boolean newAutomaticApproval) {
        this.newAutomaticApproval = newAutomaticApproval;
    }

    public Long getNewCancellationDue() {
        return newCancellationDue;
    }

    public void setNewCancellationDue(Long newCancellationDue) {
        this.newCancellationDue = newCancellationDue;
    }

    public Set<AvailabilitySlot> getNewAvailableSlots() {
        return newAvailableSlots;
    }

    public void setNewAvailableSlots(Set<AvailabilitySlot> newAvailableSlots) {
        this.newAvailableSlots = newAvailableSlots;
    }

    public Integer getNewMinGuests() {
        return newMinGuests;
    }

    public void setNewMinGuests(Integer newMinGuests) {
        this.newMinGuests = newMinGuests;
    }

    public Integer getNewMaxGuests() {
        return newMaxGuests;
    }

    public void setNewMaxGuests(Integer newMaxGuests) {
        this.newMaxGuests = newMaxGuests;
    }

    public Set<String> getNewImages() {
        return newImages;
    }

    public void setNewImages(Set<String> newImages) {
        this.newImages = newImages;
    }

    public List<Uri> getLocalImageUris() {
        return localImageUris;
    }

    public void setLocalImageUris(List<Uri> localImageUris) {
        this.localImageUris = localImageUris;
    }

    public List<String> getRemoteImageNames() {
        return remoteImageNames;
    }

    public void setRemoteImageNames(List<String> remoteImageNames) {
        this.remoteImageNames = remoteImageNames;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }
}
