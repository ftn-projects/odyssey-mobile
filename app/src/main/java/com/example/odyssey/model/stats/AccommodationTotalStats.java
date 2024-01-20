package com.example.odyssey.model.stats;

import com.example.odyssey.model.accommodations.Accommodation;

import java.util.List;

public class AccommodationTotalStats {
    Long start;
    Long end;
    Integer totalReservations;
    Double totalIncome;
    Accommodation accommodation;
    List<MonthlyStats> monthlyStats;

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public Integer getTotalReservations() {
        return totalReservations;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public List<MonthlyStats> getMonthlyStats() {
        return monthlyStats;
    }
}
