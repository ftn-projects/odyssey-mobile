package com.example.odyssey.model.stats;

import java.io.Serial;
import java.io.Serializable;

public class MonthlyStats implements Serializable {
    private Long month;
    private Integer reservationsCount;
    private Double totalIncome;

    public Long getMonth() {
        return month;
    }
    public Integer getReservationsCount() {
        return reservationsCount;
    }
    public Double getTotalIncome() {
        return totalIncome;
    }
}
