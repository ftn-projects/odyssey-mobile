package com.example.odyssey.fragments;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.example.odyssey.utils.SlotUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CreateAccommodationSlots extends Fragment {

    View v;
    Button nextBtn, addBtn;
    MaterialButton dateBtn;
    TextInputLayout priceInput;
    TextInputEditText priceEdit, startEdit, endEdit;
    String date1,date2;
    private AccommodationRequest accommodation;
    ArrayList<String> images = new ArrayList<>();
    public CreateAccommodationSlots() {
    }
    public static CreateAccommodationSlots newInstance(String param1, String param2) {
        CreateAccommodationSlots fragment = new CreateAccommodationSlots();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accommodation = (AccommodationRequest) getArguments().getSerializable("Request");
        images = getArguments().getStringArrayList("Images");
        v = inflater.inflate(R.layout.fragment_create_accommodation_slots, container, false);
        dateBtn = v.findViewById(R.id.selectDateButton);
        addBtn = v.findViewById(R.id.buttonAdd);
        nextBtn = v.findViewById(R.id.buttonNext);
        priceEdit = v.findViewById(R.id.inputEditPrice);
        startEdit = v.findViewById(R.id.inputEditStartText);
        endEdit = v.findViewById(R.id.inputEditEndText);

        dateBtn.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                startEdit.setText(date1);
                endEdit.setText(date2);
            });

            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        addBtn.setOnClickListener(c -> {
            try {
                LocalDateTime starting = LocalDate.parse(date1, formatter).atStartOfDay();
                LocalDateTime ending = LocalDate.parse(date2, formatter).atStartOfDay();
                AvailabilitySlot slot = new AvailabilitySlot(Double.parseDouble(priceEdit.getText().toString()),
                        new TimeSlot(starting, ending));
                LinearLayout layout = v.findViewById(R.id.plsRadiOpet);
                int count = layout.getChildCount();

                for(int i=1;i<count;i++){
                    layout.removeView(layout.getChildAt(i));
                }

                if(addSlots(slot)) accommodation.getNewAvailableSlots().add(slot);

                for(AvailabilitySlot s: accommodation.getNewAvailableSlots()){
                    AvailabilitySlots slots = new AvailabilitySlots(requireContext());
                    slots.setSlot(s);
                    layout.addView(slots);
                }

            }
            catch(DateTimeParseException e){
                e.printStackTrace();
            }
        });

        nextBtn.setOnClickListener(c -> {
            Bundle args = new Bundle();
            args.putSerializable("Request",accommodation);
            args.putStringArrayList("Images", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_map,args);
        });

        return v;
    }

    private boolean addSlots(AvailabilitySlot slot){
        boolean add = true;
        Set<AvailabilitySlot> slotsToRemove = new HashSet<>();

        for (AvailabilitySlot s : new HashSet<>(accommodation.getNewAvailableSlots())) {
            if (s.getTimeSlot().overlaps(slot.getTimeSlot())) {
                add = false;

                if (s.getTimeSlot().equals(slot.getTimeSlot())) {
                    slotsToRemove.add(s);
                    accommodation.getNewAvailableSlots().add(slot);
                } else {
                    if (s.getPrice().equals(slot.getPrice())) {
                        slotsToRemove.add(s);
                        accommodation.getNewAvailableSlots().add(SlotUtils.joinSlots(s, slot));
                    } else {
                        slotsToRemove.add(s);
                        for (AvailabilitySlot a : SlotUtils.splitSlots(s, slot))
                            accommodation.getNewAvailableSlots().add(a);
                    }
                }
            }
        }

        accommodation.getNewAvailableSlots().removeAll(slotsToRemove);
        return add;
    }
}