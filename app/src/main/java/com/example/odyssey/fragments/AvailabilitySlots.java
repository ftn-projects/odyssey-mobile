package com.example.odyssey.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AvailabilitySlot;

import java.time.format.DateTimeFormatter;

public class AvailabilitySlots extends LinearLayout {

    TextView start, end, price;

    public AvailabilitySlots(AvailabilitySlot slots, Context context) {
        super(context);
        initializeElements();

        start.setText(slots.getTimeSlot().getStart().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        end.setText(slots.getTimeSlot().getEnd().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        price.setText(String.valueOf(slots.getPrice()));
    }

    private void initializeElements() {
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_availability_slots2, this, true);
        start = findViewById(R.id.startDate);
        end = findViewById(R.id.endDate);
        price = findViewById(R.id.price);
    }
}