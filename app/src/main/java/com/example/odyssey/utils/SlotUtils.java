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
        Double price = first.getPrice();
        TimeSlot firstSlot = first.getTimeSlot(), secondSlot = second.getTimeSlot();

        if (firstSlot.getStart().isBefore(secondSlot.getStart()))
            spliced.add(new AvailabilitySlot(price, new TimeSlot(firstSlot.getStart(), secondSlot.getStart().minusDays(1))));

        if (secondSlot.getEnd().isBefore(firstSlot.getEnd()))
            spliced.add(new AvailabilitySlot(price, new TimeSlot(secondSlot.getEnd().plusDays(1), firstSlot.getEnd())));

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
