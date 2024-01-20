package com.example.odyssey.model.stats;

import com.example.odyssey.model.User;

import java.io.Serializable;
import java.util.List;

public class TotalStats implements Serializable {
    Long start;
    Long end;
    User host;
    Integer totalAccommodations;
    Integer totalReservations;
    Double totalIncome;
    List<MonthlyStats> monthlyStats;

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public User getHost() {
        return host;
    }

    public Integer getTotalAccommodations() {
        return totalAccommodations;
    }

    public Integer getTotalReservations() {
        return totalReservations;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public List<MonthlyStats> getMonthlyStats() {
        return monthlyStats;
    }
}
