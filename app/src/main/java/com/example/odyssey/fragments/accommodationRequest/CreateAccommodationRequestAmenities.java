package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationRequestAmenities extends Fragment {
    private static final String ARG_REQUEST = "Request";
    private AccommodationRequest request = null;
    CheckBox wifi, tv, air, kitchen, parking, beach, washer, spa, bed, smoking;
    Button nextBtn;
    Button backBtn;

    public CreateAccommodationRequestAmenities() {
    }

    public static CreateAccommodationRequestAmenities newInstance() {
        return new CreateAccommodationRequestAmenities();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_REQUEST))
            request = (AccommodationRequest) getArguments().getSerializable(ARG_REQUEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_accommodation_amenities, container, false);
        initializeElements(v);
        loadData();

        nextBtn.setOnClickListener(v1 -> navigateTo(R.id.nav_accommodation_create_slots));
        backBtn.setOnClickListener(c -> navigateTo(R.id.nav_accommodation_create_map));
        return v;
    }

    private void navigateTo(int id) {
        collectData();
        Bundle args = new Bundle();
        args.putSerializable("Request", request);
        Navigation.findNavController(requireView()).navigate(id, args);
    }

    private void initializeElements(View v) {
        nextBtn = v.findViewById(R.id.buttonCreate);
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
    }

    private void loadData() {
        Set<String> amenities = request.getNewAmenities().stream()
                .map(Amenity::getTitle).collect(Collectors.toSet());

        if (amenities.contains("Wifi")) wifi.setChecked(true);
        if (amenities.contains("Air conditioning")) air.setChecked(true);
        if (amenities.contains("TV")) tv.setChecked(true);
        if (amenities.contains("Kitchen")) kitchen.setChecked(true);
        if (amenities.contains("Free parking")) parking.setChecked(true);
        if (amenities.contains("Beach view")) beach.setChecked(true);
        if (amenities.contains("Washer")) washer.setChecked(true);
        if (amenities.contains("Spa")) spa.setChecked(true);
        if (amenities.contains("King bed")) bed.setChecked(true);
        if (amenities.contains("Smoking")) smoking.setChecked(true);
    }

    private void collectData() {
        Set<Amenity> amenities = new HashSet<>();
        if (wifi.isChecked()) amenities.add(new Amenity( 1L, "Wifi"));
        if (air.isChecked()) amenities.add(new Amenity(2L, "Air conditioning"));
        if (tv.isChecked()) amenities.add(new Amenity(3L, "TV"));
        if (kitchen.isChecked()) amenities.add(new Amenity(4L, "Kitchen"));
        if (parking.isChecked()) amenities.add(new Amenity( 5L, "Free parking"));
        if (beach.isChecked()) amenities.add(new Amenity(6L, "Beach view"));
        if (washer.isChecked()) amenities.add(new Amenity(7L, "Washer"));
        if (spa.isChecked()) amenities.add(new Amenity(8L, "Spa"));
        if (bed.isChecked()) amenities.add(new Amenity(9L, "King bed"));
        if (smoking.isChecked()) amenities.add(new Amenity(10L, "Smoking"));
        request.setNewAmenities(amenities);
    }
}