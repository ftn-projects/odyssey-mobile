package com.example.odyssey.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot implements Serializable {
    private LocalDateTime start;
    private LocalDateTime end;

    public Duration getDuration() {
        return Duration.between(start, end);
    }

    public boolean overlaps(TimeSlot slot){
        return (this.getStart().isBefore(slot.getEnd()) && this.getEnd().isAfter(slot.getStart()));
    }

    public boolean equals(TimeSlot slot){
        return (this.getStart().equals(slot.getStart()) && this.getEnd().equals(slot.getEnd()));
    }

    public boolean containsDay(LocalDate day){
        return !(this.getStart().toLocalDate().isAfter(day) && this.getEnd().toLocalDate().isBefore(day));
    }

    public boolean coolerContainsDay(LocalDate day){
        return this.getStart().toLocalDate().isBefore(day) && this.getEnd().toLocalDate().isAfter(day);
    }

    public List<LocalDate> getDays(){
        return new ArrayList<>();
//        return start.toLocalDate().datesUntil(end.toLocalDate()).collect(Collectors.toList());
    }

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
