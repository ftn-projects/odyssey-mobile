package com.example.odyssey.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.odyssey.R;
import com.example.odyssey.clients.ClientUtils;
import com.example.odyssey.model.accommodations.Accommodation;
import com.example.odyssey.model.accommodations.Amenity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterPopupDialog extends DialogFragment{

    private FilterDialogListener filterDialogListener;
    public void setFilterDialogListener(FilterDialogListener filterDialogListener) {
        this.filterDialogListener = filterDialogListener;
    }

    private ArrayList<Amenity> amenities = new ArrayList<>();

    private float startPrice;
    private float endPrice;

    Accommodation.Type type;

    public FilterPopupDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.popup_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RangeSlider rangeSlider = view.findViewById(R.id.priceRangeSliderFilter);
        TextView priceRangeStart = view.findViewById(R.id.priceStartFilterEditText);
        TextView priceRangeEnd = view.findViewById(R.id.priceEndFilterEditText);
        AutoCompleteTextView typeFilter = view.findViewById(R.id.accommodationTypeFilter);

        typeFilter.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedType = (String) parent.getItemAtPosition(position);
            switch (selectedType) {
                case "Apartment":
                    type = Accommodation.Type.APARTMENT;
                    break;
                case "Room":
                    type = Accommodation.Type.ROOM;
                    break;
                case "House":
                    type = Accommodation.Type.HOUSE;
                    break;
                default:
                    type = null;
                    break;
            }
        });

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                startPrice = values.get(0);
                endPrice = values.get(1);
                priceRangeStart.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(startPrice)));
                priceRangeEnd.setText(MessageFormat.format("{0}", NumberFormat.getCurrencyInstance().format(endPrice)));
            }
        });

        rangeSlider.setValues(0f, 1500f);

        MaterialButton closeButton = view.findViewById(R.id.filter_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        MaterialButton applyButton = view.findViewById(R.id.filter_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialogListener.onFilterApplied(getSelectedAmenities(), startPrice, endPrice, type);
                dismiss();
            }
        });

        getDataFromClient();

        LinearLayout amenitiesContainer = view.findViewById(R.id.amenities_container);

    }

    private void getDataFromClient(){
        Call<ArrayList<Amenity>> call = ClientUtils.amenityService.getAll();
        call.enqueue(new Callback<ArrayList<Amenity>>() {
            @Override
            public void onResponse(Call<ArrayList<Amenity>> call, Response<ArrayList<Amenity>> response) {
                if (response.code() == 200){
                    Log.d("REZ","Meesage recieved");
                    amenities = response.body();
                    populateCheckBoxes();

                }else{
                    Log.e("REZ","Meesage recieved: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Amenity>> call, Throwable t) {
                Log.e("REZ", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void populateCheckBoxes() {
        LinearLayout amenitiesContainer = requireView().findViewById(R.id.amenities_container);

        if (amenities != null) {
            for (Amenity amenity : amenities) {
                Typeface montserratTypeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular);
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setId(View.generateViewId());
                checkBox.setText(amenity.getTitle());
                checkBox.setTag(amenity);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 15, 0, 15);
                checkBox.setLayoutParams(layoutParams);
                checkBox.setTypeface(montserratTypeface);


                amenitiesContainer.addView(checkBox);
            }
        } else {
            Log.d("REZ", "Amenities is null");
        }
    }

    private List<Amenity> getSelectedAmenities() {
        List<Amenity> selectedAmenities = new ArrayList<>();

        LinearLayout amenitiesContainer = requireView().findViewById(R.id.amenities_container);

        for (int i = 0; i < amenitiesContainer.getChildCount(); i++) {
            View childView = amenitiesContainer.getChildAt(i);
            if (childView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) childView;
                if (checkBox.isChecked()) {
                    selectedAmenities.add((Amenity) checkBox.getTag());
                    Log.d("REZ", "Selected amenity: " + checkBox.getText().toString());
                }
            }
        }

        return selectedAmenities;
    }


    public interface FilterDialogListener {
        void onFilterApplied(List<Amenity> selectedAmenities, float startPrice, float endPrice, Accommodation.Type type);
    }
}