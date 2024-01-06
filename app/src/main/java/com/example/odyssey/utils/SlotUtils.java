package com.example.odyssey.utils;

import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.accommodations.AvailabilitySlot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SlotUtils {
    public static List<AvailabilitySlot> splitSlots(AvailabilitySlot first, AvailabilitySlot second){
        List<AvailabilitySlot> spliced = new ArrayList<>();
        spliced.add(second);
        if(first.getTimeSlot().getStart().isBefore(second.getTimeSlot().getStart())){
            if(second.getTimeSlot().getEnd().isBefore(first.getTimeSlot().getEnd())){
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(first.getTimeSlot().getStart(), second.getTimeSlot().getStart().minusDays(1))));
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(second.getTimeSlot().getEnd().plusDays(1), first.getTimeSlot().getEnd())));
            }
            else spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(first.getTimeSlot().getStart(), second.getTimeSlot().getStart().minusDays(1))));
        }else{
            if(!second.getTimeSlot().getEnd().isAfter(first.getTimeSlot().getEnd()))
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(second.getTimeSlot().getEnd().plusDays(1),first.getTimeSlot().getEnd() )));
        }
        return spliced;
    }

    public static AvailabilitySlot joinSlots(AvailabilitySlot first, AvailabilitySlot second){
        LocalDateTime start = null;
        LocalDateTime end = null;
        AvailabilitySlot joined = new AvailabilitySlot(0D,new TimeSlot(null,null));
        if(first.getPrice().equals(second.getPrice())){
            joined.setPrice(first.getPrice());

            if(first.getTimeSlot().getStart().isBefore(second.getTimeSlot().getStart())) start = first.getTimeSlot().getStart();
            else start = second.getTimeSlot().getStart();

            if(first.getTimeSlot().getEnd().isAfter(second.getTimeSlot().getEnd())) end = first.getTimeSlot().getEnd();
            else end = second.getTimeSlot().getEnd();

            joined.setTimeSlot(new TimeSlot(start,end));
        }
        return joined;
    }

}
