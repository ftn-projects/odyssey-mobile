package com.example.odyssey.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccommodationAmenities extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private AccommodationRequest accommodation;
    CheckBox wifi, tv, air, kitchen, parking, beach, washer, spa, bed, smoking;

    Button nextBtn;
    Button backBtn;

    public UpdateAccommodationAmenities() {
    }

    public static UpdateAccommodationAmenities newInstance(String param1, String param2) {
        UpdateAccommodationAmenities fragment = new UpdateAccommodationAmenities();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_accommodation_amenities, container, false);
        accommodation = (AccommodationRequest) getArguments().getSerializable("Request");
        nextBtn = v.findViewById(R.id.buttonNext);
        backBtn = v.findViewById(R.id.buttonBack);
        wifi = v.findViewById(R.id.checkWifi);
        air = v.findViewById(R.id.checkAirConditioning);
        tv = v.findViewById(R.id.checkTV);
        kitchen = v.findViewById(R.id.checkKitchen);
        parking = v.findViewById(R.id.checkParking);
        beach = v.findViewById(R.id.checkBeach);
        washer = v.findViewById(R.id.checkWasher);
        spa = v.findViewById(R.id.checkSpa);
        bed = v.findViewById(R.id.checkKingBed);
        smoking = v.findViewById(R.id.checkSmoking);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<Amenity> amenities = new HashSet<>();
                if (wifi.isChecked()) amenities.add(new Amenity(1L, "Wifi"));
                if (air.isChecked()) amenities.add(new Amenity(2L, "Air conditioning"));
                if (tv.isChecked()) amenities.add(new Amenity(3L, "TV"));
                if (kitchen.isChecked()) amenities.add(new Amenity(4L, "Kitchen"));
                if (parking.isChecked()) amenities.add(new Amenity(5L, "Free parking"));
                if (beach.isChecked()) amenities.add(new Amenity(6L, "Beach view"));
                if (washer.isChecked()) amenities.add(new Amenity(7L, "Washer"));
                if (bed.isChecked()) amenities.add(new Amenity(8L, "King bed"));
                if (smoking.isChecked()) amenities.add(new Amenity(9L, "Smoking"));

                accommodation.setNewAmenities(amenities);


                Bundle args = new Bundle();
                args.putSerializable("Request", accommodation);
                Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_update_slots, args);
            }
        });
        loadAccommodationRequest();

        backBtn.setOnClickListener(c -> Navigation.findNavController(requireActivity(), R.id.fragment_container_main).navigate(R.id.nav_accommodation_update_details));
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
        List<String> amenities = accommodation.getAmenities().stream().map(Amenity::getTitle).collect(Collectors.toList());
        wifi.setChecked(amenities.contains("WiFi"));
        air.setChecked(amenities.contains("Air conditioning"));
        tv.setChecked(amenities.contains("TV"));
        kitchen.setChecked(amenities.contains("Kitchen"));
        parking.setChecked(amenities.contains("Free parking"));
        beach.setChecked(amenities.contains("Beach access"));
        washer.setChecked(amenities.contains("Washer"));
        spa.setChecked(amenities.contains("Spa"));
        bed.setChecked(amenities.contains("King bed"));
        smoking.setChecked(amenities.contains("Smoking room"));
    }
}