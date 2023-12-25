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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateAccommodationSlots extends Fragment {

    View v;
    Button nextBtn, addBtn;
    MaterialButton dateBtn;
    TextInputLayout priceInput;
    TextInputEditText priceEdit;
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

        dateBtn.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
            )).build();
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                    date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection.second));
                }
            });

            materialDatePicker.show(getChildFragmentManager(), "tag");
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        addBtn.setOnClickListener(c -> {
            try {
                LocalDateTime starting = LocalDate.parse(date1, formatter).atStartOfDay();
                LocalDateTime ending = LocalDate.parse(date2, formatter).atStartOfDay();
                AvailabilitySlots slots = new AvailabilitySlots(requireContext());
                AvailabilitySlot slot = new AvailabilitySlot(Double.parseDouble(priceEdit.getText().toString()),
                        new TimeSlot(starting, ending));
                accommodation.getNewAvailableSlots().add(slot);
                slots.setSlot(slot);
                LinearLayout layout = v.findViewById(R.id.plsRadiOpet);
                layout.addView(slots);
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
}