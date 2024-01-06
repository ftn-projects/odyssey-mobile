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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationSlots extends Fragment {

    View v;
    Button nextBtn, addBtn;
    MaterialButton dateBtn;
    TextInputLayout priceInput;
    TextInputEditText priceEdit, startEdit, endEdit;
    String date1, date2;
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
                layout.removeAllViews();

                addSlot(slot);

                for (AvailabilitySlot s : accommodation.getNewAvailableSlots()) {
                    AvailabilitySlots slots = new AvailabilitySlots(requireContext());
                    slots.setSlot(s);
                    layout.addView(slots);
                }
                priceEdit.setText("");
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        });

        nextBtn.setOnClickListener(c -> {
            Bundle args = new Bundle();
            args.putSerializable("Request", accommodation);
            args.putStringArrayList("Images", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_map, args);
        });

        return v;
    }

    private void addSlot(AvailabilitySlot slot) {
        Set<AvailabilitySlot> newSlots = new HashSet<>();
        Set<AvailabilitySlot> overlapping = accommodation.getNewAvailableSlots().stream()
                .filter(s -> s.getTimeSlot().overlaps(slot.getTimeSlot()))
                .collect(Collectors.toSet());

        overlapping.forEach(s -> newSlots.addAll(SlotUtils.splitSlots(s, slot)));

        accommodation.getNewAvailableSlots().removeAll(overlapping);
        accommodation.getNewAvailableSlots().addAll(newSlots);
        accommodation.getNewAvailableSlots().add(slot);

        newSlots.clear();
        List<AvailabilitySlot> toBeJoined = new ArrayList<>();
        for (AvailabilitySlot s : sorted(accommodation.getNewAvailableSlots())) {
            if (!toBeJoined.isEmpty() && !isSuccessive(toBeJoined.get(toBeJoined.size() - 1), s)) {
                newSlots.add(SlotUtils.joinSlots(toBeJoined));
                toBeJoined.clear();
            }
            toBeJoined.add(s);
        }

        if (!toBeJoined.isEmpty())
            newSlots.add(SlotUtils.joinSlots(toBeJoined));

        accommodation.getNewAvailableSlots().clear();
        accommodation.getNewAvailableSlots().addAll(sorted(newSlots));
    }

    private boolean isSuccessive(AvailabilitySlot first, AvailabilitySlot second) {
        return first.getPrice().equals(second.getPrice()) &&
                first.getTimeSlot().getEnd().plusDays(1).equals(second.getTimeSlot().getStart());
    }

    private List<AvailabilitySlot> sorted(Collection<AvailabilitySlot> slots) {
        return slots.stream().sorted(
                (s1, s2) -> s1.getTimeSlot().getStart().compareTo(s2.getTimeSlot().getStart())
        ).collect(Collectors.toList());
    }
}