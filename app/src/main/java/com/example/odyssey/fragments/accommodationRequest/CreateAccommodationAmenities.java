package com.example.odyssey.fragments.accommodationRequest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.odyssey.R;
import com.example.odyssey.model.accommodations.AccommodationRequest;
import com.example.odyssey.model.accommodations.Amenity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CreateAccommodationAmenities extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private AccommodationRequest accommodation;
    ArrayList<String> images =new ArrayList<>();
    CheckBox wifi,tv,air,kitchen,parking,beach,washer,spa,bed,smoking;

    Button nextBtn;
    Button backBtn;
    public CreateAccommodationAmenities() {
    }

    public static CreateAccommodationAmenities newInstance(String param1, String param2) {
        CreateAccommodationAmenities fragment = new CreateAccommodationAmenities();
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

        if(getArguments()!= null && getArguments().getSerializable("Request") != null)
            accommodation = (AccommodationRequest) getArguments().getSerializable("Request");

        if(getArguments()!= null && getArguments().getStringArrayList("Images") != null)
            images = getArguments().getStringArrayList("Images");

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
                if(wifi.isChecked()) amenities.add(new Amenity(1L,"Wifi"));
                if(air.isChecked()) amenities.add(new Amenity(2L,"Air conditioning"));
                if(tv.isChecked()) amenities.add(new Amenity(3L,"TV"));
                if(kitchen.isChecked()) amenities.add(new Amenity(4L,"Kitchen"));
                if(parking.isChecked()) amenities.add(new Amenity(5L,"Free parking"));
                if(beach.isChecked()) amenities.add(new Amenity(6L,"Beach view"));
                if(washer.isChecked()) amenities.add(new Amenity(7L, "Washer"));
                if(bed.isChecked()) amenities.add(new Amenity(8L, "King bed"));
                if(smoking.isChecked()) amenities.add(new Amenity(9L, "Smoking"));

                accommodation.setNewAmenities(amenities);


                Bundle args = new Bundle();
                args.putSerializable("Request",accommodation);
                args.putStringArrayList("Images", images);
                Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_images, args);
            }
        });
        backBtn.setOnClickListener(c -> {
            Bundle args = new Bundle();
            args.putSerializable("Request",accommodation);
            args.putStringArrayList("Images", images);
            Navigation.findNavController(requireView()).navigate(R.id.nav_accommodation_create_details, args);
        });
        return v;
    }
}