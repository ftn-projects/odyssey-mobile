package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.odyssey.R;
import com.example.odyssey.fragments.AvailabilitySlots;
import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.example.odyssey.utils.SlotUtils;
import com.example.odyssey.utils.Validation;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationSlots extends Fragment {

    View v;
    Button nextBtn, addBtn, backBtn;
    MaterialButton dateBtn;
    TextInputLayout priceInput, startInput, endInput;
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

        if(getArguments()!= null && getArguments().getSerializable("Request") != null)
            accommodation = (AccommodationRequest) getArguments().getSerializable("Request");

        if(getArguments()!= null && getArguments().getStringArrayList("Images") != null)
            images = getArguments().getStringArrayList("Images");

        v = inflater.inflate(R.layout.fragment_create_accommodation_slots, container, false);
        dateBtn = v.findViewById(R.id.selectDateButton);
        addBtn = v.findViewById(R.id.buttonAdd);
        nextBtn = v.findViewById(R.id.buttonNext);
        backBtn = v.findViewById(R.id.buttonBack);

        priceEdit = v.findViewById(R.id.inputEditPrice);
        priceInput = v.findViewById(R.id.inputPrice);
        priceEdit.addTextChangedListener(new ValidationTextWatcher(priceEdit));

        startEdit = v.findViewById(R.id.inputEditStartText);
        startInput = v.findViewById(R.id.inputStartText);
        startEdit.addTextChangedListener(new ValidationTextWatcher(startEdit));

        endEdit = v.findViewById(R.id.inputEditEndText);
        endInput = v.findViewById(R.id.inputEndText);
        endEdit.addTextChangedListener(new ValidationTextWatcher(endEdit));

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
            if(!Validation.validateDouble(priceInput, priceEdit, requireActivity().getWindow()) ||
                    !Validation.validateEmpty(startInput, startEdit, requireActivity().getWindow()) ||
                    !Validation.validateEmpty(endInput, endEdit, requireActivity().getWindow()))
                return;

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

        backBtn.setOnClickListener(c -> {
            Bundle args = new Bundle();
            args.putSerializable("Request", accommodation);
            args.putStringArrayList("Images", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_images, args);
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

    private class ValidationTextWatcher implements TextWatcher {
        private final View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(view.getId() == R.id.inputEditPrice)
                Validation.validateDouble(priceInput, priceEdit, requireActivity().getWindow());
            else if(view.getId() == R.id.inputEditStartText)
                Validation.validateEmpty(startInput, startEdit, requireActivity().getWindow());
            else if(view.getId() == R.id.inputEditEndText)
                Validation.validateEmpty(endInput, endEdit, requireActivity().getWindow());
        }
    }
}