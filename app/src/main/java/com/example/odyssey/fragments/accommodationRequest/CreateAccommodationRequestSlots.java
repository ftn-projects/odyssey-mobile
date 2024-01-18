package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationRequestSlots extends Fragment {
    private static final String ARG_REQUEST = "Request";
    private AccommodationRequest request = null;
    TextInputLayout priceInput, startInput, endInput;
    TextInputEditText priceEdit, startEdit, endEdit;
    Button nextBtn, addBtn, backBtn;
    MaterialButton dateBtn;
    LinearLayout slotsLayout;
    View v;

    public CreateAccommodationRequestSlots() {
    }

    public static CreateAccommodationRequestSlots newInstance() {
        return new CreateAccommodationRequestSlots();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_REQUEST))
            request = (AccommodationRequest) getArguments().getSerializable(ARG_REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_create_accommodation_slots, container, false);
        initializeElements(v);
        loadData();

        dateBtn.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                startEdit.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first)));
                endEdit.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second)));
            });

            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        addBtn.setOnClickListener(c -> {
            if (!validFields()) return;

            try {
                LocalDateTime start = LocalDate.parse(startEdit.getText().toString(), formatter).atStartOfDay();
                LocalDateTime end = LocalDate.parse(endEdit.getText().toString(), formatter).atStartOfDay();
                Double price = Double.parseDouble(priceEdit.getText().toString());

                addSlot(new AvailabilitySlot(price, new TimeSlot(start, end)));

                slotsLayout.removeAllViews();
                loadData();
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        });

        nextBtn.setOnClickListener(c -> navigateTo(R.id.nav_accommodation_create_images));
        backBtn.setOnClickListener(c -> navigateTo(R.id.nav_accommodation_create_amenities));
        return v;
    }

    private void navigateTo(int id) {
        Bundle args = new Bundle();
        args.putSerializable("Request", request);
        Navigation.findNavController(requireView()).navigate(id, args);
    }

    private void loadData() {
        request.getNewAvailableSlots().forEach(s ->
                slotsLayout.addView(new AvailabilitySlots(s, requireContext())));
    }

    private void initializeElements(View v) {
        dateBtn = v.findViewById(R.id.selectDateButton);
        addBtn = v.findViewById(R.id.buttonAdd);
        nextBtn = v.findViewById(R.id.buttonCreate);
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

        slotsLayout = v.findViewById(R.id.plsRadiOpet);
    }

    private boolean validFields() {
        return Validation.validateDouble(priceInput, priceEdit, requireActivity().getWindow()) &&
                Validation.validateEmpty(startInput, startEdit, requireActivity().getWindow()) &&
                Validation.validateEmpty(endInput, endEdit, requireActivity().getWindow());
    }

    private void addSlot(AvailabilitySlot slot) {
        Set<AvailabilitySlot> newSlots = new HashSet<>();
        Set<AvailabilitySlot> overlapping = request.getNewAvailableSlots().stream()
                .filter(s -> s.getTimeSlot().overlaps(slot.getTimeSlot()))
                .collect(Collectors.toSet());

        overlapping.forEach(s -> newSlots.addAll(SlotUtils.splitSlots(s, slot)));

        request.getNewAvailableSlots().removeAll(overlapping);
        request.getNewAvailableSlots().addAll(newSlots);
        request.getNewAvailableSlots().add(slot);

        newSlots.clear();
        List<AvailabilitySlot> toBeJoined = new ArrayList<>();
        for (AvailabilitySlot s : sorted(request.getNewAvailableSlots())) {
            if (!toBeJoined.isEmpty() && !isSuccessive(toBeJoined.get(toBeJoined.size() - 1), s)) {
                newSlots.add(SlotUtils.joinSlots(toBeJoined));
                toBeJoined.clear();
            }
            toBeJoined.add(s);
        }

        if (!toBeJoined.isEmpty())
            newSlots.add(SlotUtils.joinSlots(toBeJoined));

        request.getNewAvailableSlots().clear();
        request.getNewAvailableSlots().addAll(sorted(newSlots));

        startEdit.setText("");
        endEdit.setText("");
        priceEdit.setText("");
    }

    private boolean isSuccessive(AvailabilitySlot first, AvailabilitySlot second) {
        return first.getPrice().equals(second.getPrice()) &&
                first.getTimeSlot().getEnd().plusDays(1).equals(second.getTimeSlot().getStart());
    }

    private List<AvailabilitySlot> sorted(Collection<AvailabilitySlot> slots) {
        return slots.stream().sorted(Comparator.comparing(s -> s.getTimeSlot().getStart()))
                .collect(Collectors.toList());
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
            if (view.getId() == R.id.inputEditPrice)
                Validation.validateDouble(priceInput, priceEdit, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditStartText)
                Validation.validateEmpty(startInput, startEdit, requireActivity().getWindow());
            else if (view.getId() == R.id.inputEditEndText)
                Validation.validateEmpty(endInput, endEdit, requireActivity().getWindow());
        }
    }
}