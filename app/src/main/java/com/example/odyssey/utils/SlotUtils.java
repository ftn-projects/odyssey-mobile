package com.example.odyssey.utils;

import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.accommodations.AvailabilitySlot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SlotUtils {
    public static Collection<AvailabilitySlot> splitSlots(AvailabilitySlot first, AvailabilitySlot second) {
        Collection<AvailabilitySlot> spliced = new ArrayList<>();
        spliced.add(second);
        if (first.getTimeSlot().getStart().isBefore(second.getTimeSlot().getStart())) {
            if (second.getTimeSlot().getEnd().isBefore(first.getTimeSlot().getEnd())) {
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(first.getTimeSlot().getStart(), second.getTimeSlot().getStart().minusDays(1))));
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(second.getTimeSlot().getEnd().plusDays(1), first.getTimeSlot().getEnd())));
            } else
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(first.getTimeSlot().getStart(), second.getTimeSlot().getStart().minusDays(1))));
        } else {
            if (!second.getTimeSlot().getEnd().isAfter(first.getTimeSlot().getEnd()))
                spliced.add(new AvailabilitySlot(first.getPrice(), new TimeSlot(second.getTimeSlot().getEnd().plusDays(1), first.getTimeSlot().getEnd())));
        }
        return spliced;
    }

    public static AvailabilitySlot joinSlots(List<AvailabilitySlot> slots) {
        if (slots.size() == 1) return slots.get(0);

        LocalDateTime start, end;
        start = slots.get(0).getTimeSlot().getStart();
        end = slots.get(slots.size() - 1).getTimeSlot().getEnd();
        return new AvailabilitySlot(slots.get(0).getPrice(), new TimeSlot(start, end));
    }

}
