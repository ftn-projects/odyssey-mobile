package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.activities.MainActivity;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationRequestSlots extends Fragment {
    private static final String ARG_REQUEST = "request";
    private AccommodationRequest request = null;
    TextInputLayout priceInput, startInput, endInput;
    TextInputEditText priceEdit, startEdit, endEdit;
    Button nextBtn, addBtn, backBtn;
    MaterialButton dateBtn;
    LinearLayout slotsLayout;
    View v;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDateTime start, end;
    Double price;

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
        v = inflater.inflate(R.layout.fragment_create_accommodation_request_slots, container, false);
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

        addBtn.setOnClickListener(c -> {
            if (!validFields()) return;

            try {
                collectData();

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
        if (request.getNewAvailableSlots().size() == 0) {
            Toast.makeText(requireActivity(), "Please add at least one slot", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putSerializable(ARG_REQUEST, request);
        Navigation.findNavController(requireView()).navigate(id, args);
    }

    private void loadData() {
        request.getNewAvailableSlots().forEach(s ->
                slotsLayout.addView(new AvailabilitySlots(s, requireContext())));
    }

    private void collectData() {
        start = LocalDate.parse(Objects.requireNonNull(
                startEdit.getText()).toString(), formatter).atStartOfDay();
        end = LocalDate.parse(Objects.requireNonNull(
                endEdit.getText()).toString(), formatter).atStartOfDay();
        price = Double.parseDouble(Objects.requireNonNull(
                priceEdit.getText()).toString());
    }

    private void initializeElements(View v) {
        dateBtn = v.findViewById(R.id.selectDateButton);
        addBtn = v.findViewById(R.id.buttonAdd);
        nextBtn = v.findViewById(R.id.buttonCreate);
        backBtn = v.findViewById(R.id.buttonBack);

        priceEdit = v.findViewById(R.id.inputEditPrice);
        priceInput = v.findViewById(R.id.inputPrice);

        startEdit = v.findViewById(R.id.inputEditStartText);
        startInput = v.findViewById(R.id.inputStartText);

        endEdit = v.findViewById(R.id.inputEditEndText);
        endInput = v.findViewById(R.id.inputEndText);

        slotsLayout = v.findViewById(R.id.plsRadiOpet);

        ((MainActivity) requireActivity()).setActionBarTitle(
                request.getAccommodationId() == null ? "Create accommodation" : "Edit accommodation"
        );
    }

    private boolean validFields() {
        return Validation.validateDouble(priceEdit) &&
                Validation.validateEmpty(startEdit) &&
                Validation.validateEmpty(endEdit);
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
    }

    private boolean isSuccessive(AvailabilitySlot first, AvailabilitySlot second) {
        return first.getPrice().equals(second.getPrice()) &&
                first.getTimeSlot().getEnd().plusDays(1).equals(second.getTimeSlot().getStart());
    }

    private List<AvailabilitySlot> sorted(Collection<AvailabilitySlot> slots) {
        return slots.stream().sorted(Comparator.comparing(s -> s.getTimeSlot().getStart()))
                .collect(Collectors.toList());
    }
}