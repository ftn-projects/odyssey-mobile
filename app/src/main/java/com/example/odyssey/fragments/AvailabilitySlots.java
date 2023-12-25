package com.example.odyssey.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AvailabilitySlot;

import java.time.format.DateTimeFormatter;

public class AvailabilitySlots extends LinearLayout {

    TextView start,end,price;
    public AvailabilitySlots(Context context) {
        super(context);
        init();
    }

    public void setSlot(AvailabilitySlot slots){
        start.setText(slots.getTimeSlot().getStart().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        end.setText(slots.getTimeSlot().getEnd().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        price.setText(slots.getPrice().toString());
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_availability_slots2, this, true);

        start = findViewById(R.id.startDate);
        end = findViewById(R.id.endDate);
        price = findViewById(R.id.price);
    }
}