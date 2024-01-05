package com.example.odyssey.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.TimeSlot;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.AvailabilitySlot;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccommodationSlots extends Fragment {

    View v;
    Button nextBtn, addBtn;
    MaterialButton dateBtn;
    TextInputLayout priceInput;
    TextInputEditText priceEdit;
    String date1,date2;
    private AccommodationRequest accommodation;
    ArrayList<String> images = new ArrayList<>();
    public UpdateAccommodationSlots() {
    }
    public static UpdateAccommodationSlots newInstance(String param1, String param2) {
        UpdateAccommodationSlots fragment = new UpdateAccommodationSlots();
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
            Toast.makeText(requireActivity(), "Accommodation request created", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigate(R.id.nav_home);
        });

        loadAccommodationRequest();

        return v;
    }

    private void loadAccommodationRequest() {
        Long id = 2L;
        ClientUtils.accommodationService.getOne(id).enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(@NonNull Call<Accommodation> call, @NonNull Response<Accommodation> response) {
                if (!response.isSuccessful()) {
                    Log.e("ACCOMMODATION REQUEST", response.message() != null ? response.message() : "error");
                    return;
                }
                updateFormWithData(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Accommodation> call, @NonNull Throwable t) {
                Log.e("ACCOMMODATION REQUEST", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateFormWithData(Accommodation accommodation) {
        for (AvailabilitySlot s : accommodation.getAvailableSlots()) {
            AvailabilitySlots slots = new AvailabilitySlots(requireContext());
            this.accommodation.getNewAvailableSlots().add(s);
            slots.setSlot(s);
            LinearLayout layout = v.findViewById(R.id.plsRadiOpet);
            layout.addView(slots);
        }
    }
}