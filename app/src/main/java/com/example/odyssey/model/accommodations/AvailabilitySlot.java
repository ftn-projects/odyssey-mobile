package com.example.odyssey.model.accommodations;

import com.example.odyssey.model.TimeSlot;

public class AvailabilitySlot {
    private Double price;
    private TimeSlot timeSlot;

    public AvailabilitySlot(Double price, TimeSlot timeSlot) {
        this.price = price;
        this.timeSlot = timeSlot;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
}
